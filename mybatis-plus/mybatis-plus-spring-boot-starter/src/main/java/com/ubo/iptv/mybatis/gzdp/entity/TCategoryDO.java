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
    * 栏目
    * </p>
 *
 * @author gzdp
 * @since 2021-03-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("gzdp.t_category")
public class TCategoryDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 栏目id
     */
    @TableField("category_id")
    private String categoryId;

    /**
     * 栏目名称
     */
    @TableField("name")
    private String name;

    /**
     * 栏目code
     */
    @TableField("code")
    private String code;

    /**
     * 序号
     */
    @TableField("sequence")
    private Integer sequence;

    /**
     * 父ID
     */
    @TableField("parentid")
    private Long parentid;

    /**
     * 父code
     */
    @TableField("parentcode")
    private String parentcode;

    /**
     * 状态（valid=有效，invalid=无效）
     */
    @TableField("status")
    private String status;

    /**
     * 创建来源(out=外部，in=内部)
     */
    @TableField("create_from")
    private String createFrom;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

    /**
     * 海报地址
     */
    @TableField("poster_img")
    private String posterImg;

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
     * 跳转目标栏目code
     */
    @TableField("target_category_code")
    private String targetCategoryCode;

    /**
     * 跳转目标栏目名称
     */
    @TableField("target_category_name")
    private String targetCategoryName;

    /**
     * 栏目类型
     */
    @TableField("category_kind")
    private String categoryKind;

    /**
     * 呈现数量
     */
    @TableField("render_size")
    private Integer renderSize;

    /**
     * 运营商标识（CTC=电信；CU=联通；CM=移动）
     */
    @TableField("isp")
    private String isp;

    /**
     * CPID
     */
    @TableField("cpid")
    private Long cpid;

    /**
     * CP名称
     */
    @TableField("cpname")
    private String cpname;

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
     * 是否基础栏目（YES=是；NO=否）
     */
    @TableField("is_base_category")
    private String isBaseCategory;

    /**
     * 模板标识
     */
    @TableField("render_template_code")
    private String renderTemplateCode;

    /**
     * 是否展现名称（YES=是；NO=否）
     */
    @TableField("is_display_name")
    private String isDisplayName;

    /**
     * 是否展现更多（YES=是；NO=否）
     */
    @TableField("is_display_more")
    private String isDisplayMore;

    /**
     * 是否使用页面模板（YES=是；NO=否）
     */
    @TableField("is_use_template")
    private String isUseTemplate;

    /**
     * 是否展现标题（YES=是；NO=否）
     */
    @TableField("is_display_title")
    private String isDisplayTitle;

    /**
     * 是否展现副标题（YES=是；NO=否）
     */
    @TableField("is_display_subtitle")
    private String isDisplaySubtitle;


}
