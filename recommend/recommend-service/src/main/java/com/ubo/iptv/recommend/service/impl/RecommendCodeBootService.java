package com.ubo.iptv.recommend.service.impl;

import com.ubo.iptv.core.CommonResponse;
import com.ubo.iptv.core.enums.RecommendEngineEnum;
import com.ubo.iptv.entity.gdgd.SceneDTO;
import com.ubo.iptv.entity.gdgd.recommend.RecommendEngineDTO;
import com.ubo.iptv.mybatis.recommend.entity.StrategyRecommendEngineDO;
import com.ubo.iptv.recommend.enums.MediaShowEnum;
import com.ubo.iptv.recommend.response.IPTVMediaVO;
import com.ubo.iptv.recommend.service.UserBehaviorRecommendService;
import com.ubo.iptv.recommend.util.CalculateCoreUtil;
import com.ubo.iptv.recommend.util.ConstantUtil;
import com.ubo.iptv.recommend.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 冷启动推荐
 * Created by uboo on 2019/6/17.
 */
@Service
@Slf4j
public class RecommendCodeBootService extends RecommendService {
    @Override
    public List<RecommendEngineDTO> calculateRecommendEngineSize(Integer userDBId, String mediaId, SceneDTO strategyConfig, Integer size) {
        //根据推荐引擎,获取引擎列表,计算出所有引擎下详细推荐数量
        //核心算法 权重从小到大排序
        List<StrategyRecommendEngineDO> engineDOList = strategyConfig.getCodeBootStrategyDetail().getStrategyRecommendEngineList();
        List<RecommendEngineDTO> recommendEngines = engineDOList.stream().map(e -> {
            RecommendEngineDTO recommendEngine = new RecommendEngineDTO();
            BeanUtils.copyProperties(e, recommendEngine);
            return recommendEngine;
        }).collect(Collectors.toList());

        //计算出每个推荐引擎推荐的数量
        CalculateCoreUtil.calculateSize(recommendEngines, size);


        //题材偏好推荐引擎: 媒资类型,媒资题材,收费付费数量计算
        for (RecommendEngineDTO recommendEngine : recommendEngines) {
            //贵州小程序暂无
            if (RecommendEngineEnum.MEDIA_PREFERENCE.intValue().equals(recommendEngine.getRecommendEngine())) {
                //题材偏好 推荐引擎的数量
                CalculateCoreUtil.setCodeBootTypeKindSizeList(recommendEngine, strategyConfig);
                recommendEngine.setIsCalculateKind(false);
            }
            break;
        }
        return recommendEngines;
    }

    @Override
    public CommonResponse<List<IPTVMediaVO>> recommend(LinkedHashMap<Integer, IPTVMediaVO> resultMap, String userId, Integer userDBId, SceneDTO sceneDTO, Integer size, String mediaCode) {
//        log.info("recommend: userDBId={}, strategyConfig={}, size={},mediaId={}", userDBId, JSONObject.toJSONString(sceneDTO), size, mediaId);
        AtomicLong begin = new AtomicLong(System.currentTimeMillis());
        //1黑名单排除   暂时没有黑名单
        LinkedHashMap<String, String> excludeMap = blackList(sceneDTO);
        //2 获取人工干预
//        resultMap = manualList(resultMap, size, sceneDTO, true, excludeMap);
//        if (size - resultMap.keySet().size() <= ConstantUtil.ZERO) {
//            return CommonResponse.success(resultMap.values().stream().sorted((o1, o2) -> {
//                if (o1.getShowType().equals(MediaShowEnum.COLD_BOOT_MANUAL_TOP.intValue()) || o2.getShowType().equals(MediaShowEnum.COLD_BOOT_MANUAL_TOP.intValue())) {
//                    return o1.getShowType().compareTo(o2.getShowType());
//                } else {
//                    Float o1Score = o1.getScore() == null ? 0 : o1.getScore();
//                    Float o2Score = o2.getScore() == null ? 0 : o2.getScore();
//                    return o2Score.compareTo(o1Score);
//                }
//            }).collect(Collectors.toList()));
//        }

        //4 排除列表
        excludeMap = excludeList(userId,sceneDTO, userDBId,true, excludeMap);
        //如果参数中的mediaCode不为为空,则放入排除列表（当前媒资不在此页面再次推荐）
        if (null != mediaCode) {
            excludeMap.put(mediaCode, "");
        }
        //3 根据推荐引擎,获取引擎列表,计算出所有引擎下详细推荐数量
        List<RecommendEngineDTO> recommendEngines = calculateRecommendEngineSize(userDBId, mediaCode, sceneDTO, size - resultMap.keySet().size());
//        log.info("coldboot  计算出所有引擎下详细推荐数量：共耗时 {}毫秒", System.currentTimeMillis() - begin.get());
        begin = new AtomicLong(System.currentTimeMillis());
        //4 推荐 题材偏好
        for (RecommendEngineDTO recommendEngine : recommendEngines) {
            //当前推荐数量不大于0,则执行下个推荐引擎
            if (recommendEngine.getSize() <= ConstantUtil.ZERO) {
                continue;
            }
            //如果是题材偏好,typeList为空,则跳过推荐
            if (RecommendEngineEnum.MEDIA_PREFERENCE.intValue().equals(recommendEngine.getRecommendEngine())) {
                if (CollectionUtils.isEmpty(recommendEngine.getMediaTypeList())) {
                    continue;
                }
            }
            UserBehaviorRecommendService recommendService = (UserBehaviorRecommendService) SpringContextUtil.getBean(RecommendEngineEnum.valueOf(recommendEngine.getRecommendEngine()).unit());
            resultMap = recommendService.recommendAndSort(resultMap, userDBId, mediaCode, sceneDTO, excludeMap, recommendEngine, true);
            if (size - resultMap.keySet().size() <= ConstantUtil.ZERO) {
                break;
            }
        }
//        log.info("coldboot 冷启动 题材偏好：共耗时 {}毫秒", System.currentTimeMillis() - begin.get());
        //返回结果
        return CommonResponse.success(resultMap.values().stream().sorted((o1, o2) -> {
            if (o1.getShowType().equals(MediaShowEnum.COLD_BOOT_MANUAL_TOP.intValue()) || o2.getShowType().equals(MediaShowEnum.COLD_BOOT_MANUAL_TOP.intValue())) {
                return o1.getShowType().compareTo(o2.getShowType());
            } else {
                Float o1Score = o1.getScore() == null ? 0 : o1.getScore();
                Float o2Score = o2.getScore() == null ? 0 : o2.getScore();
                return o2Score.compareTo(o1Score);
            }
        }).collect(Collectors.toList()));
    }
}
