package com.ubo.iptv.manage.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubo.iptv.core.CommonResponse;
import com.ubo.iptv.core.constant.RedisConstant;
import com.ubo.iptv.core.enums.*;
import com.ubo.iptv.core.util.DateUtil;
import com.ubo.iptv.entity.gdgd.MediaDTO;
import com.ubo.iptv.entity.gdgd.StrategySwitchDTO;
import com.ubo.iptv.manage.enums.ManageErrorEnum;
import com.ubo.iptv.manage.requset.StrategyDetailSaveDTO;
import com.ubo.iptv.manage.requset.StrategyManualAddDTO;
import com.ubo.iptv.manage.response.StrategyDetailVO;
import com.ubo.iptv.manage.response.StrategyInfoVO;
import com.ubo.iptv.manage.response.StrategyManualVO;
import com.ubo.iptv.manage.response.StrategyVO;
import com.ubo.iptv.manage.service.RedisService;
import com.ubo.iptv.manage.service.StrategyService;
import com.ubo.iptv.mybatis.gsgd.entity.GSContentinfoDO;
import com.ubo.iptv.mybatis.gsgd.mapper.GSContentPicUrlMapper;
import com.ubo.iptv.mybatis.gsgd.mapper.GSContentinfoMapper;
import com.ubo.iptv.mybatis.recommend.entity.*;
import com.ubo.iptv.mybatis.recommend.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: xuning
 * @Date: 2020-11-07
 */
@Service
@Slf4j
public class StrategyServiceImpl implements StrategyService {
    @Autowired
    private RedisService redisService;
    @Resource
    private StrategyManualMapper strategyManualMapper;
    @Resource
    private GSContentinfoMapper gsContentinfoMapper;
    @Resource
    private AccountMapper accountMapper;
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
    private StrategyMediaSortMapper strategyMediaSortMapper;
    @Resource
    private StrategyMediaTypeMapper strategyMediaTypeMapper;
    @Resource
    private StrategyRecommendEngineMapper strategyRecommendEngineMapper;
    @Resource
    private MediaBlacklistMapper mediaBlacklistMapper;
    @Resource
    private GSContentPicUrlMapper gsContentPicUrlMapper;

    @Override
    public CommonResponse<StrategyInfoVO> listStrategy(String ispId, Integer type, Page page) {
        IPage<StrategyDO> strategyDOPage = strategyMapper.selectPage(page, new LambdaQueryWrapper<StrategyDO>().eq(StrategyDO::getSysId, ispId).eq(StrategyDO::getType, type));
        List<StrategyDO> strategyDOList = strategyDOPage.getRecords();
        IPage<StrategyVO> strategyVOPage = new Page<>();
        List<StrategyVO> strategyVOList = new ArrayList<>();
        //????????????????????????id??????
        String key = RedisConstant.IR_CONFIG;
        String config = redisService.get(key);
        JSONObject js = JSONObject.parseObject(config);


        strategyDOList.forEach(strategyDO -> {
            SceneDO sceneDO = sceneMapper.selectOne(new LambdaQueryWrapper<SceneDO>().eq(SceneDO::getId, strategyDO.getSceneId()));
            AccountDO accountDO = accountMapper.selectOne(new LambdaQueryWrapper<AccountDO>().eq(AccountDO::getId, strategyDO.getCreateorId()));
            StrategyVO strategyVO = new StrategyVO();
            strategyVO.setSceneId(strategyDO.getSceneId());
            strategyVO.setSceneName(null == sceneDO ? "" : sceneDO.getName());
            strategyVO.setStrategyId(strategyDO.getId());
            strategyVO.setStrategyName(strategyDO.getName());
            strategyVO.setIspId(strategyDO.getSysId());
            strategyVO.setIspName(SysEnum.description(strategyDO.getSysId()));
            strategyVO.setCreateType(CreateTypeEnum.description(CreateTypeEnum.DEFAULT.value()));
            strategyVO.setCreator(null == accountDO ? "" : accountDO.getName());
            strategyVO.setCreateTime(DateUtil.format(strategyDO.getCreateTime()));
            String sceneIRKey = String.format(RedisConstant.SINGLE_SCENE, sceneDO.getId());
            String sceneIRValue = js.getString(sceneIRKey);
            StrategySwitchDTO strategySwitchDTO = JSONObject.parseObject(sceneIRValue, StrategySwitchDTO.class);
            strategyVO.setOpenFlag(strategySwitchDTO == null ? false : strategySwitchDTO.getOpenCloseFlag() == 1 ? true : false);
            strategyVOList.add(strategyVO);
        });
        strategyVOPage.setRecords(strategyVOList);
        iPage(strategyDOPage, strategyVOPage);
        //??????
        String allIRKey = RedisConstant.ALL_SCENE;
        String allIRValue = js.getString(allIRKey);
        StrategySwitchDTO strategySwitchDTO = JSONObject.parseObject(allIRValue, StrategySwitchDTO.class);
        StrategyInfoVO strategyInfoVO = new StrategyInfoVO();
        strategyInfoVO.setOpenFlag(strategySwitchDTO == null ? false : strategySwitchDTO.getOpenCloseFlag() == 1 ? true : false);
        strategyInfoVO.setPage(strategyVOPage);
        return CommonResponse.success(strategyInfoVO);
    }

