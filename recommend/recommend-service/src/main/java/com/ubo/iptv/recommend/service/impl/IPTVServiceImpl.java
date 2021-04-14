package com.ubo.iptv.recommend.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ubo.iptv.core.CommonResponse;
import com.ubo.iptv.core.constant.RedisConstant;
import com.ubo.iptv.core.enums.IPTVErrorCode;
import com.ubo.iptv.core.enums.StatusEnum;
import com.ubo.iptv.core.util.DateUtil;
import com.ubo.iptv.entity.gdgd.RecommendSnapshotDTO;
import com.ubo.iptv.entity.gdgd.SceneDTO;
import com.ubo.iptv.entity.gdgd.StrategySwitchDTO;
import com.ubo.iptv.entity.gdgd.UserDTO;
import com.ubo.iptv.mybatis.recommend.entity.SceneDO;
import com.ubo.iptv.recommend.enums.MediaShowEnum;
import com.ubo.iptv.recommend.response.IPTVMediaVO;
import com.ubo.iptv.recommend.service.IPTVService;
import com.ubo.iptv.recommend.service.NatsMessageService;
import com.ubo.iptv.recommend.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * iptv推荐外部接口
 * Created by uboo on 2019/6/17.
 */
@Service
@Slf4j
public class IPTVServiceImpl implements IPTVService {
    @Autowired
    RedisService redisService;
    @Autowired
    @Qualifier(value = "recommendCodeBootService")
    private RecommendCodeBootService codeBootService;
    @Autowired
    @Qualifier(value = "recommendIntelligenceService")
    private RecommendIntelligenceService intelligenceService;
    @Autowired
    private NatsMessageService messageService;

    @Override
    public CommonResponse<List<IPTVMediaVO>> recommendPrograms(String sysId, String userId, Integer sceneId, Integer size, Integer categoryId, String mediaCode) {
//        log.info("recommend: sysId={}, userId={}, sceneId={},size={}, categoryId={}, mediaId={}", sysId, userId, sceneId, size, categoryId, mediaId);

        //获取场景详细配置
        CommonResponse<SceneDTO> strategyConfigComm = getSceneConfig(sysId, sceneId);
        if (strategyConfigComm._isFailed()) return (CommonResponse) strategyConfigComm;
        SceneDTO sceneDTO = strategyConfigComm.getData();
        SceneDO sceneDO = sceneDTO.getSceneDO();

        //智能推荐是否关闭 关闭则返回后台设置的栏目编号
        CommonResponse<String> isRecommendClose = isRecommendClose(sysId, sceneDO);
        if (isRecommendClose.getResult() == 0 && isRecommendClose.getStatus() == 10)
            return (CommonResponse) isRecommendClose;

        //mediaId转化为内部mediaId

        //userId转化成内部userId
        Integer userDbId = null;
        String userKey = redisService.getHash(RedisConstant.HASH_USER_CODE2ID_KEY, userId);
        if (StringUtils.isNotBlank(userKey)) {
            UserDTO user = redisService.get(userKey, UserDTO.class);
            if (user != null) {
                userDbId = user.getUserId();
            }
        }

        boolean isCodeBoot = false;
//        //新用户或者新媒资的情况下,走冷启动
//        if (null == userDbId) {
//            //冷启动推荐流程
//            isCodeBoot = true;
//        }

        //当前栏目页面冷启动逻辑是否触发
        if (sceneDO.getSupportColdBoot()) {
            if (userColdBoot(userDbId, sceneDO)) {
                //冷启动推荐流程
                isCodeBoot = true;
            }
        }

        //返回值设定
        CommonResponse<List<IPTVMediaVO>> resultComm = null;
        //获取上次缓存的推荐数据 key为媒资code
        LinkedHashMap<Integer, IPTVMediaVO> map = new LinkedHashMap<>();
        // LinkedHashMap<Integer, IPTVMediaVO> map = getLastCacheData(userId, sceneId, mediaCode);
        if (isCodeBoot) {
            //冷启动逻辑
            resultComm = codeBootService.recommend(map, userId, userDbId, sceneDTO, size, mediaCode);
//            log.info("main 冷启动逻辑：共耗时 {}毫秒", System.currentTimeMillis() - begin.get());
        } else {
            //智能推荐流程
            resultComm = intelligenceService.recommend(map, userId, userDbId, sceneDTO, size, mediaCode);
//            log.info("main 智能推荐逻辑：共耗时 {}毫秒", System.currentTimeMillis() - begin.get());
        }

        if (resultComm._isOk()) {
            saveLastCacheData(resultComm, userId, sceneId, mediaCode);
            return saveRecommendSnapshot(sysId, resultComm, userDbId, userId, sceneDTO);
        }
//        log.info("main 本次推荐：共耗时 {}毫秒", System.currentTimeMillis() - begin.get());
        return resultComm;
    }

