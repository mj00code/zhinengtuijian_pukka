package com.ubo.iptv.recommend.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ubo.iptv.core.constant.RedisConstant;
import com.ubo.iptv.entity.gdgd.MediaDTO;
import com.ubo.iptv.entity.gdgd.SceneDTO;
import com.ubo.iptv.entity.gdgd.recommend.RecommendEngineDTO;
import com.ubo.iptv.mybatis.recommend.entity.StrategyMediaTypeDO;
import com.ubo.iptv.recommend.entity.CollaborativeFiterResponse;
import com.ubo.iptv.recommend.entity.CollaborativeFiterResult;
import com.ubo.iptv.recommend.enums.MediaShowEnum;
import com.ubo.iptv.recommend.service.RedisService;
import com.ubo.iptv.recommend.service.UserBehaviorRecommendService;
import com.ubo.iptv.recommend.util.CalculateCoreUtil;
import com.ubo.iptv.recommend.util.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 历史协同过滤推荐
 * Created by uboo on 2019/6/17.
 */
@Service("UserBehaviorCollaborativeFilterService")
@Slf4j
public class UserBehaviorCollaborativeFilterService implements UserBehaviorRecommendService {
    @Autowired
    RedisService redisService;
    @Value("${recommend.collaborativeFilter.item2itemUrl}")
    private String item2itemUrl;
    @Value("${recommend.collaborativeFilter.user2itemUrl}")
    private String user2itemUrl;

    private boolean setRedis(String key, String searchResult, RecommendEngineDTO recommendEngine) {
        //媒资详情没有推荐过的情况
        if (!redisService.exists(key)) {
            //保存到redis数据
            CollaborativeFiterResult cfResult = new CollaborativeFiterResult();
            //结果有错误的情况
            if (searchResult.contains("Error")) {
                cfResult.setStatus(500);
                redisService.set(key, JSONObject.toJSONString(cfResult), 10 * 60L);
                return true;
            }
            //返回结果异常的情况
            CollaborativeFiterResponse response = JSONObject.parseObject(searchResult, CollaborativeFiterResponse.class);
            if (200 != response.getStatus()) {
                cfResult.setStatus(500);
                redisService.set(key, JSONObject.toJSONString(cfResult), 10 * 60L);
                return true;
            }
            //获取结果
            List<String> dataList = response.getData();
            //数量不够则退出
            if (recommendEngine.getSize() > dataList.size()) {
                cfResult.setStatus(500);
                redisService.set(key, JSONObject.toJSONString(cfResult), 60 * 60L);
                return true;
            }
            //获取mediaDto,并且保存
            List<String> medias = new ArrayList<>();
            for (String s : dataList) {
                Integer resultMediaId = Integer.parseInt(s.split("_")[0]);
                String mediaInfoString = redisService.get(String.format(RedisConstant.MEDIA_KEY, resultMediaId));
                if (StringUtils.isNotEmpty(mediaInfoString)) {
                    medias.add(mediaInfoString);
                }
            }
            cfResult.setStatus(200);
            cfResult.setMedias(medias);
            redisService.set(key, JSONObject.toJSONString(cfResult), 5 * 24 * 60 * 60L);
        }
        return false;
    }

