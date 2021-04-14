package com.ubo.iptv.manage.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ubo.iptv.core.CommonResponse;
import com.ubo.iptv.core.constant.RedisConstant;
import com.ubo.iptv.core.enums.IPTVErrorCode;
import com.ubo.iptv.core.enums.StrategyTypeEnum;
import com.ubo.iptv.entity.gdgd.SceneDTO;
import com.ubo.iptv.entity.gdgd.StrategyDetailDTO;
import com.ubo.iptv.entity.gdgd.StrategySwitchDTO;
import com.ubo.iptv.manage.requset.CacheSwitchDTO;
import com.ubo.iptv.manage.service.RedisService;
import com.ubo.iptv.manage.service.SceneService;
import com.ubo.iptv.mybatis.recommend.entity.*;
import com.ubo.iptv.mybatis.recommend.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: xuning
 * @Date: 2020-11-07
 */
@Service
@Slf4j
public class SceneServiceImpl implements SceneService {

    @Autowired
    private RedisService redisService;
    @Resource
    private SceneMapper sceneMapper;
    @Resource
    private StrategyMapper strategyMapper;
    @Resource
    private StrategyBrowseFilterMapper strategyBrowseFilterMapper;
    @Resource
    private StrategyClickFilterMapper strategyClickFilterMapper;
    @Resource
    private StrategyColdBootMapper strategyColdBootMapper;
    @Resource
    private StrategyManualMapper strategyManualMapper;
    @Resource
    private StrategyMediaSortMapper strategyMediaSortMapper;
    @Resource
    private StrategyMediaTypeMapper strategyMediaTypeMapper;
    @Resource
    private StrategyRecommendEngineMapper strategyRecommendEngineMapper;

    @Override
    public CommonResponse cacheScene() {
        List<SceneDO> list = sceneMapper.selectList(null);

        Map<String, String> sceneArgsMap = new HashMap<>(list.size());
        Map<String, String> sceneMap = new HashMap<>(list.size());
        list.forEach(s -> {
            // 组装sceneArgsMap集合
            String key = s.getSysId();
            if (StringUtils.isNotEmpty(s.getCategoryCode())) {
                key += "_" + s.getCategoryCode();
            }
            sceneArgsMap.put(key, JSONObject.toJSONString(s));

            // 组装sceneMap集合
            SceneDTO sceneDTO = getScene(s);
            sceneMap.put(s.getId().toString(), JSONObject.toJSONString(sceneDTO));
        });
        //存储场景id
        sceneMap.put("all_keys", JSONObject.toJSONString(sceneMap.keySet()));
        // 同步至redis
        redisService.remove(RedisConstant.HASH_SCENE_ARGS_KEY);
        boolean redisResponse = redisService.setHash(RedisConstant.HASH_SCENE_ARGS_KEY, sceneArgsMap);
        if (!redisResponse) {
            log.error("Redis setHash error: SCENE_ARGS");
            return CommonResponse.FAIL;
        }
        redisService.remove(RedisConstant.HASH_SCENE_KEY);
        redisResponse = redisService.setHash(RedisConstant.HASH_SCENE_KEY, sceneMap);
        if (!redisResponse) {
            log.error("Redis setHash error: SCENE");
            return CommonResponse.FAIL;
        }
        return CommonResponse.SUCCESS;
    }

