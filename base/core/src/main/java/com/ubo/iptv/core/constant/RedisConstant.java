package com.ubo.iptv.core.constant;

public interface RedisConstant {

    /**
     * 媒资key
     * String.format(RedisConstant.MEDIA_KEY, mediaId)
     */
    public static String MEDIA_KEY = "media_%s";

    /**
     * 媒资code转id
     */
    public static String HASH_MEDIA_CODE2ID_KEY = "hash_media_code2id";

    /**
     * 用户key
     * String.format(RedisConstant.USER_KEY, userId)
     */
    public static String USER_KEY = "user_%d";

    /**
     * 用户code转id
     * String.format(RedisConstant.HASH_USER_CODE2ID_KEY, sysId)
     */
    public static String HASH_USER_CODE2ID_KEY = "hash_user_code2id";

    /**
     * 场景参数key
     */
    public static String HASH_SCENE_ARGS_KEY = "hash_scene_args";

    /**
     * 场景key
     */
    public static String HASH_SCENE_KEY = "hash_scene";
    // --------- dawei -----------


    /**
     * 媒资code与id的映射关系
     */
    public static String MEDIA_CODE_TO_ID_MAPPING = "media_code_has_";

    /**
     * 活跃用户
     * 昨日有log记录的用户
     * String.format(RedisConstant.DAY_ACTIVE_USER_IDS, yesterday)
     */
    public static String DAY_ACTIVE_USER_IDS = "active_user_%s";

    /**
     * 活跃媒资
     * 昨日有log记录的媒资
     * String.format(RedisConstant.DAY_ACTIVE_USER_IDS, yesterday)
     */
    public static String DAY_ACTIVE_MEDIA_IDS = "active_media_%s";

    /**
     * URL_TO_SCENE
     * active_user_YYYYMMDD
     */
    public static String URL_TO_SCENE = "url_to_scene_";

    // ---------xiaofei-----------

    /**
     * 冷启动 查询key
     * codeBoot_#userId
     * 每日JOB设置
     */
    public static String CODE_BOOT = "codeBoot_%d";
    /**
     * 智能推荐全局是否设置 查询key
     * intelligentRecommendationConfig
     * 后台设置
     */
    public static String IR_CONFIG = "intelligentRecommendationConfig";
    /**
     * 智能推荐全局是否设置 查询key
     * all_scene
     * 后台设置
     */
    public static String ALL_SCENE = "all_scene";
    /**
     * 单独场景智能推荐是否设置 查询key
     * scene_#sceneId
     * 后台设置
     */
    public static String SINGLE_SCENE = "scene_%d";

    /**
     * 媒资类型ID
     * mediaTypeId
     * 每小时JOB设置
     */
    public static String MEDIA_TYPE_ID = "mediaTypeId";
    /**
     * 媒资类型名称
     * mediaTypeName
     * 每小时JOB设置
     */
    public static String MEDIA_TYPE_NAME = "mediaTypeName";
    /**
     * 媒资名称
     * name
     * 每小时JOB设置
     */
    public static String MEDIA_NAME = "name";
    /**
     * 点击必推
     * click_must_#userId_#sceneId
     * 每日JOB设置
     */
    public static String CLICK_MUST = "click_must_%d_%d";
    /**
     * 黑名单
     * 后台设置
     * String.format(RedisConstant.MEDIA_BLACKLIST_KEY, sysId)
     */
    public static String MEDIA_BLACKLIST_KEY = "%s_media_blacklist";
    /**
     * 无海报
     * no_poster_#ispId
     * 后台设置
     */
    public static String NO_POSTER = "no_poster_%s";
    /**
     * 点击过滤
     * click_exclude_#userId_#sceneId
     * 每日JOB设置
     */
    public static String CLICK_EXCLUDE = "click_exclude_%d_%d";
    /**
     * 曝光排除
     * click_exclude_#userId_#sceneId
     * 每日JOB设置
     */
    public static String EXPOSURE_EXCLUDE = "exposure_exclude_%d_%d";
    /**
     * 其他页面智能推荐排除
     * provided_#userId_#sceneId
     * 推荐接口设置
     */
    public static String PROVIDED = "provided_%s_%d";
    /**
     * 上次缓存推荐的数据
     * rc_cache_#userId_#sceneId_#mediaId
     * 推荐接口设置
     */
    public static String RC_CACHE = "rc_cache_%s_%d_%s";
    /**
     * ERP系统运营位去重
     * erp_provide_#sceneId
     */
    public static String ERP_PROVIDED = "erp_provided_%d";
    /**
     * 媒资类型偏好TOP5
     * 每日JOB设置
     * String.format(RedisConstant.MEDIA_TYPE_TOP5, userId)
     */
    public static String MEDIA_TYPE_TOP5 = "media_type_top5_%d";

    /**
     * 实时媒资类型偏好TOP
     * String.format(RedisConstant.MEDIA_TYPE_REAL_TIME_TOP, userId)
     */
    public static String MEDIA_TYPE_REAL_TIME_TOP = "media_type_real_time_top_%d";

    /**
     * 热门明显
     * 每日JOB设置
     */
    public static String MEDIA_ACTOR_TOP5 = "media_actor_top";


}
