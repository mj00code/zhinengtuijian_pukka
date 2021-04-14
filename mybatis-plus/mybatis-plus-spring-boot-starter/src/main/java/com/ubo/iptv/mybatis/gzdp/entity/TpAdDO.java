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
    * 广告位
    * </p>
 *
 * @author gzdp
 * @since 2021-03-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("gzdp.tp_ad")
public class TpAdDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 广告位编号
     */
    @TableField("code")
    private String code;

    /**
     * 广告位描述
     */
    @TableField("description")
    private String description;

    /**
     * 广告位序号
     */
    @TableField("sequence")
    private Integer sequence;

    /**
     * 状态（VALID=有效，INVALID=无效）
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


}
