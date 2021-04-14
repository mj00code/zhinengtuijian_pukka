package com.ubo.iptv.core.enums;

import com.ubo.iptv.core.annotation.CommonEntity;

public enum ElasticIndexEnum implements CommonEnum {

    @CommonEntity(value = "7", description = "七天前日志(不包含当天)")
    LOG_INDEX_7D,
    @CommonEntity(value = "30", description = "三十天前日志(不包含当天)")
    LOG_INDEX_30D,
    @CommonEntity(value = "60", description = "六十天前日志(不包含当天)")
    LOG_INDEX_60D,
    @CommonEntity(value = "2", description = "二天内日志(包含当天)")
    RT_LOG_INDEX_2D,
    @CommonEntity(value = "7", description = "七天内日志(包含当天)")
    RT_LOG_INDEX_7D,
    @CommonEntity(value = "30", description = "三十天内日志(包含当天)")
    RT_LOG_INDEX_30D,
    @CommonEntity(value = "60", description = "六十天内日志(包含当天)")
    RT_LOG_INDEX_60D,
    @CommonEntity(value = "week", description = "一周内日志(包含当天)")
    RT_LOG_INDEX_WEEK,


}