    private IPage iPage(IPage resouce, IPage tar) {
        tar.setSize(resouce.getSize());
        tar.setCurrent(resouce.getCurrent());
        tar.setTotal(resouce.getTotal());
        tar.setPages(resouce.getPages());
        return tar;
    }

    @Override
    public CommonResponse<StrategyDetailVO> getStrategyDetail(Long strategyId) {
        StrategyDetailVO strategyDetailVO = new StrategyDetailVO();
        //??????????????????
        StrategyDO strategyDO = strategyMapper.selectOne(new LambdaQueryWrapper<StrategyDO>().eq(StrategyDO::getId, strategyId));
        if (strategyDO == null) {
            CommonResponse.build(IPTVErrorCode.NO_STRATEGY_SET);
        }
        strategyDetailVO.setStrategyId(strategyId);
        //??????????????????
        SceneDO sceneDO = sceneMapper.selectOne(new LambdaQueryWrapper<SceneDO>().eq(SceneDO::getId, strategyDO.getSceneId()));
        if (sceneDO == null) {
            CommonResponse.build(IPTVErrorCode.NO_SCENE_SET);
        }
        strategyDetailVO.setSceneId(sceneDO.getId());
        //????????????????????????id??????
        String key = RedisConstant.IR_CONFIG;
        String config = redisService.get(key);
        JSONObject js = JSONObject.parseObject(config);
        String sceneIRKey = String.format(RedisConstant.SINGLE_SCENE, sceneDO.getId());
        String sceneIRValue = js.getString(sceneIRKey);
        StrategySwitchDTO strategySwitchDTO = JSONObject.parseObject(sceneIRValue, StrategySwitchDTO.class);
        strategyDetailVO.setOpenFlag(strategySwitchDTO == null ? false : strategySwitchDTO.getOpenCloseFlag() == 1 ? true : false);
        strategyDetailVO.setCategoryId(strategySwitchDTO == null ? null : strategySwitchDTO.getCategoryId());
        // ??????????????????
        strategyDetailVO.setBase_strategyName(strategyDO.getName());
        //??????????????????
        List<StrategyMediaTypeDO> strategyMediaTypeList = strategyMediaTypeMapper.selectList(new LambdaQueryWrapper<StrategyMediaTypeDO>().eq(StrategyMediaTypeDO::getStrategyId, strategyDO.getId()));
        strategyMediaTypeList.forEach(strategyMediaTypeDO -> {
            if (MediaTypeEnum.MOVIE.intValue().equals(strategyMediaTypeDO.getMediaType())) {
                // ????????????-????????????_??????????????????
                strategyDetailVO.setSiteInfo_filmChargeRatio(strategyMediaTypeDO.getChargeRatio().multiply(new BigDecimal(100)).intValue());
                strategyDetailVO.setSiteInfo_filmWeight(strategyMediaTypeDO.getWeight().multiply(new BigDecimal(100)).intValue());
            }
            if (MediaTypeEnum.SERIES.intValue().equals(strategyMediaTypeDO.getMediaType())) {
                // ????????????-????????????_?????????????????????
                strategyDetailVO.setSiteInfo_dramaChargeRatio(strategyMediaTypeDO.getChargeRatio().multiply(new BigDecimal(100)).intValue());
                strategyDetailVO.setSiteInfo_dramaWeight(strategyMediaTypeDO.getWeight().multiply(new BigDecimal(100)).intValue());
            }
//            if (MediaTypeEnum.VARIETY.intValue().equals(strategyMediaTypeDO.getMediaType())) {
//                // ????????????-????????????_??????????????????
//                strategyDetailVO.setSiteInfo_zyChargeRatio(strategyMediaTypeDO.getChargeRatio().multiply(new BigDecimal(100)).intValue());
//                strategyDetailVO.setSiteInfo_zyWeight(strategyMediaTypeDO.getWeight().multiply(new BigDecimal(100)).intValue());
//            }
            if (MediaTypeEnum.CHILDREN.intValue().equals(strategyMediaTypeDO.getMediaType())) {
                // ????????????-????????????_??????????????????
                strategyDetailVO.setSiteInfo_childChargeRatio(strategyMediaTypeDO.getChargeRatio().multiply(new BigDecimal(100)).intValue());
                strategyDetailVO.setSiteInfo_childWeight(strategyMediaTypeDO.getWeight().multiply(new BigDecimal(100)).intValue());
            }
            if (MediaTypeEnum.COMIC.intValue().equals(strategyMediaTypeDO.getMediaType())) {
                // ????????????-????????????_??????????????????
                strategyDetailVO.setSiteInfo_dmChargeRatio(strategyMediaTypeDO.getChargeRatio().multiply(new BigDecimal(100)).intValue());
                strategyDetailVO.setSiteInfo_dmWeight(strategyMediaTypeDO.getWeight().multiply(new BigDecimal(100)).intValue());
            }
            if (MediaTypeEnum.LIVE.intValue().equals(strategyMediaTypeDO.getMediaType())) {
                // ????????????-????????????_??????????????????
                strategyDetailVO.setSiteInfo_zbChargeRatio(strategyMediaTypeDO.getChargeRatio().multiply(new BigDecimal(100)).intValue());
                strategyDetailVO.setSiteInfo_zbWeight(strategyMediaTypeDO.getWeight().multiply(new BigDecimal(100)).intValue());
            }
//            if (MediaTypeEnum.DOCUMENTARY.intValue().equals(strategyMediaTypeDO.getMediaType())) {
//                // ????????????-????????????_?????????????????????
//                strategyDetailVO.setSiteInfo_jlpChargeRatio(strategyMediaTypeDO.getChargeRatio().multiply(new BigDecimal(100)).intValue());
//                strategyDetailVO.setSiteInfo_jlpWeight(strategyMediaTypeDO.getWeight().multiply(new BigDecimal(100)).intValue());
//            }
        });
        //????????????
        List<StrategyRecommendEngineDO> strategyRecommendEngineList = strategyRecommendEngineMapper.selectList(new LambdaQueryWrapper<StrategyRecommendEngineDO>().eq(StrategyRecommendEngineDO::getStrategyId, strategyDO.getId()).orderByAsc(StrategyRecommendEngineDO::getWeight));
        strategyRecommendEngineList.forEach(strategyRecommendEngineDO -> {
            //????????????-??????????????????????????????
            if (RecommendEngineEnum.MEDIA_PREFERENCE.intValue().equals(strategyRecommendEngineDO.getRecommendEngine())) {
                strategyDetailVO.setRc_h_mediaRatio(strategyRecommendEngineDO.getWeight().multiply(new BigDecimal(100)).intValue());
            }
            //????????????-??????????????????????????????
            if (RecommendEngineEnum.STAR_PREFERENCE.intValue().equals(strategyRecommendEngineDO.getRecommendEngine())) {
                strategyDetailVO.setRc_h_starRatio(strategyRecommendEngineDO.getWeight().multiply(new BigDecimal(100)).intValue());
            }
            //????????????-????????????????????????????????????
            if (RecommendEngineEnum.COLLABORATIVE_FILTER.intValue().equals(strategyRecommendEngineDO.getRecommendEngine())) {
                strategyDetailVO.setRc_h_similarRatio(strategyRecommendEngineDO.getWeight().multiply(new BigDecimal(100)).intValue());
            }
            //????????????-??????????????????????????????
            if (RecommendEngineEnum.REAL_TIME_PREFERENCE.intValue().equals(strategyRecommendEngineDO.getRecommendEngine())) {
                strategyDetailVO.setRc_n_mediaRatio(strategyRecommendEngineDO.getWeight().multiply(new BigDecimal(100)).intValue());
            }
        });
        //??????????????????
        List<StrategyMediaSortDO> strategyMediaSortList = strategyMediaSortMapper.selectList(new LambdaQueryWrapper<StrategyMediaSortDO>().eq(StrategyMediaSortDO::getStrategyId, strategyDO.getId()).orderByAsc(StrategyMediaSortDO::getWeight));
        strategyMediaSortList.forEach(strategyMediaSortDO -> {
            //??????????????????-????????????????????????
            if (MediaSortEnum.BY_PLAY.intValue().equals(strategyMediaSortDO.getMediaSort())) {
                strategyDetailVO.setSort_hotRatio(strategyMediaSortDO.getWeight().multiply(new BigDecimal(100)).intValue());
            }
            //??????????????????-????????????????????????
            if (MediaSortEnum.BY_SEARCH.intValue().equals(strategyMediaSortDO.getMediaSort())) {
                strategyDetailVO.setSort_searchRatio(strategyMediaSortDO.getWeight().multiply(new BigDecimal(100)).intValue());
            }
            //??????????????????-????????????????????????
            if (MediaSortEnum.BY_SCORE.intValue().equals(strategyMediaSortDO.getMediaSort())) {
                strategyDetailVO.setSort_scoreRatio(strategyMediaSortDO.getWeight().multiply(new BigDecimal(100)).intValue());
            }
            //??????????????????-????????????????????????
            if (MediaSortEnum.BY_RELEASE_TIME.intValue().equals(strategyMediaSortDO.getMediaSort())) {
                strategyDetailVO.setSort_startTimeRatio(strategyMediaSortDO.getWeight().multiply(new BigDecimal(100)).intValue());
            }
            //??????????????????-????????????????????????
            if (MediaSortEnum.BY_PUBLIC_TIME.intValue().equals(strategyMediaSortDO.getMediaSort())) {
                strategyDetailVO.setSort_pubTimeRatio(strategyMediaSortDO.getWeight().multiply(new BigDecimal(100)).intValue());
            }
        });
        //????????????
        StrategyBrowseFilterDO strategyBrowseFilterDO = strategyBrowseFilterMapper.selectOne(new LambdaQueryWrapper<StrategyBrowseFilterDO>().eq(StrategyBrowseFilterDO::getStrategyId, strategyDO.getId()));
        if (null != strategyBrowseFilterDO) {
            //??????????????????-??????????????????
            strategyDetailVO.setExposure_upDay(strategyBrowseFilterDO.getBrowseDays());
            //??????????????????-???????????????
            strategyDetailVO.setExposure_downDay(strategyBrowseFilterDO.getFreezeDays());
            //??????????????????-??????????????????
            strategyDetailVO.setExposure_enable(StatusEnum.EFFECT.intValue());
        } else {
            //??????????????????-??????????????????
            strategyDetailVO.setExposure_enable(StatusEnum.INVALID.intValue());
        }
        //????????????
        List<StrategyClickFilterDO> strategyClickFilterList = strategyClickFilterMapper.selectList(new LambdaQueryWrapper<StrategyClickFilterDO>().eq(StrategyClickFilterDO::getStrategyId, strategyDO.getId()));
        if (CollectionUtils.isEmpty(strategyClickFilterList)) {
            //??????????????????-??????????????????
            strategyDetailVO.setClick_enable(StatusEnum.INVALID.intValue());
        } else {
            //??????????????????-??????????????????
            strategyDetailVO.setClick_enable(StatusEnum.EFFECT.intValue());
            strategyClickFilterList.forEach(strategyClickFilterDO -> {
                if (MediaTypeEnum.MOVIE.intValue().equals(strategyClickFilterDO.getMediaType())) {
                    // ??????????????????-??????-????????????
                    strategyDetailVO.setClick_movie_showTime(strategyClickFilterDO.getPlayMinutes());
                    // ??????????????????-??????-??????????????????
                    strategyDetailVO.setClick_movie_delayDay(strategyClickFilterDO.getIgnoreDays());
                    // ??????????????????-??????-???????????????
                    strategyDetailVO.setClick_movie_hideDay(strategyClickFilterDO.getFreezeDays());
                }
                if (MediaTypeEnum.SERIES.intValue().equals(strategyClickFilterDO.getMediaType())) {
                    // ??????????????????-?????????-????????????
                    strategyDetailVO.setClick_tv_showTime(strategyClickFilterDO.getPlayMinutes());
                    // ??????????????????-?????????-??????????????????
                    strategyDetailVO.setClick_tv_delayDay(strategyClickFilterDO.getIgnoreDays());
                    // ??????????????????-?????????-???????????????
                    strategyDetailVO.setClick_tv_hideDay(strategyClickFilterDO.getFreezeDays());
                }
//                if (MediaTypeEnum.VARIETY.intValue().equals(strategyClickFilterDO.getMediaType())) {
//                    // ??????????????????-??????-????????????
//                    strategyDetailVO.setClick_variety_showTime(strategyClickFilterDO.getPlayMinutes());
//                    // ??????????????????-??????-??????????????????
//                    strategyDetailVO.setClick_variety_delayDay(strategyClickFilterDO.getIgnoreDays());
//                    // ??????????????????-??????-???????????????
//                    strategyDetailVO.setClick_variety_hideDay(strategyClickFilterDO.getFreezeDays());
//                }
            });
        }
        //?????????
        StrategyColdBootDO strategyColdBootDO = strategyColdBootMapper.selectOne(new LambdaQueryWrapper<StrategyColdBootDO>().eq(StrategyColdBootDO::getStrategyId, strategyDO.getId()));
        if (null != strategyColdBootDO) {
            //?????????????????????????????????
            strategyDetailVO.setCold_clickNumber(strategyColdBootDO.getClickNums());
        }
        return CommonResponse.success(strategyDetailVO);
    }

