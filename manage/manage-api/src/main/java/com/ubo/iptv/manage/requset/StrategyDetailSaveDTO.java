package com.ubo.iptv.manage.requset;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author: xuning
 * @Date: 2020-11-09
 */
@Data
public class StrategyDetailSaveDTO {

    @ApiModelProperty(value = "策略id")
    @NotNull
    private Long strategyId;

    @ApiModelProperty(value = "推荐策略名称")
    private String base_strategyName;

    @ApiModelProperty(value = "页面信息-媒资范围_电影收费比例")
    private Integer siteInfo_filmChargeRatio;

    @ApiModelProperty(value = "页面信息-媒资范围_电视剧收费比例")
    private Integer siteInfo_dramaChargeRatio;

    @ApiModelProperty(value = "页面信息-媒资范围_综艺收费比例")
    private Integer siteInfo_zyChargeRatio;

    @ApiModelProperty(value = "页面信息-媒资范围_少儿收费比例")
    private Integer siteInfo_childChargeRatio;

    @ApiModelProperty(value = "页面信息-媒资范围_动漫收费比例")
    private Integer siteInfo_dmChargeRatio;
    @ApiModelProperty(value = "页面信息-媒资范围_直播收费比例")
    private Integer siteInfo_zbChargeRatio;
    @ApiModelProperty(value = "页面信息-媒资范围_纪录片收费比例")
    private Integer siteInfo_jlpChargeRatio;

    @ApiModelProperty(value = "页面信息-媒资范围_电影_占推荐数量,（冷启动需要）")
    private Integer siteInfo_dramaWeight;
    @ApiModelProperty(value = "页面信息-媒资范围_电视剧_占推荐数量,（冷启动需要）")
    private Integer siteInfo_zyWeight;
    @ApiModelProperty(value = "页面信息-媒资范围_综艺_占推荐数量,（冷启动需要）")
    private Integer siteInfo_childWeight;
    @ApiModelProperty(value = "页面信息-媒资范围_少儿_占推荐数量,（冷启动需要）")
    private Integer siteInfo_dmWeight;
    @ApiModelProperty(value = "页面信息-媒资范围_动漫_占推荐数量,（冷启动需要）")
    private Integer siteInfo_filmWeight;
    @ApiModelProperty(value = "页面信息-媒资范围_直播_占推荐数量,（冷启动需要）")
    private Integer siteInfo_zbWeight;
    @ApiModelProperty(value = "页面信息-媒资范围_纪录片_占推荐数量,（冷启动需要）")
    private Integer siteInfo_jlpWeight;

    @ApiModelProperty(value = "历史偏好-媒资偏好推荐数量比例")
    private Integer rc_h_mediaRatio;

    @ApiModelProperty(value = "历史偏好-明星偏好推荐数量比例")
    private Integer rc_h_starRatio;

    @ApiModelProperty(value = "历史偏好-协同过滤偏好推荐数量比例")
    private Integer rc_h_similarRatio;

    @ApiModelProperty(value = "实时偏好-媒资偏好推荐数量比例")
    private Integer rc_n_mediaRatio;

    @ApiModelProperty(value = "媒资排序规则-按照热门程度权重")
    private Integer sort_hotRatio;

    @ApiModelProperty(value = "媒资排序规则-按照热搜程度权重")
    private Integer sort_searchRatio;

    @ApiModelProperty(value = "媒资排序规则-按照评分高低权重")
    private Integer sort_scoreRatio;

    @ApiModelProperty(value = "媒资排序规则-按照上映时间权重")
    private Integer sort_startTimeRatio;

    @ApiModelProperty(value = "媒资排序规则-按照发布时间权重")
    private Integer sort_pubTimeRatio;

    @ApiModelProperty(value = "媒资曝光排除-启用与未启用")
    private Integer exposure_enable;

    @ApiModelProperty(value = "媒资曝光排除-连续曝光天数")
    private Integer exposure_upDay;

    @ApiModelProperty(value = "媒资曝光排除-不推荐天数")
    private Integer exposure_downDay;

    @ApiModelProperty(value = "媒资点击排除-启用与未启用")
    private Integer click_enable;

    @ApiModelProperty(value = "媒资点击排除-电影-必推时长")
    private Integer click_movie_showTime;

    @ApiModelProperty(value = "媒资点击排除-电影-连续不点天数")
    private Integer click_movie_delayDay;

    @ApiModelProperty(value = "媒资点击排除-电影-不再推天数")
    private Integer click_movie_hideDay;

    @ApiModelProperty(value = "媒资点击排除-电视剧-必推时长")
    private Integer click_tv_showTime;

    @ApiModelProperty(value = "媒资点击排除-电视剧-连续不点天数")
    private Integer click_tv_delayDay;

    @ApiModelProperty(value = "媒资点击排除-电视剧-不再推天数")
    private Integer click_tv_hideDay;

    @ApiModelProperty(value = "媒资点击排除-综艺-必推时长")
    private Integer click_variety_showTime;

    @ApiModelProperty(value = "媒资点击排除-综艺-连续不点天数")
    private Integer click_variety_delayDay;

    @ApiModelProperty(value = "媒资点击排除-综艺-不再推天数")
    private Integer click_variety_hideDay;

    @ApiModelProperty(value = "冷启动触发点击次数限制")
    private Integer cold_clickNumber;
}
