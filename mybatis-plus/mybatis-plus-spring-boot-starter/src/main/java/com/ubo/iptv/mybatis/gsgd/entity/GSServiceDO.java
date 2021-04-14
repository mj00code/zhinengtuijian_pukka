package com.ubo.iptv.mybatis.gsgd.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
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
@TableName("ottdb_gsgd.t_service")
public class GSServiceDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @TableField("Name")
    private String Name;

    @TableField("Code")
    private String Code;

    @TableField("Type")
    private Integer Type;

    @TableField("Description")
    private String Description;

    @TableField("ValidStart")
    private LocalDateTime ValidStart;

    @TableField("ValidEnd")
    private LocalDateTime ValidEnd;

    @TableField("CreateTime")
    private LocalDateTime CreateTime;

    @TableField("ISPPV")
    private Integer isppv;

    @TableField("Status")
    private Integer Status;

    @TableField("SPID")
    private Integer spid;

    @TableField("SPName")
    private String SPName;

    @TableField("IsPlan")
    private Integer IsPlan;

    @TableField("FrontDisplay")
    private Integer FrontDisplay;

    @TableField("FreeMonth")
    private Integer FreeMonth;

    @TableField("SpreadStart")
    private LocalDate SpreadStart;

    @TableField("SpreadEnd")
    private LocalDate SpreadEnd;

    @TableField("AD_Level")
    private Integer adLevel;

    /**
     * 产品包下发。  0：没有下发过。 1：下发过。
     */
    @TableField("Service_IsSend")
    private Integer serviceIssend;

    @TableField("OuterProductCode")
    private String OuterProductCode;

    @TableField("IsSendDesc")
    private String IsSendDesc;

    @TableField("icon")
    private String icon;

    @TableField("backgroundpic")
    private String backgroundpic;

    @TableField("thumbnail")
    private String thumbnail;

    @TableField("licensingwindowend")
    private String licensingwindowend;

    @TableField("licensingwindowstart")
    private String licensingwindowstart;

    @TableField("ordernumber")
    private String ordernumber;

    @TableField("price")
    private String price;

    @TableField("rentalperiod")
    private String rentalperiod;

    @TableField("searchname")
    private String searchname;

    @TableField("sortname")
    private String sortname;

    @TableField("c2type")
    private Integer c2type;

    /**
     * 更新标记0##不需要同步1##需要同步##ISSEARCH
     */
    @TableField("IsSynchronization")
    private Integer IsSynchronization;

    /**
     * 下发时间
     */
    @TableField("PublishTime")
    private LocalDateTime PublishTime;

    @TableField("OldCode")
    private String OldCode;

    /**
     * 包类型  1.免费  2.包月  3.ppv
     */
    @TableField("PackageType")
    private Integer PackageType;

    /**
     * 共享产品包spid
     */
    @TableField("ShareSPID")
    private Integer ShareSPID;

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
     * 计费code
     */
    @TableField("billingcode")
    private String billingcode;

    /**
     * 同步sp表中cspid（下游分配的标识）
     */
    @TableField("cspid")
    private String cspid;

    @TableField("publishautostatus")
    private Integer publishautostatus;

    @TableField("publishautodesc")
    private String publishautodesc;

    @TableField("publishautodetail")
    private String publishautodetail;


}