    @Override
    public LinkedHashMap recommendAndSort(LinkedHashMap resultMap, Integer userId, String mediaCode, SceneDTO sceneDTO, LinkedHashMap<String, String> excludeMap, RecommendEngineDTO recommendEngine, Boolean isColdBoot) {

        String result = "";
        //判定是否有缓存信息,没有则放到缓存,有的话则取缓存的值
        //媒资详情的推荐
        if (StringUtils.isNotEmpty(mediaCode)) {
            String cfMediaKey = "cf_mediaId_" + mediaCode;
            if (!redisService.exists(cfMediaKey)) {
                String url = item2itemUrl;
                String searchResult = HttpClientUtil.doGet(new StringBuffer().append(url).append("?programId=").append(mediaCode).append("&size=").append("1000").toString());
                //设置缓存
                if (setRedis(cfMediaKey, searchResult, recommendEngine)) return resultMap;
            }
            result = redisService.get(cfMediaKey);
        } else {
            //用户协同过滤推荐
            String cfUserKey = "cf_userId_" + userId;
            if (!redisService.exists(cfUserKey)) {
                String url = user2itemUrl;
                String searchResult = HttpClientUtil.doGet(new StringBuffer().append(url).append("?userId=").append(userId.toString()).append("&size=").append("1000").toString());
                if (setRedis(cfUserKey, searchResult, recommendEngine)) return resultMap;
            }
            result = redisService.get(cfUserKey);
        }
        //解析result
        CollaborativeFiterResult collaborativeFiterResult = JSONObject.parseObject(result, CollaborativeFiterResult.class);
        if (200 != collaborativeFiterResult.getStatus()) return resultMap;
        //获取推荐数量
        List<StrategyMediaTypeDO> mediaTypeDOList = sceneDTO.getRecommendStrategyDetail().getStrategyMediaTypeList();
        Integer chargeSize = CalculateCoreUtil.calculateChargeFreeSize(sceneDTO, recommendEngine);
        //设置收费的
        int realChargeSize = 0;
        for (String mediaInfoString : collaborativeFiterResult.getMedias()) {
            if (realChargeSize >= chargeSize) {
                break;
            }
            JSONObject mediaInfo = JSONObject.parseObject(mediaInfoString);
            MediaDTO media = JSONObject.parseObject(mediaInfoString, MediaDTO.class);
            if (excludeMap.containsKey(media.getMediaId().toString())) {
                break;
            }
            if (!media.getFree()) {
                for (StrategyMediaTypeDO strategyMediaTypeDO : mediaTypeDOList) {
                    if (strategyMediaTypeDO.getMediaType().equals(media.getMediaType()) && 1 == media.getPublishStatus()) {
                        Float score = mediaInfo.getFloat("strategyScore" + strategyMediaTypeDO.getStrategyId());
                        resultMap.put(media.getMediaId(), CalculateCoreUtil.getIPTVMediaVO(media.getMediaId(), media, MediaShowEnum.IR_COLLABORATIVE.intValue(), sceneDTO.getSceneDO().getSysId(), score));
                        realChargeSize++;
                        break;
                    }
                }
            }

        }

        //设置免费的
        int realFreeSize = 0;
        for (String mediaInfoString : collaborativeFiterResult.getMedias()) {
            if (realFreeSize >= recommendEngine.getSize() - realChargeSize) {
                break;
            }
            JSONObject mediaInfo = JSONObject.parseObject(mediaInfoString);
            MediaDTO media = JSONObject.parseObject(mediaInfoString, MediaDTO.class);
            if (excludeMap.containsKey(media.getMediaId().toString())) {
                break;
            }
            if (media.getFree()) {
                for (StrategyMediaTypeDO strategyMediaTypeDO : mediaTypeDOList) {
                    if (strategyMediaTypeDO.getMediaType().equals(media.getMediaType()) && 1 == media.getPublishStatus()) {
                        Float score = mediaInfo.getFloat("strategyScore" + strategyMediaTypeDO.getStrategyId());
                        resultMap.put(media.getMediaId(), CalculateCoreUtil.getIPTVMediaVO(media.getMediaId(), media, MediaShowEnum.IR_COLLABORATIVE.intValue(), sceneDTO.getSceneDO().getSysId(), score));
                        realFreeSize++;
                        break;
                    }
                }

            }
        }

        //免费的不够,则补充收费的
        int insertChargeSize = 0;
        for (String mediaInfoString : collaborativeFiterResult.getMedias()) {
            if (insertChargeSize >= recommendEngine.getSize() - realChargeSize - realFreeSize) {
                break;
            }
            JSONObject mediaInfo = JSONObject.parseObject(mediaInfoString);
            MediaDTO media = JSONObject.parseObject(mediaInfoString, MediaDTO.class);
            if (excludeMap.containsKey(media.getMediaId().toString())) {
                break;
            }
            if (!media.getFree()) {
                for (StrategyMediaTypeDO strategyMediaTypeDO : mediaTypeDOList) {
                    if (strategyMediaTypeDO.getMediaType().equals(media.getMediaType()) && 1 == media.getPublishStatus()) {
                        Float score = mediaInfo.getFloat("strategyScore" + strategyMediaTypeDO.getStrategyId());
                        resultMap.put(media.getMediaId(), CalculateCoreUtil.getIPTVMediaVO(media.getMediaId(), media, MediaShowEnum.IR_COLLABORATIVE.intValue(), sceneDTO.getSceneDO().getSysId(), score));
                        insertChargeSize++;
                        break;
                    }
                }

            }
        }

        return resultMap;
    }
}
