package cn.pukkasoft.datasync.vo;

import cn.pukkasoft.datasync.advice.CPEnum;
import cn.pukkasoft.datasync.advice.ChargingTypeEnum;
import cn.pukkasoft.datasync.advice.OrderStatusEnum;
import cn.pukkasoft.datasync.advice.PlatFormEnum;
import cn.pukkasoft.datasync.annotate.DateFormat;
import cn.pukkasoft.datasync.annotate.EnumCheck;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;

/**
 * Order
 *
 * @author TODO:
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("GSOrderVo")
public class GSOrderVo implements java.io.Serializable {
    @NotBlank
    @Length(max = 4)
    @EnumCheck(clazz = PlatFormEnum.class, message = "platformOperator枚举不在范围内或者大小写不匹配")
    @ApiModelProperty(value = "运营商平台标识CMCC移动CUCC联通CTCC电信", dataType = "String", name = "platformOperator")
    private String platformOperator;
    @Max(2147483647)
    @ApiModelProperty(value = "下发id", dataType = "Integer", name = "publishId")
    private Integer publishId;
    @NotBlank
    @Length(max = 64)
    @ApiModelProperty(value = "用户账号", dataType = "String", name = "userId")
    private String userId;
    @Length(max = 64)
    @ApiModelProperty(value = "用户名", dataType = "String", name = "userName")
    private String userName;
    @Max(2147483647)
    @ApiModelProperty(value = "订单来源", dataType = "Integer", name = "csource")
    private Integer csource;
    @Length(max = 64)
    @ApiModelProperty(value = "订单id", dataType = "String", name = "orderId")
    private String orderId;
    @Length(max = 64)
    @NotBlank
    @ApiModelProperty(value = "订单号", dataType = "String", name = "orderNumber")
    private String orderNumber;
    @EnumCheck(clazz = OrderStatusEnum.class, message = "status枚举值不在范围内")
    @ApiModelProperty(value = "状态 ##IsSearch ##SelectList{0:预订购,1:已订购,2:待支付,3:支付失败,4:已退订,5:续订购成功,6:续订购失败}", dataType = "Integer", name = "status")
    private String status;
    @DateFormat(patterns = {"yyyy-MM-dd HH:mm:ss", "yyyyMMddHHmmss"}, message = "orderTime日期解析错误")
    @ApiModelProperty(value = "订单时间yyyy-MM-dd HH:mm:ss或yyyyMMddHH24mmss", dataType = "String", name = "orderTime")
    private String orderTime;
    @DateFormat(patterns = {"yyyy-MM-dd HH:mm:ss", "yyyyMMddHHmmss"}, message = "unOrderTime日期解析错误")
    @ApiModelProperty(value = "退订时间yyyy-MM-dd HH:mm:ss或yyyyMMddHH24mmss", dataType = "String", name = "unOrderTime")
    private String unOrderTime;
    @Length(max = 64)
    @ApiModelProperty(value = "服务编号", dataType = "String", name = "billingCode")
    private String billingCode;
    @EnumCheck(clazz = ChargingTypeEnum.class, message = "chargingType枚举值不在范围内")
    @ApiModelProperty(value = "资费类型 ##IsSearch ##SelectList{1:包年,2:包半年,3:包季,4:包月,5:按次(暂只支持包月)}", dataType = "Integer", name = "chargingType")
    private Integer chargingType;
    /*@DateFormat(patterns ={"yyyy-MM-dd HH:mm:ss", "yyyyMMddHHmmss", "yyyy/MM/dd HH:mm:ss", "yyyy.MM.dd HH:mm:ss", "MMM d, yyyy K:m:s a", "MMM d, yyyy K:m:s a",
            "yyyy-MM-dd HH:mm", "yyyyMMddHHmm", "yyyy/MM/dd HH:mm", "yyyy.MM.dd HH:mm", "yyyy/MM/dd", "yyyyMMdd", "yyyy.MM.dd", "yyyy-MM-dd",
            "yyyy-MM", "yyyyMM", "yyyy/MM", "yyyy.MM"} ,message = "experienceTime日期解析错误")*/
    @Max(2147483647)
    @ApiModelProperty(value = "优惠券使用时间", dataType = "Integer", name = "experienceTime")
    private Integer experienceTime;
    @Max(2147483647)
    @ApiModelProperty(value = "支付方式", dataType = "Integer", name = "payType")
    private Integer payType;
    @Max(2147483647)
    @ApiModelProperty(value = "支付状态", dataType = "Integer", name = "payStatus")
    private Integer payStatus;
    @ApiModelProperty(value = "支付结果描述", dataType = "String", name = "payDesc")
    private String payDesc;
    @Length(max = 20)
    @ApiModelProperty(value = "请求IP", dataType = "String", name = "requestIp")
    private String requestIp;
    @Length(max = 255)
    @ApiModelProperty(value = "支付流水号", dataType = "String", name = "payCode")
    private String payCode;
    @EnumCheck(clazz = CPEnum.class, message = "status枚举值不在范围内")
    @ApiModelProperty(value = "内容提供商 ##IsSearch ##SelectList{0:家开,1:咪咕}", dataType = "Integer", name = "contentProvider")
    private Integer contentProvider;
    @Max(2147483647)
    @ApiModelProperty(value = "产品ID", dataType = "Integer", name = "productId")
    private Integer productId;
    @Length(max = 128)
    @ApiModelProperty(value = "产品Code", dataType = "String", name = "productCode")
    private String productCode;
    @Length(max = 255)
    @ApiModelProperty(value = "产品名称", dataType = "String", name = "productName")
    private String productName;
    @Max(2147483647)
    @ApiModelProperty(value = "产品包ID", dataType = "Integer", name = "serviceId")
    private Integer serviceId;
    @Length(max = 128)
    @NotBlank(message = "serviceCode不能为空")
    @ApiModelProperty(value = "产品包Code", dataType = "String", name = "serviceCode")
    private String serviceCode;
    @Length(max = 255)
    @ApiModelProperty(value = "产品包名称", dataType = "String", name = "serviceName")
    private String serviceName;
    @Max(2147483647)
    @ApiModelProperty(value = "内容ID", dataType = "Integer", name = "contentId")
    private Integer contentId;
    @Length(max = 128)
    @ApiModelProperty(value = "内容编码", dataType = "String", name = "contentCode")
    private String contentCode;
    @Length(max = 128)
    @ApiModelProperty(value = "内容名称", dataType = "String", name = "contentName")
    private String contentName;
    @Length(max = 64)
    @ApiModelProperty(value = "总价（美元）", dataType = "String", name = "price")
    private String price;
    @Length(max = 64)
    @ApiModelProperty(value = "折扣价（美元）", dataType = "String", name = "discount")
    private String discount;
    @Length(max = 64)
    @ApiModelProperty(value = "成交价(美元)", dataType = "String", name = "fee")
    private String fee;
    @DateFormat(patterns = {"yyyy-MM-dd HH:mm:ss", "yyyyMMddHHmmss"}, message = "validStart日期解析错误")
    @ApiModelProperty(value = "生效时间yyyy-MM-dd HH:mm:ss或yyyyMMddHH24mmss", dataType = "String", name = "validStart")
    private String validStart;
    @DateFormat(patterns = {"yyyy-MM-dd HH:mm:ss", "yyyyMMddHHmmss"}, message = "validEnd日期解析错误")
    @ApiModelProperty(value = "失效时间yyyy-MM-dd HH:mm:ss或yyyyMMddHH24mmss", dataType = "String", name = "validEnd")
    private String validEnd;
    @Length(max = 128)
    @ApiModelProperty(value = "推荐结果标识", dataType = "String", name = "traceId")
    private String traceId;
    @DateFormat(patterns = {"yyyy-MM-dd HH:mm:ss", "yyyyMMddHHmmss"}, message = "publishCreateTime日期解析错误")
    @ApiModelProperty(value = "下发创建时间yyyy-MM-dd HH:mm:ss或yyyyMMddHH24mms", dataType = "String", name = "publishCreateTime")
    private String publishCreateTime;
    @DateFormat(patterns = {"yyyy-MM-dd HH:mm:ss", "yyyyMMddHHmmss"}, message = "publishUpString日期解析错误")
    @ApiModelProperty(value = "下发修改时间yyyy-MM-dd HH:mm:ss或yyyyMMddHH24mms", dataType = "String", name = "publishUpString")
    private String publishUpString;
    @ApiModelProperty(value = "资费ID", dataType = "Integer", name = "chargingId")
    private String chargingId;
    @ApiModelProperty(value = "SPID", dataType = "Integer", name = "spId")
    private String spId;
    @Length(max = 64)
    @ApiModelProperty(value = "SP名称", dataType = "String", name = "spName")
    private String spName;
    @ApiModelProperty(value = "经销商ID", dataType = "Integer", name = "spResellerId")
    private String spResellerId;
    @Length(max = 64)
    @ApiModelProperty(value = "经销商名称", dataType = "String", name = "spResellerName")
    private String spResellerName;
    @Max(2147483647)
    @ApiModelProperty(value = "产品包类型", dataType = "Integer", name = "serviceType")
    private Integer serviceType;
    @Max(2147483647)
    @ApiModelProperty(value = "是否生成了Log", dataType = "Integer", name = "isLog")
    private Integer isLog;
    @Length(max = 64)
    @ApiModelProperty(value = "终端类型", dataType = "String", name = "tmType")
    private String tmType;
    @ApiModelProperty(value = "是否包含合约", dataType = "Integer", name = "isPaymentHod")
    private String isPaymentHod;
    @Max(2147483647)
    @ApiModelProperty(value = "是否计划包", dataType = "Integer", name = "isPlan")
    private Integer isPlan;
    @Max(2147483647)
    @ApiModelProperty(value = "资费时长", dataType = "Integer", name = "chargingDuration")
    private Integer chargingDuration;
    @Max(2147483647)
    @ApiModelProperty(value = "是否强制激活", dataType = "Integer", name = "isForceActivation")
    private Integer isForceActivation;
    @Length(max = 32)
    @ApiModelProperty(value = "账单日期", dataType = "String", name = "month")
    private String month;
    @DateFormat(patterns = {"yyyy-MM-dd HH:mm:ss", "yyyyMMddHHmmss"}, message = "serviceCancelTime日期解析错误")
    @ApiModelProperty(value = "产品包取消时间", dataType = "String", name = "serviceCancelTime")
    private String serviceCancelTime;
    @Max(2147483647)
    @ApiModelProperty(value = "免费月数", dataType = "Integer", name = "freeMonth")
    private Integer freeMonth;
    @ApiModelProperty(value = "备注", dataType = "String", name = "remark")
    private String remark;
    @DateFormat(patterns = {"yyyy-MM-dd HH:mm:ss", "yyyyMMddHHmmss"}, message = "reportTime日期解析错误")
    @ApiModelProperty(value = "CRM交易时间", dataType = "String", name = "reportTime")
    private String reportTime;
    @Max(2147483647)
    @ApiModelProperty(value = "是否首月", dataType = "Integer", name = "isFirstMonth")
    private Integer isFirstMonth;
    @Max(2147483647)
    @ApiModelProperty(value = "首月价格", dataType = "Integer", name = "firstMonthPrice")
    private Integer firstMonthPrice;
}