    @Override
    public CommonResponse saveStrategyDetail(StrategyDetailSaveDTO dto) {
        //??????????????????
        StrategyDO strategyDO = strategyMapper.selectOne(new LambdaQueryWrapper<StrategyDO>().eq(StrategyDO::getId, dto.getStrategyId()));
        if (strategyDO == null) {
            return CommonResponse.build(IPTVErrorCode.NO_STRATEGY_SET);
        }
        // ????????????????????????
        strategyDO.setName(dto.getBase_strategyName());
        strategyMapper.updateById(strategyDO);

        //??????????????????
        //??????
        strategyMediaTypeMapper.delete(new LambdaQueryWrapper<StrategyMediaTypeDO>().eq(StrategyMediaTypeDO::getStrategyId, strategyDO.getId()));
        //??????
        for (MediaTypeEnum mediaTypeEnum : MediaTypeEnum.values()) {
            Integer chargeRatio = null;
            Integer weight = null;
            if (MediaTypeEnum.MOVIE.equals(mediaTypeEnum)) {
                // ????????????-????????????_??????????????????
                chargeRatio = dto.getSiteInfo_filmChargeRatio();
                //??????
                weight = dto.getSiteInfo_filmWeight();
            }
            if (MediaTypeEnum.SERIES.equals(mediaTypeEnum)) {
                // ????????????-????????????_?????????????????????
                chargeRatio = dto.getSiteInfo_dramaChargeRatio();
                //??????
                weight = dto.getSiteInfo_dramaWeight();
            }
//            if (MediaTypeEnum.VARIETY.equals(mediaTypeEnum)) {
//                // ????????????-????????????_??????????????????
//                chargeRatio = dto.getSiteInfo_zyChargeRatio();
//                //??????
//                weight = dto.getSiteInfo_zyWeight();
//            }
            if (MediaTypeEnum.CHILDREN.equals(mediaTypeEnum)) {
                // ????????????-????????????_??????????????????
                chargeRatio = dto.getSiteInfo_childChargeRatio();
                //??????
                weight = dto.getSiteInfo_childWeight();
            }
            if (MediaTypeEnum.COMIC.equals(mediaTypeEnum)) {
                // ????????????-????????????_??????????????????
                chargeRatio = dto.getSiteInfo_dmChargeRatio();
                //??????
                weight = dto.getSiteInfo_dmWeight();
            }
            if (MediaTypeEnum.LIVE.equals(mediaTypeEnum)) {
                // ????????????-????????????_??????????????????
                chargeRatio = dto.getSiteInfo_zbChargeRatio();
                //??????
                weight = dto.getSiteInfo_zbWeight();
            }
//            if (MediaTypeEnum.DOCUMENTARY.equals(mediaTypeEnum)) {
//                // ????????????-????????????_??????????????????
//                chargeRatio = dto.getSiteInfo_jlpChargeRatio();
//                //??????
//                weight = dto.getSiteInfo_jlpWeight();
//            }

            //????????????????????????????????????,?????????
            chargeRatio = null == chargeRatio ? 0 : chargeRatio;
            weight = null == weight ? 0 : weight;
            if (chargeRatio == 0 && weight == 0) {
                continue;
            }
            StrategyMediaTypeDO strategyMediaTypeDO = new StrategyMediaTypeDO();
            strategyMediaTypeDO.setStrategyId(dto.getStrategyId());
            strategyMediaTypeDO.setMediaType(mediaTypeEnum.intValue());
            // ????????????-????????????_?????????????????????
            strategyMediaTypeDO.setChargeRatio(0 == chargeRatio ? BigDecimal.ZERO : new BigDecimal(chargeRatio).divide(new BigDecimal(100)));
            //??????
            strategyMediaTypeDO.setWeight(0 == weight ? BigDecimal.ZERO : new BigDecimal(weight).divide(new BigDecimal(100)));
            strategyMediaTypeMapper.insert(strategyMediaTypeDO);
        }
        //????????????
        //??????
        strategyRecommendEngineMapper.delete(new LambdaQueryWrapper<StrategyRecommendEngineDO>().eq(StrategyRecommendEngineDO::getStrategyId, strategyDO.getId()));
        //??????
        for (RecommendEngineEnum recommendEngineEnum : RecommendEngineEnum.values()) {
            Integer weight = null;
            //????????????-??????????????????????????????
            if (RecommendEngineEnum.MEDIA_PREFERENCE.equals(recommendEngineEnum)) {
                weight = dto.getRc_h_mediaRatio();
            }
            //????????????-??????????????????????????????
            if (RecommendEngineEnum.STAR_PREFERENCE.equals(recommendEngineEnum)) {
                weight = dto.getRc_h_starRatio();
            }
            //????????????-????????????????????????????????????
            if (RecommendEngineEnum.COLLABORATIVE_FILTER.equals(recommendEngineEnum)) {
                weight = dto.getRc_h_similarRatio();
            }
            //????????????-??????????????????????????????
            if (RecommendEngineEnum.REAL_TIME_PREFERENCE.equals(recommendEngineEnum)) {
                weight = dto.getRc_n_mediaRatio();
            }
            //????????????????????????,?????????
            weight = null == weight ? 0 : weight;
            if (weight == 0) {
                continue;
            }
            StrategyRecommendEngineDO strategyRecommendEngineDO = new StrategyRecommendEngineDO();
            strategyRecommendEngineDO.setStrategyId(strategyDO.getId());
            strategyRecommendEngineDO.setWeight(0 == weight ? BigDecimal.ZERO : new BigDecimal(weight).divide(new BigDecimal(100)));
            strategyRecommendEngineDO.setRecommendEngine(recommendEngineEnum.intValue());
            strategyRecommendEngineMapper.insert(strategyRecommendEngineDO);

        }
        //??????????????????
        //??????
        strategyMediaSortMapper.delete(new LambdaQueryWrapper<StrategyMediaSortDO>().eq(StrategyMediaSortDO::getStrategyId, strategyDO.getId()));
        //??????
        for (MediaSortEnum mediaSortEnum : MediaSortEnum.values()) {
            Integer weight = null;
            //??????????????????-????????????????????????
            if (MediaSortEnum.BY_PLAY.equals(mediaSortEnum)) {
                weight = dto.getSort_hotRatio();
            }
            //??????????????????-????????????????????????
            if (MediaSortEnum.BY_SEARCH.equals(mediaSortEnum)) {
                weight = dto.getSort_searchRatio();
            }
            //??????????????????-????????????????????????
            if (MediaSortEnum.BY_SCORE.equals(mediaSortEnum)) {
                weight = dto.getSort_scoreRatio();
            }
            //??????????????????-????????????????????????
            if (MediaSortEnum.BY_RELEASE_TIME.equals(mediaSortEnum)) {
                weight = dto.getSort_startTimeRatio();
            }
            //??????????????????-????????????????????????
            if (MediaSortEnum.BY_PUBLIC_TIME.equals(mediaSortEnum)) {
                weight = dto.getSort_pubTimeRatio();
            }
            //????????????????????????,?????????
            weight = null == weight ? 0 : weight;
            if (weight == 0) {
                continue;
            }
            StrategyMediaSortDO strategyMediaSortDO = new StrategyMediaSortDO();
            strategyMediaSortDO.setStrategyId(strategyDO.getId());
            strategyMediaSortDO.setWeight(0 == weight ? BigDecimal.ZERO : new BigDecimal(weight).divide(new BigDecimal(100)));
            strategyMediaSortDO.setMediaSort(mediaSortEnum.intValue());
            strategyMediaSortMapper.insert(strategyMediaSortDO);

        }

        //????????????
        //??????
        strategyBrowseFilterMapper.delete(new LambdaQueryWrapper<StrategyBrowseFilterDO>().eq(StrategyBrowseFilterDO::getStrategyId, strategyDO.getId()));
        //??????
        if (StatusEnum.EFFECT.intValue().equals(dto.getExposure_enable())) {
            //??????
            if (dto.getExposure_upDay() != null || dto.getExposure_downDay() != null) {
                StrategyBrowseFilterDO strategyBrowseFilterDO = new StrategyBrowseFilterDO();
                strategyBrowseFilterDO.setStrategyId(strategyDO.getId());
                //??????????????????-??????????????????
                strategyBrowseFilterDO.setBrowseDays(dto.getExposure_upDay());
                //??????????????????-???????????????
                strategyBrowseFilterDO.setFreezeDays(dto.getExposure_downDay());
                strategyBrowseFilterMapper.insert(strategyBrowseFilterDO);
            }
        }
        //????????????
        //??????
        strategyClickFilterMapper.delete(new LambdaQueryWrapper<StrategyClickFilterDO>().eq(StrategyClickFilterDO::getStrategyId, strategyDO.getId()));
        //??????
        if (StatusEnum.EFFECT.intValue().equals(dto.getClick_enable())) {
            for (MediaTypeEnum mediaTypeEnum : MediaTypeEnum.values()) {
                // ??????????????????-??????-????????????
                Integer showTime = null;
                // ??????????????????-??????-??????????????????
                Integer delayDay = null;
                // ??????????????????-??????-???????????????
                Integer hideDay = null;
                if (MediaTypeEnum.MOVIE.equals(mediaTypeEnum)) {
                    // ??????????????????-??????-????????????
                    showTime = dto.getClick_movie_showTime();
                    // ??????????????????-??????-??????????????????
                    delayDay = dto.getClick_movie_delayDay();
                    // ??????????????????-??????-???????????????
                    hideDay = dto.getClick_movie_hideDay();
                }
                if (MediaTypeEnum.SERIES.equals(mediaTypeEnum)) {
                    // ??????????????????-?????????-????????????
                    showTime = dto.getClick_tv_showTime();
                    // ??????????????????-?????????-??????????????????
                    delayDay = dto.getClick_tv_delayDay();
                    // ??????????????????-?????????-???????????????
                    hideDay = dto.getClick_tv_hideDay();
                }
//                if (MediaTypeEnum.VARIETY.equals(mediaTypeEnum)) {
//                    // ??????????????????-??????-????????????
//                    showTime = dto.getClick_variety_showTime();
//                    // ??????????????????-??????-??????????????????
//                    delayDay = dto.getClick_variety_delayDay();
//                    // ??????????????????-??????-???????????????
//                    hideDay = dto.getClick_variety_hideDay();
//                }

                //???????????????,?????????
                if (null == showTime && null == delayDay && null == hideDay) {
                    continue;
                }
                StrategyClickFilterDO strategyClickFilterDO = new StrategyClickFilterDO();
                strategyClickFilterDO.setStrategyId(dto.getStrategyId());
                strategyClickFilterDO.setMediaType(mediaTypeEnum.intValue());
                strategyClickFilterDO.setPlayMinutes(showTime);
                strategyClickFilterDO.setFreezeDays(hideDay);
                strategyClickFilterDO.setIgnoreDays(delayDay);
                strategyClickFilterMapper.insert(strategyClickFilterDO);
            }
        }
        //?????????
        //??????
        strategyColdBootMapper.delete(new LambdaQueryWrapper<StrategyColdBootDO>().eq(StrategyColdBootDO::getStrategyId, strategyDO.getId()));
        //??????
        if (dto.getCold_clickNumber() != null) {
            StrategyColdBootDO strategyColdBootDO = new StrategyColdBootDO();
            strategyColdBootDO.setClickNums(dto.getCold_clickNumber());
            strategyColdBootDO.setStrategyId(strategyDO.getId());
            strategyColdBootMapper.insert(strategyColdBootDO);
        }
        return CommonResponse.SUCCESS;
    }

