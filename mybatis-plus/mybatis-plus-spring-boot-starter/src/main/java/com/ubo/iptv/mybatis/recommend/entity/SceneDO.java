package com.ubo.iptv.mybatis.recommend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
    * 推荐场景
    * </p>
 *
 * @author gz_recommend
 * @since 2020-11-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("gz_recommend.t_scene")
public class SceneDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 场景id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 场景名称
     */
    @TableField("name")
    private String name;

    /**
     * 运营商id
     */
    @TableField("sys_id")
    private String sysId;

    /**
     * 页面路径
     */
    @TableField("page_url")
    private String pageUrl;

    /**
     * 栏目id
     */
    @TableField("category_code")
    private String categoryCode;

    /**
     * 是否支持曝光过滤
     */
    @TableField("support_browse_filter")
    private Boolean supportBrowseFilter;

    /**
     * 是否支持点击过滤
     */
    @TableField("support_click_filter")
    private Boolean supportClickFilter;

    /**
     * 是否支持冷启动
     */
    @TableField("support_cold_boot")
    private Boolean supportColdBoot;

    /**
     * 是否支持人工干预
     */
    @TableField("support_manual")
    private Boolean supportManual;

    /**
     * 是否支持其他场景推荐过滤(排行不需要)
     */
    @TableField("support_scene_filter")
    private Boolean supportSceneFilter;

    /**
     * 是否支持用户偏好(排行不支持)
     */
    @TableField("support_user_favour")
    private Boolean supportUserFavour;


}
