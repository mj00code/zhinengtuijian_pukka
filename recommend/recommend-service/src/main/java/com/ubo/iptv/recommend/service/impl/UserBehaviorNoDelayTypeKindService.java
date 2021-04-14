package com.ubo.iptv.recommend.service.impl;

import com.ubo.iptv.entity.gdgd.SceneDTO;
import com.ubo.iptv.entity.gdgd.recommend.RecommendEngineDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * 实时类型题材推荐
 * Created by uboo on 2019/6/17.
 */
@Service("UserBehaviorNoDelayTypeKindService")
@Slf4j
public class UserBehaviorNoDelayTypeKindService extends UserBehaviorTypeKindService {
    @Override
    public LinkedHashMap recommendAndSort(LinkedHashMap resultMap, Integer userId, String mediaId, SceneDTO sceneDTO, LinkedHashMap<String, String> excludeMap, RecommendEngineDTO recommendEngine, Boolean isColdBoot) {
        return super.recommendAndSort(resultMap, userId, mediaId, sceneDTO, excludeMap, recommendEngine, false);
    }
}
