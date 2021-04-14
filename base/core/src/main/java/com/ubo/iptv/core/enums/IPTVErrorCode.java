package com.ubo.iptv.core.enums;

import com.ubo.iptv.core.annotation.CodeEntity;

/**
 * @Author: xuning
 * @Date: 2020-07-27
 */
public enum IPTVErrorCode implements CodeEnum {


    @CodeEntity(status = 0, result = 0, msg = "success")
    MESSAGE_OK,
    @CodeEntity(status = 10, result = 0, msg = "全局智能推荐是关闭状态!")
    ALL_RECOMMEND_CLOSE,
    @CodeEntity(status = 10, result = 0, msg = "当前场景智能推荐是关闭状态!")
    SINGLE_PAGE_RECOMMEND_CLOSE,
    @CodeEntity(status = 2, result = 1, msg = "当前场景不存在!")
    NO_SCENE_SET,
    @CodeEntity(status = 3, result = 1, msg = "当前场景的冷启动推荐策略没有配置!")
    NO_CODE_BOOT_STRATEGY_SET,
    @CodeEntity(status = 3, result = 1, msg = "当前场景的智能推荐策略没有配置!")
    NO_STRATEGY_SET,
    @CodeEntity(status = 4, result = 1, msg = "当前场景的冷启动推荐引擎没有配置!")
    NO_CODE_BOOT_STRATEGY_RECOMMEND_SET,
    @CodeEntity(status = 4, result = 1, msg = "当前场景的智能推荐引擎没有配置!")
    NO_STRATEGY_RECOMMEND_SET,
    @CodeEntity(status = 5, result = 1, msg = "当前场景的曝光过滤没有配置!")
    NO_SUPPORT_BROWSE_FILTER_SET,
    @CodeEntity(status = 6, result = 1, msg = "当前场景的点击过滤没有配置!")
    NO_CLICK_FILTER_SET,
    @CodeEntity(status = 7, result = 1, msg = "当前场景的冷启动没有配置!")
    NO_CODE_BOOT_SET,
    @CodeEntity(status = 8, result = 1, msg = "当前场景的人工干预没有配置!")
    NO_MANUAL_SET,
    @CodeEntity(status = 9, result = 1, msg = "当前场景的媒资排序没有配置!")
    NO_MEDIA_SORT_SET,
    @CodeEntity(status = 10, result = 1, msg = "当前场景的媒资类型没有配置!")
    NO_MEDIA_TYPE_SET,
    @CodeEntity(status = 11, result = 1, msg = "以下这些场景没有设置自定义栏目编号:%s")
    NOT_ALL_SCENE_CATEGORY_SET,

}