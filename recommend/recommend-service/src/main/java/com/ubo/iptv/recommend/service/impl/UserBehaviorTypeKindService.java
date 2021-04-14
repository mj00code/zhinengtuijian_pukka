package com.ubo.iptv.recommend.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ubo.iptv.core.constant.RedisConstant;
import com.ubo.iptv.entity.gdgd.MediaDTO;
import com.ubo.iptv.entity.gdgd.SceneDTO;
import com.ubo.iptv.entity.gdgd.TypeKindESSearchDTO;
import com.ubo.iptv.entity.gdgd.recommend.MediaChargeFreeDTO;
import com.ubo.iptv.entity.gdgd.recommend.RecommendEngineDTO;
import com.ubo.iptv.recommend.enums.MediaShowEnum;
import com.ubo.iptv.recommend.service.RedisService;
import com.ubo.iptv.recommend.service.SearchService;
import com.ubo.iptv.recommend.service.UserBehaviorRecommendService;
import com.ubo.iptv.recommend.util.CalculateCoreUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 历史类型题材推荐
 * Created by uboo on 2019/6/17.
 */
@Service("UserBehaviorTypeKindService")
@Slf4j
public class UserBehaviorTypeKindService implements UserBehaviorRecommendService {

    @Autowired
    RedisService redisService;
    @Autowired
    SearchService searchService;

    @Override
    public LinkedHashMap recommendAndSort(LinkedHashMap resultMap, Integer userId, String mediaCode, SceneDTO sceneDTO, LinkedHashMap<String, String> excludeMap, RecommendEngineDTO recommendEngine, Boolean isColdBoot) {
        //根据筛选的top的类型,调用es获取所有推荐类型+题材的列表
        //例如:top3个类型,top9的题材...每个题材10个,返回180条数据..从中筛选出合适的数据
        long begin = System.currentTimeMillis();
        LinkedHashMap allExcludeMap = new LinkedHashMap();
        allExcludeMap.putAll(resultMap);
        allExcludeMap.putAll(excludeMap);
        List<MediaChargeFreeDTO> mediaChargeFreeList = recommendEngine.getMediaChargeFreeList();
        List<TypeKindESSearchDTO> typeKindEsSearchDTOList = new ArrayList<>();
        if (recommendEngine.getIsCalculateKind()) {
            //是否进行媒资题材计算，小程序目前暂无媒资题材分类
            typeKindEsSearchDTOList = searchService.findMediaByTypeAndKind(sceneDTO, allExcludeMap, recommendEngine, isColdBoot);
        } else {
            typeKindEsSearchDTOList = searchService.findMediaByType(sceneDTO, allExcludeMap, recommendEngine, isColdBoot);
        }

        if (typeKindEsSearchDTOList.size() == 0) {
            return resultMap;
        }
        Integer mediaShowType = null;
        if (isColdBoot) {
            mediaShowType = MediaShowEnum.COLD_BOOT_IR_TYPE_KIND.intValue();
        } else {
            mediaShowType = MediaShowEnum.IR_TYPE_KIND.intValue();
        }
        for (MediaChargeFreeDTO mediaChargeFree : mediaChargeFreeList) {
            //精准匹配
            wholeMatch(typeKindEsSearchDTOList, mediaChargeFree, resultMap, recommendEngine, sceneDTO, mediaShowType);
            if (mediaChargeFree.getSize() > 0) {
                //数量不够,忽略题材匹配
                matchExceptKind(typeKindEsSearchDTOList, mediaChargeFree, resultMap, recommendEngine, sceneDTO, mediaShowType);
            }
            if (mediaChargeFree.getSize() > 0) {
                //数量不够,忽略是否免费匹配
                matchExceptChargeFree(typeKindEsSearchDTOList, mediaChargeFree, resultMap, recommendEngine, sceneDTO, mediaShowType);
            }
            if (mediaChargeFree.getSize() > 0) {
                //数量不够,忽略类型匹配
                matchExceptType(typeKindEsSearchDTOList, mediaChargeFree, resultMap, sceneDTO, mediaShowType);
            }

        }
//        log.info("题材偏好 function：共耗时 {}毫秒", (System.currentTimeMillis() - begin) );
        return resultMap;
    }

    /**
     * 忽略类型匹配
     *
     * @param typeKindEsSearchDTOList
     * @param mediaChargeFree
     * @param resultMap
     * @param sceneDTO
     */
    private void matchExceptType(List<TypeKindESSearchDTO> typeKindEsSearchDTOList, MediaChargeFreeDTO mediaChargeFree, LinkedHashMap resultMap, SceneDTO sceneDTO, Integer mediaShowType) {
        for (TypeKindESSearchDTO esresult : typeKindEsSearchDTOList) {
            if (mediaChargeFree.getSize() <= 0) {
                break;
            }
            //去重
            if (resultMap.containsKey(esresult.getMediaId())) {
                continue;
            }
            //满足设值条件
            String mediaInfoString = redisService.get(String.format(RedisConstant.MEDIA_KEY, esresult.getMediaId()));
            if (StringUtils.isNotEmpty(mediaInfoString)) {
                MediaDTO media = JSONObject.parseObject(mediaInfoString, MediaDTO.class);
                resultMap.put(esresult.getMediaId(), CalculateCoreUtil.getIPTVMediaVO(esresult.getMediaId(), media, mediaShowType, sceneDTO.getSceneDO().getSysId(), esresult.getScore()));
                mediaChargeFree.setSize(mediaChargeFree.getSize() - 1);
            }

        }
    }

