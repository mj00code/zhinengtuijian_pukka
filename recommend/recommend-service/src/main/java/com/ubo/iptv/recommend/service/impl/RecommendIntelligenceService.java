package com.ubo.iptv.recommend.service.impl;

import com.ubo.iptv.core.CommonResponse;
import com.ubo.iptv.entity.gdgd.SceneDTO;
import com.ubo.iptv.recommend.response.IPTVMediaVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 智能推荐
 * Created by uboo on 2019/6/17.
 */
@Service
@Slf4j
public class RecommendIntelligenceService extends RecommendService {
    @Autowired
    @Qualifier(value = "recommendCodeBootService")
    private RecommendCodeBootService codeBootService;

    @Override
    public CommonResponse<List<IPTVMediaVO>> recommend(LinkedHashMap<Integer, IPTVMediaVO> resultMap, String userId, Integer userDbId, SceneDTO strategyConfig, Integer size, String mediaCode) {
        CommonResponse<List<IPTVMediaVO>> recommendCommonResponse = super.recommend(resultMap, userId, userDbId, strategyConfig, size, mediaCode);
        if (recommendCommonResponse._isFailed()) {
            return recommendCommonResponse;
        }
        List<IPTVMediaVO> recommendList = recommendCommonResponse.getData();
        //如果智能推荐的数量不够,如果支持冷启动,就通过冷启动推送,如果不支持冷启动,则有多少推送多少
        if (recommendList.size() < size) {
            if (strategyConfig.getSceneDO().getSupportColdBoot()) {
                //走冷启动补数据
                return codeBootService.recommend(resultMap, userId, userDbId, strategyConfig, size, mediaCode);
            }
        }
        return recommendCommonResponse;
    }
}
