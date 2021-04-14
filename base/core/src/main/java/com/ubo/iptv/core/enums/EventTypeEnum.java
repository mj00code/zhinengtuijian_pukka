package com.ubo.iptv.core.enums;

import com.ubo.iptv.core.annotation.CommonEntity;

public enum EventTypeEnum implements CommonEnum {
    @CommonEntity(value = "1", description = "非播放⻚⼼跳")
    NON_PLAY_PAGE_HEART,
    @CommonEntity(value = "1", description = "直播⼼跳")
    TV_PLAY_HEART,
    @CommonEntity(value = "1", description = "频道播放时移⼼跳")
    TIMESHIFT_PALY_HEART,
    @CommonEntity(value = "1", description = "点播播放⼼跳")
    VOD_PLAY_HEART,
    @CommonEntity(value = "1", description = "回看节⽬播放⼼跳")
    TVOD_PLAY_HEART,
    @CommonEntity(value = "1", description = "外部链接播放⼼跳")
    EXTERNAL_LINK_PALY_HEART,
    @CommonEntity(value = "1", description = "推荐位点击")
    RECOMMEND_LOCATION_CLICK,
    @CommonEntity(value = "1", description = "推荐位曝光")
    RECOMMEND_LOCATION_EXPOSURE,
    @CommonEntity(value = "1", description = "列表⻚分类切换")
    LIST_PAGE_CATA_SWITCH,
    @CommonEntity(value = "1", description = "播放点击")
    DETAIL_2_PALY_PAGE,
    @CommonEntity(value = "1", description = "订购业务")
    ORDER,
    @CommonEntity(value = "1", description = "收藏业务")
    FAVORITE,
    @CommonEntity(value = "1", description = "开关机")
    ON_OFF,
    @CommonEntity(value = "1", description = "搜索点击")
    SEARCH_CLICK;


}
