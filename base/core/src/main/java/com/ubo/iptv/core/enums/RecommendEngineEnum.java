package com.ubo.iptv.core.enums;

import com.ubo.iptv.core.annotation.CommonEntity;

/**
 * @Author: xuning
 * @Date: 2020-10-23
 */
public enum RecommendEngineEnum implements CommonEnum {

    @CommonEntity(value = "1", description = "媒资偏好推荐",unit = "UserBehaviorTypeKindService")
    MEDIA_PREFERENCE,

    @CommonEntity(value = "2", description = "明星偏好推荐",unit = "UserBehaviorStarService")
    STAR_PREFERENCE,

    @CommonEntity(value = "3", description = "协同过滤",unit = "UserBehaviorCollaborativeFilterService")
    COLLABORATIVE_FILTER,

    @CommonEntity(value = "4", description = "实时偏好推荐",unit = "UserBehaviorNoDelayTypeKindService")
    REAL_TIME_PREFERENCE;

    /**
     * 根据value获取description
     *
     * @param value
     * @return
     */
    public static String description(Object value) {
        if (value != null) {
            for (RecommendEngineEnum statusEnum : RecommendEngineEnum.values()) {
                if (statusEnum.value().equals(value.toString())) {
                    return statusEnum.description();
                }
            }
            return value.toString();
        }
        return null;
    }
    /**
     * unit
     *
     * @param value
     * @return
     */
    public static String unit(Object value) {
        if (value != null) {
            for (RecommendEngineEnum statusEnum : RecommendEngineEnum.values()) {
                if (statusEnum.value().equals(value.toString())) {
                    return statusEnum.unit();
                }
            }
            return value.toString();
        }
        return null;
    }

    /**
     * 根据value获取enum
     *
     * @param value
     * @return
     */
    public static RecommendEngineEnum valueOf(Object value) {
        if (value != null) {
            for (RecommendEngineEnum statusEnum : RecommendEngineEnum.values()) {
                if (statusEnum.value().equals(value.toString())) {
                    return statusEnum;
                }
            }
        }
        return null;
    }
}