    @Override
    public CommonResponse<List<StrategyManualVO>> listManual(Long strategyId) {
        // ??????

        // ????????????????????????
        LambdaQueryWrapper<StrategyManualDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StrategyManualDO::getStrategyId, strategyId);
        wrapper.orderByDesc(StrategyManualDO::getId);
        List<StrategyManualDO> strategyManualDOList = strategyManualMapper.selectList(wrapper);

        List<StrategyManualVO> voList = new ArrayList<>();
        strategyManualDOList.forEach(s -> {
            StrategyManualVO vo = new StrategyManualVO();
            vo.setId(s.getId());
            vo.setMediaId(s.getMediaId());
            vo.setMediaCode(s.getMediaCode());
            vo.setMediaName(s.getMediaName());
            vo.setMediaType(s.getMediaType());
            vo.setMediaTypeName(MediaTypeEnum.description(s.getMediaType()));
            vo.setStatus(s.getTop() ? 1 : 0);
            vo.setStatusValue(s.getTop() ? "??????" : "??????");
            MediaBlacklistDO mediaBlacklistDO = mediaBlacklistMapper.selectOne(
                    new LambdaQueryWrapper<MediaBlacklistDO>()
                            .eq(MediaBlacklistDO::getMediaId, s.getMediaId())
                            .eq(MediaBlacklistDO::getStatus, true));
            if (null != mediaBlacklistDO) {
                int blackListInfoCount = mediaBlacklistMapper.selectCount(
                        new LambdaQueryWrapper<MediaBlacklistDO>()
                                .eq(MediaBlacklistDO::getMediaId, s.getMediaId())
                                .eq(MediaBlacklistDO::getStatus, true)
                                .ge(null != mediaBlacklistDO.getExpireTime(), MediaBlacklistDO::getExpireTime, LocalDateTime.now())
                                .le(null != mediaBlacklistDO.getStartTime(), MediaBlacklistDO::getStartTime, LocalDateTime.now()));
                if (blackListInfoCount > 0) {
                    vo.setStatusValue("???????????????," + vo.getStatusValue() + "?????????");
                }
            }
            vo.setPosterUrl(s.getPosterUrl());
            vo.setEndTime(DateUtil.format(s.getExpireTime()));
            vo.setStartTime(DateUtil.format(s.getStartTime()));

            //cpName
            String mediaInfoString = redisService.get(String.format(RedisConstant.MEDIA_KEY, s.getMediaId()));
            if (StringUtils.isNotEmpty(mediaInfoString)) {
                MediaDTO media = JSONObject.parseObject(mediaInfoString, MediaDTO.class);
                vo.setCpName(media.getCpName());
            }

            voList.add(vo);
        });
        return CommonResponse.success(voList);
    }

    @Override
    public CommonResponse<List<StrategyManualVO>> searchMedia(Long strategyId, String content) {
        StrategyDO strategyDO = strategyMapper.selectById(strategyId);
        if (strategyDO == null) {
            return CommonResponse.build(CommonCodeEnum.GLOBAL_RECORD_NOT_EXIST, "??????");
        }
        List<GSContentinfoDO> contentinfoDOList = gsContentinfoMapper.searchMedia(SysEnum.getSpIdBySysId(strategyDO.getSysId()), content);
        List<StrategyManualVO> voList = new ArrayList<>();
        contentinfoDOList.forEach(s -> {
            // ???????????????media??????
            String mediaInfoString = redisService.get(String.format(RedisConstant.MEDIA_KEY, s.getId()));
            if (StringUtils.isNotEmpty(mediaInfoString)) {
                MediaDTO mediaDTO = JSONObject.parseObject(mediaInfoString, MediaDTO.class);
                // ??????????????????????????????
                StrategyManualDO strategyManualDO = strategyManualMapper.selectOne(new LambdaQueryWrapper<StrategyManualDO>()
                        .eq(StrategyManualDO::getStrategyId, strategyId)
                        .eq(StrategyManualDO::getMediaId, mediaDTO.getMediaId()));

                StrategyManualVO vo = new StrategyManualVO();
                vo.setMediaId(mediaDTO.getMediaId());
                vo.setMediaCode(mediaDTO.getMediaCode());
                vo.setMediaName(mediaDTO.getName());
                vo.setMediaType(mediaDTO.getMediaType());
                vo.setMediaTypeName(mediaDTO.getMediaTypeName());
                vo.setCpName(mediaDTO.getCpName());
                if (strategyManualDO != null) {
                    vo.setStatus(strategyManualDO.getTop() ? 1 : 0);
                    vo.setEndTime(DateUtil.format(strategyManualDO.getExpireTime()));
                }
                voList.add(vo);
            }
        });
        return CommonResponse.success(voList);
    }

    @Override
    public CommonResponse addManual(StrategyManualAddDTO dto) {
        // ?????????????????????/??????
        Integer count = strategyManualMapper.selectCount(new LambdaQueryWrapper<StrategyManualDO>()
                .eq(StrategyManualDO::getStrategyId, dto.getStrategyId())
                .eq(StrategyManualDO::getMediaId, dto.getMediaId()));
        if (count > 0) {
            //??????????????????/????????????
            updateManualStatus(dto);
            LambdaUpdateWrapper<StrategyManualDO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(StrategyManualDO::getMediaType, dto.getMediaType());
            updateWrapper.set(StrategyManualDO::getMediaName, dto.getMediaName());
            updateWrapper.set(StrategyManualDO::getMediaCode, dto.getMediaCode());
            updateWrapper.set(StrategyManualDO::getTop, dto.getStatus() == 1);
            updateWrapper.set(StrategyManualDO::getStartTime, dto.getStartTime() != null ? DateUtil.parse(dto.getStartTime() + " 00:00:00") : null);
            updateWrapper.set(StrategyManualDO::getExpireTime, dto.getEndTime() != null ? DateUtil.parse(dto.getEndTime() + " 23:59:59") : null);
            updateWrapper.eq(StrategyManualDO::getStrategyId, dto.getStrategyId());
            updateWrapper.eq(StrategyManualDO::getMediaId, dto.getMediaId());
            strategyManualMapper.update(null, updateWrapper);
            return CommonResponse.SUCCESS;
        }
        //??????url??????,???????????????
        String mediaInfoString = redisService.get(String.format(RedisConstant.MEDIA_KEY, dto.getMediaId()));
        if (StringUtils.isEmpty(mediaInfoString)) {
            return CommonResponse.build(ManageErrorEnum.NO_POSTER_URL);
        }
        MediaDTO media = JSONObject.parseObject(mediaInfoString, MediaDTO.class);
        if (StringUtils.isEmpty(media.getPosterUrl())) {
            return CommonResponse.build(ManageErrorEnum.NO_POSTER_URL);
        }
        //???????????????????????????
        if (1 != media.getPublishStatus()) {
            return CommonResponse.build(ManageErrorEnum.PUBLISH_ERROR);
        }
        //??????????????????????????????
        // ????????????/??????
        StrategyManualDO entity = new StrategyManualDO();
        entity.setStrategyId(dto.getStrategyId());
        entity.setMediaId(dto.getMediaId());
        entity.setMediaCode(dto.getMediaCode());
        entity.setMediaName(dto.getMediaName());
        entity.setMediaType(dto.getMediaType());
        entity.setTop(dto.getStatus() == 1);
        entity.setPosterUrl(media.getPosterUrl());
        if (StringUtils.isNotBlank(dto.getStartTime())) {
            entity.setStartTime(DateUtil.parse(dto.getStartTime() + " 00:00:00"));
        }
        if (StringUtils.isNotBlank(dto.getEndTime())) {
            entity.setExpireTime(DateUtil.parse(dto.getEndTime() + " 23:59:59"));
        }
        //??????????????????/????????????
        updateManualStatus(dto);
        strategyManualMapper.insert(entity);
        return CommonResponse.SUCCESS;
    }

    public void updateManualStatus(StrategyManualAddDTO dto) {
        if (1 == dto.getStatus()) {
            LambdaUpdateWrapper<StrategyManualDO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(StrategyManualDO::getTop, false);
            updateWrapper.eq(StrategyManualDO::getStrategyId, dto.getStrategyId());
            updateWrapper.eq(StrategyManualDO::getTop, true);
            strategyManualMapper.update(null, updateWrapper);
        }
    }

    @Override
    public CommonResponse removeManual(Long id) {
        strategyManualMapper.deleteById(id);
        return CommonResponse.SUCCESS;
    }
}
