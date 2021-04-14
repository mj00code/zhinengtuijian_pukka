package com.ubo.iptv.recommend.service;

import com.ubo.iptv.entity.gdgd.SceneDTO;
import com.ubo.iptv.entity.gdgd.recommend.RecommendEngineDTO;

import java.util.LinkedHashMap;


/**
 * Created by uboo on 2019/6/17.
 */
public interface UserBehaviorRecommendService {
    LinkedHashMap recommendAndSort(LinkedHashMap resultMap, Integer userId, String mediaCode, SceneDTO sceneDTO, LinkedHashMap<String, String> excludeMap, RecommendEngineDTO recommendEngine, Boolean isColdBoot);
}
