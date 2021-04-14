package com.ubo.iptv.recommend.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ubo.iptv.core.CommonResponse;
import com.ubo.iptv.core.constant.RedisConstant;
import com.ubo.iptv.core.enums.RecommendEngineEnum;
import com.ubo.iptv.core.util.DateUtil;
import com.ubo.iptv.entity.gdgd.ClickFilterDTO;
import com.ubo.iptv.entity.gdgd.MediaDTO;
import com.ubo.iptv.entity.gdgd.SceneDTO;
import com.ubo.iptv.entity.gdgd.recommend.MediaChargeFreeDTO;
import com.ubo.iptv.entity.gdgd.recommend.MediaKindTopDTO;
import com.ubo.iptv.entity.gdgd.recommend.MediaTypeTopDTO;
import com.ubo.iptv.entity.gdgd.recommend.RecommendEngineDTO;
import com.ubo.iptv.mybatis.recommend.entity.*;
import com.ubo.iptv.mybatis.recommend.mapper.MediaBlacklistMapper;
import com.ubo.iptv.recommend.enums.MediaShowEnum;
import com.ubo.iptv.recommend.response.IPTVMediaVO;
import com.ubo.iptv.recommend.service.RedisService;
import com.ubo.iptv.recommend.service.UserBehaviorRecommendService;
import com.ubo.iptv.recommend.util.CalculateCoreUtil;
import com.ubo.iptv.recommend.util.ConstantUtil;
import com.ubo.iptv.recommend.util.SpringContextUtil;
import io.swagger.models.auth.In;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Created by uboo on 2019/6/17.
 */
@Service
@Slf4j
public class