    @Override
    public CommonResponse cacheSwitch(CacheSwitchDTO dto) {
        //1:开启 0:关闭
        String key = RedisConstant.IR_CONFIG;
        //如果是系统初期化,则给默认开启值
        if (!redisService.exists(key)) {
            JSONObject js = new JSONObject();
            List<SceneDO> sceneDOList = sceneMapper.selectList(null);
            //设置总开关跟每个场景开关
            String allIRKey = RedisConstant.ALL_SCENE;
            StrategySwitchDTO strategySwitchDTO = new StrategySwitchDTO();
            strategySwitchDTO.setOpenCloseFlag(1);
            js.put(allIRKey, JSONObject.toJSONString(strategySwitchDTO));
            //全部场景
            for (SceneDO sceneDO : sceneDOList) {
                String sceneKey = String.format(RedisConstant.SINGLE_SCENE, sceneDO.getId());
                js.put(sceneKey, JSONObject.toJSONString(strategySwitchDTO));
            }
            redisService.set(key, JSONObject.toJSONString(js));
            return CommonResponse.SUCCESS;
        }

        String config = redisService.get(key);
        JSONObject js = JSONObject.parseObject(config);
        StrategySwitchDTO strategySwitchDTO = new StrategySwitchDTO();
        strategySwitchDTO.setOpenCloseFlag(dto.getOpenCloseFlag());
        strategySwitchDTO.setCategoryId(dto.getCategoryId());
        //单个场景开关
        if (!dto.getIsAllIR()) {
            String sceneIRKey = String.format(RedisConstant.SINGLE_SCENE, dto.getSceneId());
            js.put(sceneIRKey, JSONObject.toJSONString(strategySwitchDTO));
            redisService.set(key, JSONObject.toJSONString(js));
            return CommonResponse.SUCCESS;

        }
        //总开关
        //如果是关闭的情况,要判断是否每个场景都设置了categoryId才能关闭
        List<SceneDO> sceneDOList = sceneMapper.selectList(new LambdaQueryWrapper<SceneDO>().eq(StringUtils.isNotEmpty(dto.getSysId()), SceneDO::getSysId, dto.getSysId()));
        if (0 == dto.getOpenCloseFlag()) {
            StringBuffer sb = new StringBuffer();
            int isAllset = 0;
            for (SceneDO sceneDO : sceneDOList) {
                String sceneKey = String.format(RedisConstant.SINGLE_SCENE, sceneDO.getId());
                if (StringUtils.isEmpty(sceneKey)) {
                    sb.append(StringUtils.isEmpty(sceneDO.getName()) ? "" : sceneDO.getName() + ",");
                    isAllset = 1;
                } else {
                    StrategySwitchDTO strategySwitch = JSONObject.parseObject(js.getString(sceneKey), StrategySwitchDTO.class);
                    if (StringUtils.isEmpty(strategySwitch.getCategoryId())) {
                        sb.append(StringUtils.isEmpty(sceneDO.getName()) ? "" : sceneDO.getName() + ",");
                        isAllset = 1;
                    }
                }
            }
            //如果不是全部场景都设置了categoryId,则返回提示信息
            if (1 == isAllset) {
                String errorName = sb.toString().substring(0, sb.toString().length() - 1);
                return CommonResponse.build(IPTVErrorCode.NOT_ALL_SCENE_CATEGORY_SET, errorName);
            }

        }
        //设置总开关跟每个场景开关
        String allIRKey = RedisConstant.ALL_SCENE;
        js.put(allIRKey, JSONObject.toJSONString(strategySwitchDTO));

        //全部场景
        for (SceneDO sceneDO : sceneDOList) {
            String sceneKey = String.format(RedisConstant.SINGLE_SCENE, sceneDO.getId());
            StrategySwitchDTO strategySwitch = JSONObject.parseObject(js.getString(sceneKey), StrategySwitchDTO.class);
            strategySwitch.setOpenCloseFlag(dto.getOpenCloseFlag());
            js.put(sceneKey, JSONObject.toJSONString(strategySwitch));
        }
        redisService.set(key, JSONObject.toJSONString(js));
        return CommonResponse.SUCCESS;

    }

