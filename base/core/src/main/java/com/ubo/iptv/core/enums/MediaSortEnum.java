package com.ubo.iptv.core.enums;

import com.ubo.iptv.core.annotation.CommonEntity;
import com.ubo.iptv.core.enums.CommonEnum;

/**
 * @Author: xuning
 * @Date: 2020-10-23
 */
public enum MediaSortEnum implements CommonEnum {

    @CommonEntity(value = "1", description = "按照热门程度")
    BY_PLAY,

    @CommonEntity(value = "2", description = "按照热搜程度")
    BY_SEARCH,

    @CommonEntity(value = "3", description = "按照评分高低")
    BY_SCORE,

    @CommonEntity(value = "4", description = "按照上映时间")
    BY_RELEASE_TIME,

    @CommonEntity(value = "5", description = "按照发布时间")
    BY_PUBLIC_TIME,

}
