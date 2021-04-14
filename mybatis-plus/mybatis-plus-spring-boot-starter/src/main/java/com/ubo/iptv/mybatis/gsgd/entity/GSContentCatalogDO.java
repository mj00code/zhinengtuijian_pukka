package com.ubo.iptv.mybatis.gsgd.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
    * 
    * </p>
 *
 * @author ottdb_gsgd
 * @since 2021-01-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ottdb_gsgd.t_content_catalog")
public class GSContentCatalogDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @TableField("CatalogID")
    private Integer CatalogID;

    @TableField("ContentinfoID")
    private Integer ContentinfoID;

    @TableField("Sequence")
    private Integer Sequence;

    @TableField("CatalogIsPublish")
    private Integer CatalogIsPublish;

    @TableField("isPublishDesc")
    private String isPublishDesc;

    @TableField("RelationStatus")
    private Integer RelationStatus;

    @TableField("isDelete")
    private Integer isDelete;

    /**
     * 下发时间
     */
    @TableField("PublishTime")
    private LocalDateTime PublishTime;

    /**
     * 更新标记0:不需要同步.  1:需 要同步
     */
    @TableField("IsSynchronization")
    private Integer IsSynchronization;

    /**
     * 重置操作人
     */
    @TableField("resetOperator")
    private String resetOperator;

    /**
     * 重置操作时间
     */
    @TableField("resetOperationTime")
    private LocalDateTime resetOperationTime;

    /**
     * 自动智能代理发布0##不代理1##代理2##已写入队列
     */
    @TableField("isSmartIssue")
    private Integer isSmartIssue;

    /**
     * 冻结 0：未冻结  1：已冻结
     */
    @TableField("Frozen")
    private Integer Frozen;

    /**
     * 发布操作方式 1：单条下发  2：批量编排下发
     */
    @TableField("PublicationMode")
    private Integer PublicationMode;

    @TableField("adminid")
    private Integer adminid;

    @TableField("adminname")
    private String adminname;

    @TableField("adminip")
    private String adminip;

    @TableField("publishautostatus")
    private Integer publishautostatus;

    @TableField("publishautodesc")
    private String publishautodesc;

    @TableField("publishautodetail")
    private String publishautodetail;


}