    /**
     * 获取场景策略
     *
     * @param sceneDO
     * @return
     */
    private SceneDTO getScene(SceneDO sceneDO) {
        SceneDTO sceneDTO = new SceneDTO();
        // 场景信息
        sceneDTO.setSceneDO(sceneDO);
        // 智能推荐策略
        StrategyDetailDTO recommendStrategyDetail = getStrategyDetail(sceneDO.getId(), StrategyTypeEnum.INTELLIGENT.intValue());
        sceneDTO.setRecommendStrategyDetail(recommendStrategyDetail);
        // 冷启动策略
        if (sceneDO.getSupportColdBoot()) {
            StrategyDetailDTO codeBootStrategyDetail = getStrategyDetail(sceneDO.getId(), StrategyTypeEnum.COLD_BOOT.intValue());
            sceneDTO.setCodeBootStrategyDetail(codeBootStrategyDetail);
        }
        return sceneDTO;
    }

    /**
     * 获取策略配置信息
     *
     * @param sceneId
     * @param type
     */
    private StrategyDetailDTO getStrategyDetail(Long sceneId, Integer type) {
        //根据场景信息获取策略信息
        StrategyDO strategyDO = strategyMapper.selectOne(new LambdaQueryWrapper<StrategyDO>().eq(StrategyDO::getSceneId, sceneId).eq(StrategyDO::getType, type));
        if (strategyDO == null) {
            return null;
        }

        //曝光过滤
        StrategyBrowseFilterDO strategyBrowseFilterDO = strategyBrowseFilterMapper.selectOne(new LambdaQueryWrapper<StrategyBrowseFilterDO>().eq(StrategyBrowseFilterDO::getStrategyId, strategyDO.getId()));
        //点击过滤
        List<StrategyClickFilterDO> strategyClickFilterList = strategyClickFilterMapper.selectList(new LambdaQueryWrapper<StrategyClickFilterDO>().eq(StrategyClickFilterDO::getStrategyId, strategyDO.getId()));
        //冷启动
        StrategyColdBootDO strategyColdBootDO = strategyColdBootMapper.selectOne(new LambdaQueryWrapper<StrategyColdBootDO>().eq(StrategyColdBootDO::getStrategyId, strategyDO.getId()));
        //人工干预
        List<StrategyManualDO> strategyManualList = strategyManualMapper.selectList(new LambdaQueryWrapper<StrategyManualDO>().eq(StrategyManualDO::getStrategyId, strategyDO.getId()).eq(StrategyManualDO::getStatus, 1));
        //媒资排序信息
        List<StrategyMediaSortDO> strategyMediaSortList = strategyMediaSortMapper.selectList(new LambdaQueryWrapper<StrategyMediaSortDO>().eq(StrategyMediaSortDO::getStrategyId, strategyDO.getId()).orderByAsc(StrategyMediaSortDO::getWeight));
        //媒资类型信息
        List<StrategyMediaTypeDO> strategyMediaTypeList = strategyMediaTypeMapper.selectList(new LambdaQueryWrapper<StrategyMediaTypeDO>().eq(StrategyMediaTypeDO::getStrategyId, strategyDO.getId()).orderByAsc(StrategyMediaTypeDO::getWeight).orderByDesc(StrategyMediaTypeDO::getMediaType));
        //推荐引擎
        List<StrategyRecommendEngineDO> strategyRecommendEngineList = strategyRecommendEngineMapper.selectList(new LambdaQueryWrapper<StrategyRecommendEngineDO>().eq(StrategyRecommendEngineDO::getStrategyId, strategyDO.getId()).orderByAsc(StrategyRecommendEngineDO::getWeight));

        //获取策略详细配置
        StrategyDetailDTO strategyDetail = new StrategyDetailDTO();
        strategyDetail.setStrategyDO(strategyDO);
        strategyDetail.setStrategyBrowseFilterDO(strategyBrowseFilterDO);
        strategyDetail.setStrategyClickFilterList(strategyClickFilterList);
        strategyDetail.setStrategyColdBootDO(strategyColdBootDO);
        strategyDetail.setStrategyManualList(strategyManualList);
        strategyDetail.setStrategyMediaSortList(strategyMediaSortList);
        strategyDetail.setStrategyMediaTypeList(strategyMediaTypeList);
        strategyDetail.setStrategyRecommendEngineList(strategyRecommendEngineList);
        return strategyDetail;
    }
}