    /**
     * 忽略题材匹配
     *
     * @param typeKindEsSearchDTOList
     * @param mediaChargeFree
     * @param resultMap
     * @param recommendEngine
     * @param sceneDTO
     */
    private void matchExceptKind(List<TypeKindESSearchDTO> typeKindEsSearchDTOList, MediaChargeFreeDTO mediaChargeFree, LinkedHashMap resultMap, RecommendEngineDTO recommendEngine, SceneDTO sceneDTO, Integer mediaShowType) {
        for (TypeKindESSearchDTO esresult : typeKindEsSearchDTOList) {
            if (mediaChargeFree.getSize() <= 0) {
                break;
            }
            int isSetDate = 0;
            //去重
            if (resultMap.containsKey(esresult.getMediaId())) {
                continue;
            }
            if (mediaChargeFree.getMediaType().equals(esresult.getMediaType())
                    && mediaChargeFree.getIsCharge().equals(esresult.getIsCharge())) {
                isSetDate = 1;
            }
            //满足设值条件
            if (isSetDate == 1) {
                String mediaInfoString = redisService.get(String.format(RedisConstant.MEDIA_KEY, esresult.getMediaId()));
                if (StringUtils.isNotEmpty(mediaInfoString)) {
                    MediaDTO media = JSONObject.parseObject(mediaInfoString, MediaDTO.class);
                    resultMap.put(esresult.getMediaId(), CalculateCoreUtil.getIPTVMediaVO(esresult.getMediaId(), media, mediaShowType, sceneDTO.getSceneDO().getSysId(), esresult.getScore()));
                    mediaChargeFree.setSize(mediaChargeFree.getSize() - 1);
                }
            }

        }
    }

    /**
     * 忽略收费免费匹配
     *
     * @param typeKindEsSearchDTOList
     * @param mediaChargeFree
     * @param resultMap
     * @param recommendEngine
     * @param sceneDTO
     */
    private void matchExceptChargeFree(List<TypeKindESSearchDTO> typeKindEsSearchDTOList, MediaChargeFreeDTO mediaChargeFree, LinkedHashMap resultMap, RecommendEngineDTO recommendEngine, SceneDTO sceneDTO, Integer mediaShowType) {
        for (TypeKindESSearchDTO esresult : typeKindEsSearchDTOList) {
            if (mediaChargeFree.getSize() <= 0) {
                break;
            }
            //去重
            if (resultMap.containsKey(esresult.getMediaId())) {
                continue;
            }
            int isSetDate = 0;
            if (mediaChargeFree.getMediaType().equals(esresult.getMediaType())) {
                isSetDate = 1;
            }
            //满足设值条件
            if (isSetDate == 1) {
                String mediaInfoString = redisService.get(String.format(RedisConstant.MEDIA_KEY, esresult.getMediaId()));
                if (StringUtils.isNotEmpty(mediaInfoString)) {
                    MediaDTO media = JSONObject.parseObject(mediaInfoString, MediaDTO.class);
                    resultMap.put(esresult.getMediaId(), CalculateCoreUtil.getIPTVMediaVO(esresult.getMediaId(), media, mediaShowType, sceneDTO.getSceneDO().getSysId(), esresult.getScore()));
                    mediaChargeFree.setSize(mediaChargeFree.getSize() - 1);
                }
            }

        }
    }

    /**
     * 精准匹配
     *
     * @param typeKindEsSearchDTOList
     * @param mediaChargeFree
     * @param resultMap
     * @param recommendEngine
     * @param sceneDTO
     */
    private void wholeMatch(List<TypeKindESSearchDTO> typeKindEsSearchDTOList, MediaChargeFreeDTO mediaChargeFree, LinkedHashMap resultMap, RecommendEngineDTO recommendEngine, SceneDTO sceneDTO, Integer mediaShowType) {
        for (TypeKindESSearchDTO esresult : typeKindEsSearchDTOList) {
            if (mediaChargeFree.getSize() <= 0) {
                break;
            }
            //去重
            if (resultMap.containsKey(esresult.getMediaId())) {
                continue;
            }
            int isSetDate = 0;
            if (recommendEngine.getIsCalculateKind()) {
                //计算题材
                if (mediaChargeFree.getMediaType().equals(esresult.getMediaType())
                        && mediaChargeFree.getMediaKind().equals(esresult.getMediaKind())
                        && mediaChargeFree.getIsCharge().equals(esresult.getIsCharge())) {
                    isSetDate = 1;
                }
            } else {
                //不计算题材
                if (mediaChargeFree.getMediaType().equals(esresult.getMediaType())
                        && mediaChargeFree.getIsCharge().equals(esresult.getIsCharge())) {
                    isSetDate = 1;
                }
            }
            //满足设值条件
            if (isSetDate == 1) {
                String mediaInfoString = redisService.get(String.format(RedisConstant.MEDIA_KEY, esresult.getMediaId()));
                if (StringUtils.isNotEmpty(mediaInfoString)) {
                    MediaDTO media = JSONObject.parseObject(mediaInfoString, MediaDTO.class);
                    resultMap.put(esresult.getMediaId(), CalculateCoreUtil.getIPTVMediaVO(esresult.getMediaId(), media, mediaShowType, sceneDTO.getSceneDO().getSysId(), esresult.getScore()));
                    mediaChargeFree.setSize(mediaChargeFree.getSize() - 1);
                }
            }

        }
    }
}
