package com.ubo.iptv.mybatis.gzdp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
    * 内容与栏目关系
    * </p>
 *
 * @author gzdp
 * @since 2021-03-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("gzdp.t_content_category")
public class TContentCategoryDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 栏目ID
     */
    @TableField("category_id")
    private Integer categoryId;

    /**
     * 栏目code
     */
    @TableField("category_code")
    private String categoryCode;

    /**
     * 内容主键ID
     */
    @TableField("content_id")
    private Long contentId;

    /**
     * 内容code
     */
    @TableField("content_code")
    private String contentCode;

    /**
     * 内容类型（PROGRAM=电影，SERIES=剧集，CHANNEL=频道，BANNER=广告）
     */
    @TableField("content_type")
    private String contentType;

    /**
     * 序号
     */
    @TableField("sequence")
    private Integer sequence;

    /**
     * 创建时间
     */
    @TableField("gmt_create")
    private LocalDateTime gmtCreate;

    /**
     * 更新时间
     */
    @TableField("gmt_modified")
    private LocalDateTime gmtModified;

    /**
     * 绑定类型（AUTO=自动绑定；MANUAL=人工绑定）
     */
    @TableField("bind_type")
    private String bindType;

    /**
     * 栏目名称
     */
    @TableField("category_name")
    private String categoryName;

    /**
     * 内容名称
     */
    @TableField("content_name")
    private String contentName;


}