    private void saveLastCacheData(CommonResponse<List<IPTVMediaVO>> resultComm, String userId, Integer sceneId, String mediaCode) {
        String redisKey = String.format(RedisConstant.RC_CACHE, userId, sceneId, StringUtils.isBlank(mediaCode) ? "" : mediaCode);
        if (redisService.exists(redisKey)) {
            return;
        }
        List<IPTVMediaVO> list = resultComm.getData();
        Map<String, String> map = new HashMap<>();
        for (IPTVMediaVO iptvMediaVO : list) {
            if (MediaShowEnum.MANUAL_TOP.intValue().intValue() != iptvMediaVO.getShowType() || MediaShowEnum.MANUAL_MUST.intValue().intValue() != iptvMediaVO.getShowType()) {
                map.put(iptvMediaVO.getMediaCode(), JSONObject.toJSONString(iptvMediaVO));
            }
        }
        redisService.setHash(redisKey, map, 60 * 60L);
    }

    private LinkedHashMap<Integer, IPTVMediaVO> getLastCacheData(String userId, Integer sceneId, String mediaCode) {
        LinkedHashMap<Integer, IPTVMediaVO> resultMap = new LinkedHashMap<>();
        String redisKey = String.format(RedisConstant.RC_CACHE, userId, sceneId, StringUtils.isBlank(mediaCode) ? "" : mediaCode);
        if (redisService.exists(redisKey)) {
            resultMap = redisService.getRcHash(redisKey);
        }
        return resultMap;
    }

    /**
     * 保存智能推荐信息快照
     * Intelligent recommendation
     */
    private CommonResponse<List<IPTVMediaVO>> saveRecommendSnapshot(String sysId, CommonResponse<List<IPTVMediaVO>> resultComm, Integer userDbId, String userId, SceneDTO sceneDTO) {
        List<IPTVMediaVO> list = resultComm.getData();
        Map map = new HashMap();
        StringBuffer sb = new StringBuffer();
        Long sceneId = sceneDTO.getSceneDO().getId();
        String logUserId = "";
        RecommendSnapshotDTO recommendSnapshotDTO = new RecommendSnapshotDTO();
        if (userDbId == null) {
            logUserId = userId;
            recommendSnapshotDTO.setIsNewUser(true);
        } else {
            logUserId = userDbId.toString();
            recommendSnapshotDTO.setIsNewUser(false);
        }
        String logId = logUserId + "-" + sceneId + "-" + new AtomicLong(System.currentTimeMillis());
        for (IPTVMediaVO iptvMediaVO : list) {
            map.put(iptvMediaVO.getMediaId(), "");
            sb.append(iptvMediaVO.getMediaId());
            sb.append(",");
            iptvMediaVO.setStrategyLogId(logId);
        }
        //该场景保存智能推荐媒资信息 一个小时过期
        String redisKey = String.format(RedisConstant.PROVIDED, logUserId, sceneId);
        redisService.set(redisKey, JSONObject.toJSONString(map), 24 * 60 * 60L);
        //发送NATS消息,在ES中保存该场景的推荐信息
        recommendSnapshotDTO.setKey(logId);
        recommendSnapshotDTO.setSysId(sysId);
        recommendSnapshotDTO.setUserId(logUserId);
        recommendSnapshotDTO.setSceneId(sceneId);
        recommendSnapshotDTO.setSceneDTO(sceneDTO);
        recommendSnapshotDTO.setRecommendContent(sb.toString());
        recommendSnapshotDTO.setTime(DateUtil.format(LocalDateTime.now()));
//        messageService.publish(JSONObject.toJSONString(recommendSnapshotDTO));
        return CommonResponse.success(list);
    }

