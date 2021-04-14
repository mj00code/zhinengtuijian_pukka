package cn.pukkasoft.datasync.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * Order
 * @author TODO:
 *
 */
@Data
@EqualsAndHashCode
@JsonIgnoreProperties(value = { "handler" })
public class Order implements java.io.Serializable{
	
	/**ID*/
    private Integer id;
	/**终端类型*/
    private String tmtype;
	/**订单号*/
    private String orderNumber;
	/**订单时间*/
    private String orderTime;
	/**退订时间*/
    private String unOrderTime;
	/**支付方式*/
    private Integer payType;
	/**状态 ##IsSearch ##SelectList{0:预订购,1:已订购,2:待支付,3:支付失败,4:已退订,5:续订购成功,6:续订购失败}*/
    private String status;
	/**用户ID*/
    private String userId;
	/**支付状态*/
    private Integer payStatus;
	/**支付结果描述*/
    private String payDesc;
	/**请求IP*/
    private String requestIp;
	/**是否包含合约*/
    private String ispaymentHod;
	/**产品ID*/
    private Integer productId;
	/**产品Code*/
    private String productCode;
	/**产品名称*/
    private String productName;
	/**产品包ID*/
    private Integer serviceId;
	/**产品包Code*/
    private String serviceCode;
	/**产品包名称*/
    private String serviceName;
	/**内容ID*/
    private Integer contentId;
	/**内容编码*/
    private String contentCode;
	/**内容名称*/
    private String contentName;
	/**总价（美元）*/
    private String price;
	/**折扣价（美元）*/
    private String discount;
	/**成交价(美元)*/
    private String fee;
	/**生效时间*/
    private String validStart;
	/**失效时间*/
    private String validEnd;
	/**创建时间*/
    private String createTime;
	/**更新时间*/
    private String updateTime;
	/**资费ID*/
    private String chargingId;
	/**SPID*/
    private String spid;
	/**产品包类型*/
    private Integer serviceType;
	/**是否生成了Log*/
    private Integer isLog;
	/**用户名*/
    private String userName;
	/**SP名称*/
    private String spname;
	/**经销商ID*/
    private String spresellerId;
	/**经销商名称*/
    private String spresellerName;
	/**是否计划包*/
    private Integer isPlan;
	/**资费时长*/
    private Integer chargingDuration;
	/**是否强制激活*/
    private Integer isForceActivation;
	/**账单日期*/
    private String month;
	/**产品包取消时间*/
    private String serviceCancelTime;
	/**免费月数*/
    private Integer freeMonth;
	/**备注*/
    private String remark;
	/**CRM交易时间*/
    private String reportTime;
	/**内容提供商 ##IsSearch ##SelectList{0:家开,1:咪咕}*/
    private Integer contentProvider;
	/**订单ID*/
    private String orderId;
	/**服务编号*/
    private String billingCode;
	/**资费类型 ##IsSearch ##SelectList{1:包年,2:包半年,3:包季,4:包月,5:按次(暂只支持包月)}*/
    private Integer chargingType;
	/**支付流水号*/
    private String payCode;
	/**地市号名称*/
    private String cityNum;
	/**地市号名称*/
    private String cityNumName;
	/**1移动2联通*/
    private Integer platform;
}
