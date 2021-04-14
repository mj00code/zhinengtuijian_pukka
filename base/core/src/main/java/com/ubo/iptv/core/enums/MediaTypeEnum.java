package com.ubo.iptv.core.enums;

import com.ubo.iptv.core.annotation.CommonEntity;

/**
 * @Author: xuning
 * @Date: 2020-10-23
 */
public enum MediaTypeEnum implements CommonEnum {

    @CommonEntity(value = "0", description = "其他")
    OTHER,

    @CommonEntity(value = "1", description = "电影")
    MOVIE,

    @CommonEntity(value = "2", description = "连续剧")
    SERIES,

    @CommonEntity(value = "3", description = "游戏")
    GAME,

    @CommonEntity(value = "4", description = "少儿")
    CHILDREN,

    @CommonEntity(value = "5", description = "动漫")
    COMIC,

    @CommonEntity(value = "6", description = "直播")
    LIVE,

    @CommonEntity(value = "7", description = "VIP")
    VIP,

    @CommonEntity(value = "8", description = "驿站")
    STAGE,

    @CommonEntity(value = "9", description = "体育")
    SPORT,

    @CommonEntity(value = "10", description = "推荐")
    RECOMMEND,

    @CommonEntity(value = "11", description = "看山西")
    SHANXI,

    @CommonEntity(value = "12", description = "富美平遥")
    PINGYAO,

    @CommonEntity(value = "13", description = "热播")
    HOT;

    /**
     * 根据value获取description
     *
     * @param value
     * @return
     */
    public static String description(Object value) {
        if (value != null) {
            for (MediaTypeEnum typeEnum : MediaTypeEnum.values()) {
                if (typeEnum.value().equals(value.toString())) {
                    return typeEnum.description();
                }
            }
            return value.toString();
        }
        return null;
    }

    /**
     * 转化mediaType
     *
     * @param typeId
     * @param catalogId
     * @return
     */
    public static MediaTypeEnum getMediaType(Integer catalogId) {

        if (catalogId.equals(2545)) {
            return LIVE;
        }
        // 电影
        if (catalogId.equals(83)) {
            return MOVIE;
        }
        // 连续剧
        if (catalogId.equals(85)) {
            return SERIES;
        }
        // 游戏
        if (catalogId.equals(89)) {
            return GAME;
        }
        // 体育
        if (catalogId.equals(88)) {
            return SPORT;
        }
        // 少儿
        if (catalogId.equals(41)) {
            return CHILDREN;
        }
        // 动漫
        if (catalogId.equals(9246)) {
            return COMIC;
        }
        //VIP
        if (catalogId.equals(9698)) {
            return VIP;
        }
        //驿站
        if (catalogId.equals(34077)) {
            return STAGE;
        }
        //推荐
        if (catalogId.equals(8886)) {
            return RECOMMEND;
        }
        //看山西
        if (catalogId.equals(59003)) {
            return SHANXI;
        }
        //富美平遥
        if (catalogId.equals(60644)) {
            return PINGYAO;
        }
        //当季热播
        if (catalogId.equals(9663)) {
            return HOT;
        }
        return OTHER;
    }

    /**
     * 根据typeId分类
     *
     * @param typeId
     * @return
     */
    private static MediaTypeEnum getMediaTypeByType(Integer typeId) {
        if (typeId != null) {
            /**
             * 1 电影
             * 2 子集
             * 3 电视剧
             * 4 直播
             * 5 系列片
             * 6 片花
             */
            switch (typeId) {
                case 1:
                    return MOVIE;
                case 2:
                    return SERIES;
                case 3:
                    return SERIES;
                case 4:
                    return LIVE;
            }
        }
        return OTHER;
    }

    /**
     * 根据categoryId分类
     *
     * @param categoryId
     * @return
     */
    private static MediaTypeEnum getMediaTypeByCategory(Integer categoryId) {
        if (categoryId != null) {
            /**
             * 1029	纪实
             *
             * 1019	综艺
             *
             * 1009	卡通
             * 1031	少儿
             *
             * 1011	动漫
             */
            switch (categoryId) {
                case 1029:
                    return null;
                case 1019:
                    return null;
                case 1009:
                    return CHILDREN;
                case 1031:
                    return CHILDREN;
                case 1011:
                    return COMIC;
            }
        }
        return OTHER;
    }

    /**
     * 根据kindId分类
     *
     * @param kindIds
     * @return
     */
    private static MediaTypeEnum getMediaTypeByKind(String[] kindIds) {
        if (kindIds != null && kindIds.length > 0) {
            for (String kindId : kindIds) {
                /**
                 * 1043 新闻
                 *
                 * 1031 纪实
                 *
                 * 1073	综艺
                 * 1239 真人秀
                 * 1247 明星
                 *
                 * 1049	儿童
                 * 1115 早教
                 *
                 * 1051	动漫
                 */
                switch (kindId) {
                    case "1031":
                        return null;
                    case "1073":
                        return null;
                    case "1239":
                        return null;
                    case "1247":
                        return null;
                    case "1049":
                        return CHILDREN;
                    case "1115":
                        return CHILDREN;
                    case "1051":
                        return COMIC;
                }
            }
        }
        return OTHER;
    }

    public static MediaTypeEnum getByValue(String value) {
        for (MediaTypeEnum typeEnum : MediaTypeEnum.values()) {
            if (typeEnum.value().equals(value)) {
                return typeEnum;
            }
        }
        return null;
    }
}
