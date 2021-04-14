package com.ubo.iptv.recommend.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ubo.iptv.core.constant.RedisConstant;
import com.ubo.iptv.entity.gdgd.MediaDTO;
import com.ubo.iptv.entity.gdgd.SceneDTO;
import com.ubo.iptv.entity.gdgd.StarESSearchDTO;
import com.ubo.iptv.entity.gdgd.recommend.RecommendEngineDTO;
import com.ubo.iptv.recommend.enums.MediaChargeFreeEnum;
import com.ubo.iptv.recommend.enums.MediaShowEnum;
import com.ubo.iptv.recommend.service.RedisService;
import com.ubo.iptv.recommend.service.SearchService;
import com.ubo.iptv.recommend.service.UserBehaviorRecommendService;
import com.ubo.iptv.recommend.util.CalculateCoreUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 历史明星推荐
 * Created by uboo on 2019/6/17.
 */
@Service("UserBehaviorStarService")
@Slf4j
public class UserBehaviorStarService implements UserBehaviorRecommendService {
    @Autowired
    RedisService redisService;
    @Autowired
    SearchService searchService;

    @Override
    public LinkedHashMap recommendAndSort(LinkedHashMap resultMap, Integer userId, String mediaCode, SceneDTO sceneDTO, LinkedHashMap<String, String> excludeMap, RecommendEngineDTO recommendEngine, Boolean isColdBoot) {
        //获取明星TOP列表
        Integer chargeSize = CalculateCoreUtil.calculateChargeFreeSize(sceneDTO, recommendEngine);
        String topActorString = redisService.get(RedisConstant.MEDIA_ACTOR_TOP5);
        Map<Integer, List<String>> starMap = JSONObject.parseObject(topActorString, HashMap.class);
        LinkedHashMap allExcludeMap = new LinkedHashMap();
        allExcludeMap.putAll(resultMap);
        allExcludeMap.putAll(excludeMap);
        List<StarESSearchDTO> starEsSearchDTOList = searchService.findMediaByActors(sceneDTO, allExcludeMap, starMap, isColdBoot, recommendEngine.getSize());
        //数量不够则退出
        if (recommendEngine.getSize() > starEsSearchDTOList.size()) {
            return resultMap;
        }
        //设置收费的
        int realChargeSize = 0;
        for (StarESSearchDTO dto : starEsSearchDTOList) {
            if (realChargeSize >= chargeSize) {
                break;
            }
            if (MediaChargeFreeEnum.CHARGE.intValue().equals(dto.getIsCharge())) {
                String mediaInfoString = redisService.get(String.format(RedisConstant.MEDIA_KEY, dto.getMediaId()));
                if (StringUtils.isNotEmpty(mediaInfoString)) {
                    MediaDTO media = JSONObject.parseObject(mediaInfoString, MediaDTO.class);
                    JSONObject mediaInfo = JSONObject.parseObject(mediaInfoString);
                    Float score = mediaInfo.getFloat("strategyScore" + sceneDTO.getRecommendStrategyDetail().getStrategyDO().getId());
                    resultMap.put(dto.getMediaId(), CalculateCoreUtil.getIPTVMediaVO(dto.getMediaId(), media, MediaShowEnum.IR_STAR.intValue(), sceneDTO.getSceneDO().getSysId(), score));
                    realChargeSize++;
                }
            }
        }

        //设置免费的
        int realFreeSize = 0;
        for (StarESSearchDTO dto : starEsSearchDTOList) {
            if (realFreeSize >= recommendEngine.getSize() - realChargeSize) {
                break;
            }
            if (MediaChargeFreeEnum.FREE.intValue().equals(dto.getIsCharge())) {
                String mediaInfoString = redisService.get(String.format(RedisConstant.MEDIA_KEY, dto.getMediaId()));
                if (StringUtils.isNotEmpty(mediaInfoString)) {
                    MediaDTO media = JSONObject.parseObject(mediaInfoString, MediaDTO.class);
                    JSONObject mediaInfo = JSONObject.parseObject(mediaInfoString);
                    Float score = mediaInfo.getFloat("strategyScore" + sceneDTO.getRecommendStrategyDetail().getStrategyDO().getId());
                    resultMap.put(dto.getMediaId(), CalculateCoreUtil.getIPTVMediaVO(dto.getMediaId(), media, MediaShowEnum.IR_STAR.intValue(), sceneDTO.getSceneDO().getSysId(), score));
                    realFreeSize++;
                }
            }
        }

        //免费的不够,则补充收费的
        int insertChargeSize = 0;
        for (StarESSearchDTO dto : starEsSearchDTOList) {
            if (insertChargeSize >= recommendEngine.getSize() - realChargeSize - realFreeSize) {
                break;
            }
            if (MediaChargeFreeEnum.CHARGE.intValue().equals(dto.getIsCharge())) {
                String mediaInfoString = redisService.get(String.format(RedisConstant.MEDIA_KEY, dto.getMediaId()));
                if (StringUtils.isNotEmpty(mediaInfoString)) {
                    MediaDTO media = JSONObject.parseObject(mediaInfoString, MediaDTO.class);
                    JSONObject mediaInfo = JSONObject.parseObject(mediaInfoString);
                    Float score = mediaInfo.getFloat("strategyScore" + sceneDTO.getRecommendStrategyDetail().getStrategyDO().getId());
                    resultMap.put(dto.getMediaId(), CalculateCoreUtil.getIPTVMediaVO(dto.getMediaId(), media, MediaShowEnum.IR_STAR.intValue(), sceneDTO.getSceneDO().getSysId(), score));
                    insertChargeSize++;
                }
            }
        }
        return resultMap;
    }
}