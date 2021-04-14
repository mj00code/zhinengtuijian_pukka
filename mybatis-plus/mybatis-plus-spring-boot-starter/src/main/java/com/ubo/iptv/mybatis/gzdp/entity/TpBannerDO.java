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
    * 广告推荐位
    * </p>
 *
 * @author gzdp
 * @since 2021-03-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("gzdp.tp_banner")
public class TpBannerDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 名称
     */
    @TableField("name")
    private String name;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

    /**
     * 海报
     */
    @TableField("poster")
    private String poster;

    /**
     * 序号
     */
    @TableField("sequence")
    private Integer sequence;

    /**
     * 跳转类型(h5=html5；wxapp=微信小程序)
     */
    @TableField("type")
    private String type;

    /**
     * 跳转url
     */
    @TableField("url")
    private String url;

    /**
     * 状态（valid=有效，invalid=无效）
     */
    @TableField("status")
    private String status;

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
     * 广告位编号
     */
    @TableField("position_code")
    private String positionCode;

    /**
     * 运营商标识（CTC=电信；CU=联通；CM=移动）
     */
    @TableField("isp")
    private String isp;

    /**
     * SPID
     */
    @TableField("spid")
    private Long spid;

    /**
     * SP名称
     */
    @TableField("spname")
    private String spname;

    /**
     * 审核人
     */
    @TableField("audit_by")
    private String auditBy;

    /**
     * 审核人ID
     */
    @TableField("audit_by_id")
    private Long auditById;

    /**
     * 审核时间
     */
    @TableField("audit_time")
    private LocalDateTime auditTime;

    /**
     * 审核结果（TO_AUDIT=待审核；AUDIT_PASS=审核通过；AUDIT_FAIL=审核未过）
     */
    @TableField("audit_status")
    private String auditStatus;

    /**
     * 审核结果描述或建议
     */
    @TableField("audit_status_desc")
    private String auditStatusDesc;

    /**
     * 审核人
     */
    @TableField("audit2_by")
    private String audit2By;

    /**
     * 审核人ID
     */
    @TableField("audit2_by_id")
    private Long audit2ById;

    /**
     * 审核时间
     */
    @TableField("audit2_time")
    private LocalDateTime audit2Time;

    /**
     * 审核结果（TO_AUDIT=待审核；AUDIT_PASS=审核通过；AUDIT_FAIL=审核未过）
     */
    @TableField("audit2_status")
    private String audit2Status;

    /**
     * 审核结果描述或建议
     */
    @TableField("audit2_status_desc")
    private String audit2StatusDesc;

    /**
     * 审核人
     */
    @TableField("audit3_by")
    private String audit3By;

    /**
     * 审核人ID
     */
    @TableField("audit3_by_id")
    private Long audit3ById;

    /**
     * 审核时间
     */
    @TableField("audit3_time")
    private LocalDateTime audit3Time;

    /**
     * 审核结果（TO_AUDIT=待审核；AUDIT_PASS=审核通过；AUDIT_FAIL=审核未过）
     */
    @TableField("audit3_status")
    private String audit3Status;

    /**
     * 审核结果描述或建议
     */
    @TableField("audit3_status_desc")
    private String audit3StatusDesc;

    /**
     * 广告编号
     */
    @TableField("code")
    private String code;

    /**
     * 广告横版图片
     */
    @TableField("still")
    private String still;

    /**
     * 媒体类型（VIDEO=视频；PICTURE=图片）
     */
    @TableField("media_type")
    private String mediaType;


}
