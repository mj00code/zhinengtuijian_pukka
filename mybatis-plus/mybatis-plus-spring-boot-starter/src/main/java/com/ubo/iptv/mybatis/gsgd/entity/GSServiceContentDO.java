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
 * @since 2021-01-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ottdb_gsgd.t_service_content")
public class GSServiceContentDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @TableField("ServiceID")
    private Integer ServiceID;

    @TableField("ContentID")
    private Integer ContentID;

    @TableField("ContentCode")
    private String ContentCode;

    @TableField("ContentType")
    private Integer ContentType;

    @TableField("ContentName")
    private String ContentName;

    @TableField("ServiceContent_IsSend")
    private Integer servicecontentIssend;

    @TableField("ServiceContentIsSendDesc")
    private String ServiceContentIsSendDesc;

    @TableField("isDelete")
    private Integer isDelete;

    /**
     * 下发时间
     */
    @TableField("PublishTime")
    private LocalDateTime PublishTime;

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


}