    /**
     * 设置策略配置信息
     * Intelligent recommendation
     */
    private CommonResponse<SceneDTO> getSceneConfig(String sysId, Integer sceneId) {

        if (null == sceneId) {
            return CommonResponse.build(IPTVErrorCode.NO_SCENE_SET);
        }
        String sceneInfo = redisService.getHash(RedisConstant.HASH_SCENE_KEY, sceneId.toString());
        if (StringUtils.isEmpty(sceneInfo)) {
            return CommonResponse.build(IPTVErrorCode.NO_SCENE_SET);
        }
        //策略详细配置
        SceneDTO strategyConfig = JSONObject.parseObject(sceneInfo, SceneDTO.class);

        //根据场景信息获取策略信息 智能推荐
        if (null == strategyConfig.getRecommendStrategyDetail().getStrategyDO()) {
            return CommonResponse.build(IPTVErrorCode.NO_STRATEGY_SET);
        }
        //根据策略信息获取推荐引擎 智能推荐
        if (CollectionUtils.isEmpty(strategyConfig.getRecommendStrategyDetail().getStrategyRecommendEngineList())) {
            return CommonResponse.build(IPTVErrorCode.NO_STRATEGY_SET);
        }
        if (strategyConfig.getSceneDO().getSupportColdBoot()) {
            //根据场景信息获取策略信息 冷启动
            if (null == strategyConfig.getCodeBootStrategyDetail().getStrategyDO()) {
                return CommonResponse.build(IPTVErrorCode.NO_CODE_BOOT_STRATEGY_SET);
            }
            //根据策略信息获取推荐引擎 冷启动
            if (CollectionUtils.isEmpty(strategyConfig.getCodeBootStrategyDetail().getStrategyRecommendEngineList())) {
                return CommonResponse.build(IPTVErrorCode.NO_CODE_BOOT_STRATEGY_RECOMMEND_SET);
            }
        }

        return CommonResponse.success(strategyConfig);
    }

    /**
     * 冷启动是否触发
     * Intelligent recommendation
     */
    private boolean userColdBoot(Integer userId, SceneDO sceneDO) {

        //从缓存中获取场景冷启动信息
        String key = String.format(RedisConstant.CODE_BOOT, userId);
        String userColdBoot = redisService.get(key);

        //如果没有值
        if (StringUtils.isEmpty(userColdBoot)) {
            return true;
        }

        //判断是否是冷启动
        Map map = (Map) JSONObject.parseObject(userColdBoot, HashMap.class);

        //当前场景没有值的话,触发冷启动
        if (!map.containsKey(sceneDO.getId().toString())) {
            return true;
        }

        //当前场景不满足冷启动的条件,不触发冷启动
        if (!(Boolean) map.get(sceneDO.getId().toString())) {
            return false;
        }

        return true;
    }

    /**
     * 全局智能推荐是否关闭
     * Intelligent recommendation
     */
    private CommonResponse<String> isRecommendClose(String sysId, SceneDO sceneDO) {
        String key = RedisConstant.IR_CONFIG;
        //没有设置默认关闭
        String categoryId = "";
        if (!redisService.exists(key)) {
            //整个智能推荐关闭
            return CommonResponse.buildWithData(IPTVErrorCode.ALL_RECOMMEND_CLOSE, categoryId);
        }

        String intelligentRecommendationConfig = redisService.get(key);
        JSONObject js = JSONObject.parseObject(intelligentRecommendationConfig);
        String sceneIRKey = String.format(RedisConstant.SINGLE_SCENE, sceneDO.getId());
        //获取场景值
        String sceneIRString = js.getString(sceneIRKey);
        StrategySwitchDTO sceneIRStrategySwitchDTO = JSONObject.parseObject(sceneIRString, StrategySwitchDTO.class);
        //当前场景推荐字段没设置,默认关闭 1:开启 0:关闭
        if (null == sceneIRStrategySwitchDTO || StatusEnum.INVALID.intValue() == sceneIRStrategySwitchDTO.getOpenCloseFlag().intValue()) {
            if (null != sceneIRStrategySwitchDTO) {
                categoryId = sceneIRStrategySwitchDTO.getCategoryId() == null ? "" : sceneIRStrategySwitchDTO.getCategoryId();
            }
            //当前智能推荐关闭
            return CommonResponse.buildWithData(IPTVErrorCode.SINGLE_PAGE_RECOMMEND_CLOSE, categoryId);
        }
        return CommonResponse.SUCCESS;

    }

}