RecommendService {
    @Autowired
    RedisService redisService;
    @Autowired
    MediaBlacklistMapper mediaBlacklistMapper;

    /**
     * 获取人工干预信息列表
     *
     * @param resultMap
     * @param size
     * @param sceneDTO
     * @return
     */
    public LinkedHashMap<Integer, IPTVMediaVO> manualList(LinkedHashMap<Integer, IPTVMediaVO> resultMap, Integer size, SceneDTO sceneDTO, Boolean isCodeBoot, LinkedHashMap<String, String> excludeMap) {

        //场景信息获取 场景不支持人工干预,则返回null
        SceneDO sceneDO = sceneDTO.getSceneDO();
        if (!sceneDO.getSupportManual()) {
            return resultMap;
        }

        //获取人工干预设置信息
        List<StrategyManualDO> list = null;
        //冷启动
        if (isCodeBoot) {
            list = sceneDTO.getCodeBootStrategyDetail().getStrategyManualList();
        } else {
            //智能推荐
            list = sceneDTO.getRecommendStrategyDetail().getStrategyManualList();
        }
        if (CollectionUtils.isEmpty(list)) return resultMap;

        LinkedHashMap map = new LinkedHashMap<Integer, IPTVMediaVO>();
        list.forEach(strategyManualDO -> {
            //人工干预有效期间判定
            int effectFlag = 1;
            if (null != strategyManualDO.getStartTime()) {
                if (strategyManualDO.getStartTime().isAfter(LocalDateTime.now())) {
                    effectFlag = 0;
                }
            }
            if (null != strategyManualDO.getExpireTime()) {
                if (strategyManualDO.getExpireTime().isBefore(LocalDateTime.now())) {
                    effectFlag = 0;
                }
            }
            //人工干预有效期间内
            if (1 == effectFlag) {
                if (excludeMap.containsKey(strategyManualDO.getMediaId().toString())) {
                    return;
                }
                String mediaInfoString = redisService.get(String.format(RedisConstant.MEDIA_KEY, strategyManualDO.getMediaId()));
                if (StringUtils.isNotEmpty(mediaInfoString)) {
                    JSONObject mediaInfo = JSONObject.parseObject(mediaInfoString);
                    MediaDTO media = JSONObject.parseObject(mediaInfoString, MediaDTO.class);
                    Integer mediaShowType = null;
                    if (isCodeBoot) {
                        if (strategyManualDO.getTop()) {
                            mediaShowType = MediaShowEnum.COLD_BOOT_MANUAL_TOP.intValue();
                        } else {
                            mediaShowType = MediaShowEnum.COLD_BOOT_MANUAL_MUST.intValue();
                        }

                    } else {
                        if (strategyManualDO.getTop()) {
                            mediaShowType = MediaShowEnum.MANUAL_TOP.intValue();
                        } else {
                            mediaShowType = MediaShowEnum.MANUAL_MUST.intValue();
                        }
                    }
                    Float score = mediaInfo.getFloat("strategyScore" + strategyManualDO.getStrategyId());
                    IPTVMediaVO iptvProgramVO = CalculateCoreUtil.getIPTVMediaVO(strategyManualDO.getMediaId(), media, mediaShowType, sceneDTO.getSceneDO().getSysId(), score);
                    map.put(iptvProgramVO.getMediaId(), iptvProgramVO);
                }

            }
        });

        resultMap.values().forEach(iptvMediaVO -> {
            if (map.keySet().size() >= size) {
                return;
            }
            map.put(iptvMediaVO.getMediaId(), iptvMediaVO);
        });
        return map;
    }

    /**
     * * 获取点击必推列表
     * * 从缓存中捞取点击必推信息(点击必推逻辑,每天晚上所有有动作的用户搂一遍,把信息更新在缓存中,新的保存,超过失效的删除,超过时效的部分如果满足黑名单,则放到黑名单里)
     * * 点击必推 结构 key=click_must_userId_sceneId value为map<mediaId 加入时间>
     *
     * @param resultMap
     * @param size
     * @param sceneDTO
     * @param userDbId
     * @return
     */
    public LinkedHashMap<Integer, IPTVMediaVO> clickMustList(LinkedHashMap<Integer, IPTVMediaVO> resultMap, Integer size, SceneDTO sceneDTO, Integer userDbId) {
        SceneDO sceneDO = sceneDTO.getSceneDO();
        if (!sceneDO.getSupportClickFilter()) {
            return resultMap;
        }
        String key = String.format(RedisConstant.CLICK_MUST, userDbId, sceneDO.getId());
        if (!redisService.exists(key)) {
            return resultMap;
        }

        String keyValue = redisService.get(key);
        Map<String, ClickFilterDTO> map = (Map) JSONObject.parseObject(keyValue, HashMap.class);
        //获取点击过滤的信息
        List<StrategyClickFilterDO> strategyClickFilterList = sceneDTO.getRecommendStrategyDetail().getStrategyClickFilterList();
        //循环遍历map
        for (String mediaId : map.keySet()) {
            Integer ignoreDays = ConstantUtil.TWO;
            ClickFilterDTO clickFilter = map.get(mediaId);
            if (!CollectionUtils.isEmpty(strategyClickFilterList)) {
                for (StrategyClickFilterDO strategyClickFilterDO : strategyClickFilterList) {
                    if (strategyClickFilterDO.getMediaType().toString().equals(clickFilter.getMediaType())) {
                        ignoreDays = strategyClickFilterDO.getIgnoreDays();
                        break;
                    }
                }
            }
            if (DateUtil.parse(clickFilter.getDate()).isAfter(LocalDateTime.now().minusDays(ignoreDays))) {
                String mediaInfoString = redisService.get(String.format(RedisConstant.MEDIA_KEY, mediaId));
                if (StringUtils.isNotEmpty(mediaInfoString)) {
                    JSONObject mediaInfo = JSONObject.parseObject(mediaInfoString);
                    MediaDTO media = JSONObject.parseObject(mediaInfoString, MediaDTO.class);
                    Float score = mediaInfo.getFloat("strategyScore" + sceneDTO.getRecommendStrategyDetail().getStrategyDO().getId());
                    resultMap.put(Integer.parseInt(mediaId), CalculateCoreUtil.getIPTVMediaVO(Integer.parseInt(mediaId), media, MediaShowEnum.IR_MUST.intValue(), sceneDTO.getSceneDO().getSysId(), score));
                }
                if (resultMap.keySet().size() >= size) {
                    break;
                }
            }
        }
        return resultMap;
    }

    /*获取媒资黑名单*/
    public LinkedHashMap<String, String> blackList(SceneDTO sceneDTO) {
        //返回值初期化
        LinkedHashMap<String, String> excludeMap = new LinkedHashMap();
        //获取策略信息
        SceneDO sceneDO = sceneDTO.getSceneDO();
        //获取运营商信息
        String ispId = sceneDO.getSysId();
        //黑名单 分运营商
        String blackListKey = String.format(RedisConstant.MEDIA_BLACKLIST_KEY, ispId);
        if (redisService.exists(blackListKey)) {
            String blackListValue = redisService.get(blackListKey);
            List<MediaBlacklistDO> list = JSONArray.parseArray(blackListValue, MediaBlacklistDO.class);
            //循环遍历map
            list.forEach(mediaBlacklistDO -> {
                //0-不生效；1-生效
                int effectFlag = 1;
                //0-不生效；1-生效
                if (!mediaBlacklistDO.getStatus()) {
                    effectFlag = 0;
                }
                if (null != mediaBlacklistDO.getStartTime()) {
                    //开始时间晚于当前时间，媒资不在黑名单
                    if (mediaBlacklistDO.getStartTime().isAfter(LocalDateTime.now())) {
                        effectFlag = 0;
                    }
                }
                if (null != mediaBlacklistDO.getExpireTime()) {
                    //过期时间早于当前时间，媒资不在黑名单
                    if (mediaBlacklistDO.getExpireTime().isBefore(LocalDateTime.now())) {
                        effectFlag = 0;
                    }
                }
                if (1 == effectFlag) {//黑名单媒资
                    excludeMap.put(mediaBlacklistDO.getMediaId().toString(), "");
                }
            });
        }
        return excludeMap;
    }

    /**
     * 获取排除列表
     *
     * @param sceneDTO
     * @param userDbId
     * @return
     */
    public LinkedHashMap excludeList(String userId, SceneDTO sceneDTO, Integer userDbId, Boolean isCodeBoot, LinkedHashMap<String, String> excludeMap) {
        //无媒资版权(t_contentinfo表中的ISPublish还是Stauts??)  es
        //黑名单(甲方后台定义的黑名单表?) redis
        //无海报(哪里可以获取列表) ?????
        //[点击排除]名单过滤(本地缓存) redis
        //[曝光排除]名单过滤(本地缓存) redis
        //[人工干预]集合名单过滤(计算数量时已经获取过) redis
        //[点击必推]名单过滤(计算数量时已经获取过) redis
        //当前页面媒资名单过滤(category_content表中不能重复)   redis
        //[其他智能推荐位]之间的排重过滤：用户实时行为 + 历史行为推荐位(同样为冷启动推荐位) (计算数量时已经获取过) redis
        //所有推荐页[运营部门编排推荐位] 媒资排重过滤(甲方数据库表--要确认?) redis
        //无评分的(仅针对电影)(content_info的score字段) es

        //获取策略信息
        SceneDO sceneDO = sceneDTO.getSceneDO();

        //ERP系统运营位去重 媒资排重
        String erpProvideKey = String.format(RedisConstant.ERP_PROVIDED, sceneDO.getId());
        if (redisService.exists(erpProvideKey)) {
            String erpProvideKeyValue = redisService.get(erpProvideKey);
            Map erpProvideMap = (Map) JSONObject.parseObject(erpProvideKeyValue, HashMap.class);
            //循环遍历map
            if (null != erpProvideMap) {
                for (Object mediaCode : erpProvideMap.keySet()) {
                    excludeMap.put(mediaCode.toString(), "");
                }
            }
        }
        //场景是否支持其他页面智能推荐排除
        if (sceneDO.getSupportSceneFilter()) {
            String sceneKeysString = redisService.getHash(RedisConstant.HASH_SCENE_KEY, "all_keys");
            if (StringUtils.isNotEmpty(sceneKeysString)) {
                Set<String> providedKeys = JSONObject.parseObject(sceneKeysString, HashSet.class);
                for (String sceneId : providedKeys) {
                    if (sceneId.equals(sceneDO.getId().toString())) {
                        continue;
                    }
                    String providedKey = String.format(RedisConstant.PROVIDED, userDbId == null ? userId : userDbId.toString(), Integer.parseInt(sceneId));
                    if (!redisService.exists(providedKey)) {
                        continue;
                    }
                    String keyValue = redisService.get(providedKey);
                    Map map = (Map) JSONObject.parseObject(keyValue, HashMap.class);
                    //循环遍历map
                    if (null != map) {
                        for (Object mediaCode : map.keySet()) {
                            excludeMap.put(mediaCode.toString(), "");
                        }
                    }
                }
            }
        }

        //冷启动不需要点击过滤跟曝光过滤
        if (isCodeBoot) {
            return excludeMap;
        }
        //场景是否支持点击过滤
        if (sceneDO.getSupportClickFilter()) {
            String clickExcludeKey = String.format(RedisConstant.CLICK_EXCLUDE, userDbId, sceneDO.getId());
            if (redisService.exists(clickExcludeKey)) {
                String clickExcludeKeyValue = redisService.get(clickExcludeKey);
                Map clickExcludeMap = (Map) JSONObject.parseObject(clickExcludeKeyValue, HashMap.class);
                //获取点击过滤的信息
                List<StrategyClickFilterDO> strategyClickFilterList = sceneDTO.getRecommendStrategyDetail().getStrategyClickFilterList();
                //循环遍历map
                for (Object mediaCode : clickExcludeMap.keySet()) {
                    Integer freezeDays = ConstantUtil.THIRTY;
                    ClickFilterDTO clickFilter = (ClickFilterDTO) clickExcludeMap.get(mediaCode);
                    if (!CollectionUtils.isEmpty(strategyClickFilterList)) {
                        for (StrategyClickFilterDO strategyClickFilterDO : strategyClickFilterList) {
                            if (strategyClickFilterDO.getMediaType().equals(clickFilter.getMediaType())) {
                                freezeDays = strategyClickFilterDO.getFreezeDays();
                                break;
                            }
                        }

                    }
                    if (DateUtil.parse(clickFilter.getDate()).isAfter(LocalDateTime.now().minusDays(freezeDays))) {
                        excludeMap.put(mediaCode.toString(), "");
                    }
                }
            }
        }

        //场景是否支持曝光排除
        if (sceneDO.getSupportBrowseFilter()) {
            String exposurekExcludeKey = String.format(RedisConstant.EXPOSURE_EXCLUDE, userDbId, sceneDO.getId());
            if (redisService.exists(exposurekExcludeKey)) {
                String exposurekExcludeKeyValue = redisService.get(exposurekExcludeKey);
                Map exposurekExcludeMap = (Map) JSONObject.parseObject(exposurekExcludeKeyValue, HashMap.class);
                //获取点击过滤的信息
                StrategyBrowseFilterDO strategyBrowseFilterDO = sceneDTO.getRecommendStrategyDetail().getStrategyBrowseFilterDO();
                Integer freezeDays = ConstantUtil.THIRTY;
                if (null != strategyBrowseFilterDO && null != strategyBrowseFilterDO.getFreezeDays()) {
                    freezeDays = strategyBrowseFilterDO.getFreezeDays();
                }
                //循环遍历map
                for (Object mediaCode : exposurekExcludeMap.keySet()) {
                    if (DateUtil.parse(exposurekExcludeMap.get(mediaCode).toString()).isAfter(LocalDateTime.now().minusDays(freezeDays))) {
                        excludeMap.put(mediaCode.toString(), "");
                    }
                }
            }
        }
        return excludeMap;
    }

    /**
     * 获取题材偏好的top媒资类型的权重
     *
     * @return
     */
    public boolean calculateTopMediaWeight(List<MediaTypeTopDTO> mediaTypeTopList, BigDecimal allScore) {
        //如果智能推荐中有 没有题材的类型,则都不计算题材
        boolean isCalculateTopKind = true;
        //计算权重 题材偏好:通过es计算好的score算出类型跟题材的权重
        for (MediaTypeTopDTO mediaTypeTopDTO : mediaTypeTopList) {
            mediaTypeTopDTO.setWeight(mediaTypeTopDTO.getScore().divide(allScore, 2, BigDecimal.ROUND_HALF_UP));
            //如果是直播等没有题材的情况,则只推荐类型相关的
            if (!CollectionUtils.isEmpty(mediaTypeTopDTO.getMediaKindList())) {
                BigDecimal allKindScore = BigDecimal.ZERO;
                for (MediaKindTopDTO mediaKindTopDTO : mediaTypeTopDTO.getMediaKindList()) {
                    allKindScore = allKindScore.add(mediaKindTopDTO.getScore());
                }
                for (MediaKindTopDTO mediaKindTopDTO : mediaTypeTopDTO.getMediaKindList()) {
                    mediaKindTopDTO.setWeight(mediaKindTopDTO.getScore().divide(allKindScore, 2, BigDecimal.ROUND_HALF_UP));
                }
            } else {
                isCalculateTopKind = false;
            }
        }
        return isCalculateTopKind;
    }

    /**
     * 获取题材偏好的top媒资类型
     *
     * @param userDbId
     * @param sceneDTO
     * @return
     */
    public Pair<List<MediaTypeTopDTO>, BigDecimal> getTopMediaType(Integer userDbId, SceneDTO sceneDTO, Boolean isRealTime) {
        //从缓存中取出当前用户top5的类型(包含每个类型的top3的题材),并且根据后台策略筛选,找到筛选的top的类型

        List<MediaTypeTopDTO> mediaTypeTopList = new ArrayList<>();
        BigDecimal allScore = BigDecimal.ZERO;
        List<StrategyMediaTypeDO> strategyMediaTypeList = sceneDTO.getRecommendStrategyDetail().getStrategyMediaTypeList();
        if (CollectionUtils.isEmpty(strategyMediaTypeList)) {
            //策略没有设置,则返回
            return new Pair<>(mediaTypeTopList, allScore);
        }

        String mediaTypeTop5Key = null;
        if (isRealTime) {
            mediaTypeTop5Key = String.format(RedisConstant.MEDIA_TYPE_REAL_TIME_TOP, userDbId);
        } else {
            mediaTypeTop5Key = String.format(RedisConstant.MEDIA_TYPE_TOP5, userDbId);
        }
        String mediaTypeTop5Value = redisService.get(mediaTypeTop5Key);
        if (StringUtils.isNotEmpty(mediaTypeTop5Value)) {
            //获取数据列表跟后台配置列表中的交集,取top3
            JSONArray ja = JSONArray.parseArray(mediaTypeTop5Value);
            List<MediaTypeTopDTO> mediaTypeTop5List = ja.toJavaList(MediaTypeTopDTO.class);
            for (MediaTypeTopDTO mediaTypeTop5 : mediaTypeTop5List) {
                for (StrategyMediaTypeDO strategyMediaTypeDO : strategyMediaTypeList) {
                    if (mediaTypeTop5.getMediaType().equals(strategyMediaTypeDO.getMediaType())) {
                        mediaTypeTop5.setChargeRatio(strategyMediaTypeDO.getChargeRatio());
                        mediaTypeTopList.add(mediaTypeTop5);
                        allScore = allScore.add(mediaTypeTop5.getScore());
                        break;
                    }
                }
                if (mediaTypeTopList.size() >= ConstantUtil.THREE) {
                    break;
                }
            }
        }
        if (mediaTypeTopList.size() == 0) {
            //数据找不到则设置默认值top3类型
            return getTopMediaType(sceneDTO);
        }
        Pair<List<MediaTypeTopDTO>, BigDecimal> pair = new Pair<>(mediaTypeTopList, allScore);
        return pair;
    }

    /**
     * 获取题材偏好的top媒资类型
     *
     * @param sceneDTO
     * @return
     */
    public Pair<List<MediaTypeTopDTO>, BigDecimal> getTopMediaType(SceneDTO sceneDTO) {
        //从缓存中取出当前用户top5的类型(包含每个类型的top3的题材),并且根据后台策略筛选,找到筛选的top的类型

        List<MediaTypeTopDTO> mediaTypeTopList = new ArrayList<>();
        BigDecimal allScore = BigDecimal.ZERO;
        List<StrategyMediaTypeDO> strategyMediaTypeList = sceneDTO.getRecommendStrategyDetail().getStrategyMediaTypeList();
        if (CollectionUtils.isEmpty(strategyMediaTypeList)) {
            //策略没有设置,则返回
            return new Pair<>(mediaTypeTopList, allScore);
        }

        //数据找不到则设置默认值top3类型
        BigDecimal score = new BigDecimal(100).divide(new BigDecimal(strategyMediaTypeList.size()), 2, BigDecimal.ROUND_HALF_UP);
        for (StrategyMediaTypeDO strategyMediaTypeDO : strategyMediaTypeList) {
            MediaTypeTopDTO mediaTypeTopDTO = new MediaTypeTopDTO();
            mediaTypeTopDTO.setChargeRatio(strategyMediaTypeDO.getChargeRatio());
            mediaTypeTopDTO.setScore(score);
            mediaTypeTopDTO.setMediaType(strategyMediaTypeDO.getMediaType());
            mediaTypeTopDTO.setWeight(strategyMediaTypeDO.getWeight());
            mediaTypeTopList.add(mediaTypeTopDTO);
            allScore = allScore.add(score);
            if (mediaTypeTopList.size() >= ConstantUtil.THREE) {
                break;
            }
        }
        return new Pair<>(mediaTypeTopList, allScore);

    }

    /**
     * 获取题材偏好的top媒资类型(根据具体媒资获取top3类型)
     *
     * @param sceneDTO
     * @param media
     * @return
     */
    public Pair<List<MediaTypeTopDTO>, BigDecimal> getTopMediaType(SceneDTO sceneDTO, MediaDTO media) {
        List<MediaTypeTopDTO> mediaTypeTopList = new ArrayList<>();
        BigDecimal allScore = new BigDecimal(100);
        MediaTypeTopDTO mediaTypeTopDTO = new MediaTypeTopDTO();
        mediaTypeTopDTO.setMediaType(media.getMediaType());
        mediaTypeTopDTO.setScore(new BigDecimal(100));
        List<MediaKindTopDTO> mediaKindList = new ArrayList<>();
        //贵州小程序题材暂时不考虑
//        if (null != media.getMediaKindId() && media.getMediaKindId().length > 0) {
//            Arrays.stream(media.getMediaKindId()).forEach(s -> {
//                MediaKindTopDTO mediaKindTopDTO = new MediaKindTopDTO();
//                if (StringUtils.isNotEmpty(s)) {
//                    try {
//                        mediaKindTopDTO.setMediaKind(Integer.parseInt(s));
//                        mediaKindList.add(mediaKindTopDTO);
//                    } catch (Exception e) {
//
//                    }
//                }
//            });
//        }
        if (!CollectionUtils.isEmpty(mediaKindList)) {
            mediaKindList.forEach(mediaKindTopDTO -> {
                mediaKindTopDTO.setScore(new BigDecimal(100).divide(new BigDecimal(mediaKindList.size()), 2, BigDecimal.ROUND_HALF_UP));
            });
        }
        mediaTypeTopDTO.setMediaKindList(mediaKindList);
        //获取chargeRatio
        mediaTypeTopDTO.setChargeRatio(new BigDecimal(0.7));
        List<StrategyMediaTypeDO> strategyMediaTypeList = sceneDTO.getRecommendStrategyDetail().getStrategyMediaTypeList();
        for (StrategyMediaTypeDO strategyMediaTypeDO : strategyMediaTypeList) {
            if (mediaTypeTopDTO.getMediaType().equals(strategyMediaTypeDO.getMediaType())) {
                mediaTypeTopDTO.setChargeRatio(strategyMediaTypeDO.getChargeRatio());
                break;
            }
        }
        mediaTypeTopList.add(mediaTypeTopDTO);
        Pair<List<MediaTypeTopDTO>, BigDecimal> pair = new Pair<>(mediaTypeTopList, allScore);
        return pair;
    }

    /**
     * 计算推荐引擎的推荐数量
     *
     * @param userDbId
     * @param sceneDTO
     * @param size
     * @return
     */
    public List<RecommendEngineDTO> calculateRecommendEngineSize(Integer userDbId, String mediaCode, SceneDTO sceneDTO, Integer size) {
        //根据推荐引擎,获取引擎列表,计算出所有引擎下详细推荐数量
        //核心算法 权重从小打到排序
        List<StrategyRecommendEngineDO> engineDOList = sceneDTO.getRecommendStrategyDetail().getStrategyRecommendEngineList();
        List<RecommendEngineDTO> recommendEngines = engineDOList.stream().map(e -> {
            RecommendEngineDTO recommendEngine = new RecommendEngineDTO();
            BeanUtils.copyProperties(e, recommendEngine);
            return recommendEngine;
        }).collect(Collectors.toList());

        //计算出每个推荐引擎推荐的数量
        CalculateCoreUtil.calculateSize(recommendEngines, size);

        for (RecommendEngineDTO recommendEngine : recommendEngines) {
            //各推荐引擎推荐的数量
            int recommendEngineSize = recommendEngine.getSize();
            if (recommendEngineSize <= 0) {
                continue;
            }
            //题材偏好推荐引擎: 媒资类型,媒资题材,收费付费数量计算
            if (RecommendEngineEnum.MEDIA_PREFERENCE.intValue().equals(recommendEngine.getRecommendEngine()) || RecommendEngineEnum.REAL_TIME_PREFERENCE.intValue().equals(recommendEngine.getRecommendEngine())) {
                Pair<List<MediaTypeTopDTO>, BigDecimal> mediaTypeTopPair = null;
                //题材偏好 推荐引擎的数量
                if (RecommendEngineEnum.MEDIA_PREFERENCE.intValue().equals(recommendEngine.getRecommendEngine())) {
                    MediaDTO media = null;
                    if (null != mediaCode) {
                        String mediaString = redisService.get(String.format(RedisConstant.MEDIA_KEY, mediaCode));
                        media = JSONObject.parseObject(mediaString, MediaDTO.class);
                    }
                    //详情页推荐
                    if (media != null) {
                        mediaTypeTopPair = getTopMediaType(sceneDTO, media);
                    } else {
                        //支持用户偏好的情况
                        if (sceneDTO.getSceneDO().getSupportUserFavour()) {
                            mediaTypeTopPair = getTopMediaType(userDbId, sceneDTO, false);
                        } else {
                            //不支持用户偏好的情况(排行场景)
                            mediaTypeTopPair = getTopMediaType(sceneDTO);
                        }
                    }
                } else {
                    //实时推荐
                    mediaTypeTopPair = getTopMediaType(userDbId, sceneDTO, true);
                }

                // 如果总分为0或者列表为空,默认走冷启动
                List<MediaTypeTopDTO> mediaTypeTopList = mediaTypeTopPair.getKey();
                BigDecimal allScore = mediaTypeTopPair.getValue();
                if (CollectionUtils.isEmpty(mediaTypeTopList) || allScore.compareTo(BigDecimal.ZERO) == 0) {
                    if (sceneDTO.getSceneDO().getSupportColdBoot()) {
                        CalculateCoreUtil.setCodeBootTypeKindSizeList(recommendEngine, sceneDTO);
                        recommendEngine.setIsCalculateKind(false);
                    }
                    break;
                }

                //计算权重 题材偏好:通过es计算好的score算出类型跟题材的权重
                //boolean isCalculateKind = calculateTopMediaWeight(mediaTypeTopList, allScore);
                boolean isCalculateKind = false;
                //计算每个类型的数量
                CalculateCoreUtil.calculateSize(mediaTypeTopList, recommendEngineSize);
                //计算各类型中的题材数量逻辑
                List<MediaChargeFreeDTO> mediaChargeFreeList = new ArrayList<>();
                for (MediaTypeTopDTO mediaTypeTop : mediaTypeTopList) {
                    //获取计算好的类型的数量
                    int typeSize = mediaTypeTop.getSize();
                    if (typeSize <= 0) continue;
                    //是否计算Kind
                    if (isCalculateKind) {
                        List<MediaKindTopDTO> top3Themes = mediaTypeTop.getMediaKindList();
                        //直播以外的其他类型 计算每个题材的数量
                        CalculateCoreUtil.calculateSize(top3Themes, typeSize);
                        for (MediaKindTopDTO mediaKindTop : top3Themes) {
                            //计算每个题材收费免费数量
                            mediaChargeFreeList.addAll(CalculateCoreUtil.calculateChargeFreeSize(mediaTypeTop, mediaKindTop, mediaKindTop.getSize()));
                        }
                    } else {
                        //计算每个类型收费免费数量
                        mediaChargeFreeList.addAll(CalculateCoreUtil.calculateChargeFreeSize(mediaTypeTop, null, mediaTypeTop.getSize()));
                    }
                }

                //类型中设置所有数量的列表
                recommendEngine.setMediaTypeList(mediaTypeTopList);
                //类型中设置所有数量的列表
                recommendEngine.setMediaChargeFreeList(mediaChargeFreeList);
                //是否计算题材
                recommendEngine.setIsCalculateKind(isCalculateKind);

            }
        }
        return recommendEngines;
    }

    /**
     * 推荐逻辑
     *
     * @param userDbId
     * @param sceneDTO
     * @param size
     * @param mediaId
     * @return
     */
    public CommonResponse<List<IPTVMediaVO>> recommend(LinkedHashMap<Integer, IPTVMediaVO> resultMap, String userId, Integer userDbId, SceneDTO sceneDTO, Integer size, String mediaCode) {
//        log.info("recommend: userDbId={}, sceneDTO={}, size={},mediaId={}", userDbId, JSONObject.toJSONString(sceneDTO), size, mediaId);
        AtomicLong begin = new AtomicLong(System.currentTimeMillis());
        //1黑名单排除
        LinkedHashMap<String, String> excludeMap = blackList(sceneDTO);

        //2 获取人工干预
        resultMap = manualList(resultMap, size, sceneDTO, false, excludeMap);
        if (size - resultMap.keySet().size() <= ConstantUtil.ZERO) {
            return CommonResponse.success(resultMap.values().stream().sorted((o1, o2) -> {
                if (o1.getShowType().equals(MediaShowEnum.MANUAL_TOP.intValue()) || o2.getShowType().equals(MediaShowEnum.MANUAL_TOP.intValue())) {
                    return o1.getShowType().compareTo(o2.getShowType());
                } else {
                    Float o1Score = o1.getScore() == null ? 0 : o1.getScore();
                    Float o2Score = o2.getScore() == null ? 0 : o2.getScore();
                    return o2Score.compareTo(o1Score);
                }
            }).collect(Collectors.toList()));
        }
        //3 获取点击必推送
        resultMap = clickMustList(resultMap, size, sceneDTO, userDbId);
        if (size - resultMap.keySet().size() <= ConstantUtil.ZERO) {
            return CommonResponse.success(resultMap.values().stream().sorted((o1, o2) -> {
                if (o1.getShowType().equals(MediaShowEnum.MANUAL_TOP.intValue()) || o2.getShowType().equals(MediaShowEnum.MANUAL_TOP.intValue())) {
                    return o1.getShowType().compareTo(o2.getShowType());
                } else {
                    Float o1Score = o1.getScore() == null ? 0 : o1.getScore();
                    Float o2Score = o2.getScore() == null ? 0 : o2.getScore();
                    return o2Score.compareTo(o1Score);
                }
            }).collect(Collectors.toList()));
        }
        //4 排除列表
        excludeMap = excludeList(userId, sceneDTO, userDbId, false, excludeMap);
        //如果参数中的mediaId不为空,则放入排除列表
        if (null != mediaCode) {
            excludeMap.put(mediaCode, "");
        }

        //4 根据推荐引擎,获取引擎列表,计算出所有引擎下详细推荐数量
        List<RecommendEngineDTO> recommendEngines = calculateRecommendEngineSize(userDbId, mediaCode, sceneDTO, size - resultMap.keySet().size());
//        log.info("IR  计算出所有引擎下详细推荐数量：共耗时 {}毫秒", System.currentTimeMillis() - begin.get());
        begin = new AtomicLong(System.currentTimeMillis());
        //5 推荐 题材偏好 明星推荐 协同过滤 实时推荐
        for (RecommendEngineDTO recommendEngine : recommendEngines) {
            //当前推荐数量不大于0,则执行下个推荐引擎
            if (recommendEngine.getSize() <= ConstantUtil.ZERO) {
                continue;
            }
            //如果是题材偏好,typeList为空,则跳过推荐
            if (RecommendEngineEnum.MEDIA_PREFERENCE.intValue().equals(recommendEngine.getRecommendEngine()) || RecommendEngineEnum.REAL_TIME_PREFERENCE.intValue().equals(recommendEngine.getRecommendEngine())) {
                if (CollectionUtils.isEmpty(recommendEngine.getMediaTypeList())) {
                    continue;
                }
            }
            UserBehaviorRecommendService recommendService = (UserBehaviorRecommendService) SpringContextUtil.getBean(RecommendEngineEnum.valueOf(recommendEngine.getRecommendEngine()).unit());
            resultMap = recommendService.recommendAndSort(resultMap, userDbId, mediaCode, sceneDTO, excludeMap, recommendEngine, false);
            if (size - resultMap.keySet().size() <= ConstantUtil.ZERO) {
                break;
            }
        }
//        log.info("IR 智能推荐：共耗时 {}毫秒", System.currentTimeMillis() - begin.get());
        //返回结果
        return CommonResponse.success(resultMap.values().stream().sorted((o1, o2) -> {
            if (o1.getShowType().equals(MediaShowEnum.MANUAL_TOP.intValue()) || o2.getShowType().equals(MediaShowEnum.MANUAL_TOP.intValue())) {
                return o1.getShowType().compareTo(o2.getShowType());
            } else {
                Float o1Score = o1.getScore() == null ? 0 : o1.getScore();
                Float o2Score = o2.getScore() == null ? 0 : o2.getScore();
                return o2Score.compareTo(o1Score);
            }
        }).collect(Collectors.toList()));
    }

}
