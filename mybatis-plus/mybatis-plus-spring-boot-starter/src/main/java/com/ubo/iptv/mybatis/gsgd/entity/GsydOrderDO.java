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
 * @since 2020-12-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ottdb_gsgd.t_gsyd_order")
public class GsydOrderDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 终端类型
     */
    @TableField("TMType")
    private String TMType;

    /**
     * 订单号
     */
    @TableField("OrderNumber")
    private String OrderNumber;

    /**
     * 订单时间
     */
    @TableField("OrderTime")
    private LocalDateTime OrderTime;

    /**
     * 退订时间
     */
    @TableField("UnOrderTime")
    private LocalDateTime UnOrderTime;

    /**
     * 支付方式
     */
    @TableField("PayType")
    private Integer PayType;

    @TableField("Status")
    private String Status;

    /**
     * 用户ID
     */
    @TableField("UserID")
    private String UserID;

    /**
     * 支付状态
     */
    @TableField("PayStatus")
    private Integer PayStatus;

    /**
     * 支付结果描述
     */
    @TableField("PayDesc")
    private String PayDesc;

    /**
     * 请求IP
     */
    @TableField("RequestIP")
    private String RequestIP;

    /**
     * 是否包含合约
     */
    @TableField("ISPaymentHod")
    private Integer ISPaymentHod;

    /**
     * 产品ID
     */
    @TableField("ProductID")
    private Integer ProductID;

    @TableField("ProductCode")
    private String ProductCode;

    /**
     * 产品名称
     */
    @TableField("ProductName")
    private String ProductName;

    /**
     * 产品包ID
     */
    @TableField("ServiceID")
    private Integer ServiceID;

    @TableField("ServiceCode")
    private String ServiceCode;

    /**
     * 产品包名称
     */
    @TableField("ServiceName")
    private String ServiceName;

    /**
     * 内容ID
     */
    @TableField("ContentID")
    private Integer ContentID;

    @TableField("ContentCode")
    private String ContentCode;

    @TableField("ContentName")
    private String ContentName;

    /**
     * 总价（美元）
     */
    @TableField("Price")
    private String Price;

    /**
     * 折扣价（美元）
     */
    @TableField("Discount")
    private String Discount;

    /**
     * 成交价(美元)
     */
    @TableField("Fee")
    private String Fee;

    /**
     * 生效时间
     */
    @TableField("ValidStart")
    private LocalDateTime ValidStart;

    /**
     * 失效时间
     */
    @TableField("ValidEnd")
    private LocalDateTime ValidEnd;

    /**
     * 创建时间
     */
    @TableField("CreateTime")
    private LocalDateTime CreateTime;

    /**
     * 更新时间
     */
    @TableField("UpdateTime")
    private LocalDateTime UpdateTime;

    /**
     * 资费ID
     */
    @TableField("ChargingID")
    private Integer ChargingID;

    @TableField("SPID")
    private String spid;

    /**
     * 产品包类型
     */
    @TableField("ServiceType")
    private Integer ServiceType;

    /**
     * 是否生成了Log
     */
    @TableField("IsLog")
    private Integer IsLog;

    /**
     * 用户名
     */
    @TableField("UserName")
    private String UserName;

    /**
     * SP名称
     */
    @TableField("SPName")
    private String SPName;

    @TableField("SPResellerID")
    private String SPResellerID;

    /**
     * 经销商名称
     */
    @TableField("SPResellerName")
    private String SPResellerName;

    /**
     * 是否计划包
     */
    @TableField("IsPlan")
    private Integer IsPlan;

    /**
     * 资费时长
     */
    @TableField("ChargingDuration")
    private Integer ChargingDuration;

    /**
     * 是否强制激活
     */
    @TableField("IsForceActivation")
    private Integer IsForceActivation;

    /**
     * 账单日期
     */
    @TableField("Month")
    private String Month;

    /**
     * 产品包取消时间
     */
    @TableField("ServiceCancelTime")
    private LocalDateTime ServiceCancelTime;

    /**
     * 免费月数
     */
    @TableField("FreeMonth")
    private Integer FreeMonth;

    /**
     * 备注
     */
    @TableField("Remark")
    private String Remark;

    /**
     * CRM交易时间
     */
    @TableField("ReportTime")
    private LocalDateTime ReportTime;

    /**
     * 内容提供商
##IsSearch
##SelectList{0:家开,1:咪咕}
     */
    @TableField("ContentProvider")
    private Integer ContentProvider;

    /**
     * 订单ID
     */
    @TableField("OrderId")
    private String OrderId;

    /**
     * 服务编号
     */
    @TableField("BillingCode")
    private String BillingCode;

    /**
     * 资费类型
##IsSearch
##SelectList{1:包年,2:包半年,3:包季,4:包月,5:按次(暂只支持包月)}
     */
    @TableField("ChargingType")
    private Integer ChargingType;

    @TableField("PayCode")
    private String PayCode;

    /**
     * 地市号名称
     */
    @TableField("cityNum")
    private String cityNum;

    /**
     * 地市号名称
     */
    @TableField("cityNumName")
    private String cityNumName;

    @TableField("platform")
    private Integer platform;


}
