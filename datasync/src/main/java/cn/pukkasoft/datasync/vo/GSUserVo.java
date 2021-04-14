package cn.pukkasoft.datasync.vo;

import cn.pukkasoft.datasync.advice.EnablePlatForm;
import cn.pukkasoft.datasync.advice.PlatFormEnum;
import cn.pukkasoft.datasync.advice.UserStatusEnum;
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
 * User
 *
 * @author majia:
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("GSUserVo")
public class GSUserVo implements java.io.Serializable {
    @NotBlank
    @Length(max = 4)
    @EnumCheck(clazz = PlatFormEnum.class, message = "platformOperator枚举不在范围内或者大小写不匹配")
    @ApiModelProperty(value = "运营商平台标识CMCC移动CUCC联通CTCC电信", dataType = "String", name = "platformOperator")
    private String platformOperator;
    @Length(max = 20)
    @ApiModelProperty(value = "操作类型INJECTION:用户同步;UPDATE_STATUS:用户状态变更", dataType = "String", name = "action")
    private String action;
    @NotBlank
    @Length(max = 64)
    @ApiModelProperty(value = "用户账号", dataType = "String", name = "userId")
    private String userId;
    @Length(max = 64)
    @ApiModelProperty(value = "用户名", dataType = "String", name = "userName")
    private String userName;
    @Length(max = 64)
    @ApiModelProperty(value = "用户密码(iptv密码)", dataType = "String", name = "password")
    private String password;
    @DateFormat(patterns = {"yyyy-MM-dd HH:mm:ss", "yyyyMMddHHmmss"}, message = "dateCreated日期解析错误")
    @ApiModelProperty(value = "用户开户时间", dataType = "String", name = "dateCreated")
    private String dateCreated;
    @Length(max = 64)
    @ApiModelProperty(value = "电话", dataType = "String", name = "telePhone")
    private String telePhone;
    @EnumCheck(clazz = UserStatusEnum.class, message = "userStatus枚举不在范围内或者大小写不匹配")
    @ApiModelProperty(value = "用户状态联通状态：[F0A:正常;F0J:主动停机;F0K:欠费停机;F0X:销户]移动状态[1：预开户;2：销户;3：停机;4：复机;5: 已激活;6：增机;7：撤单;>=8：暂停]", dataType = "String", name = "userStatus")
    private String userStatus;
    @Max(2147483647)
    @ApiModelProperty(value = "下发id", dataType = "Integer", name = "publishId")
    private Integer publishId;
    @Length(max = 64)
    @ApiModelProperty(value = "工单号", dataType = "String", name = "correlateId")
    private String correlateId;
    @Length(max = 64)
    @ApiModelProperty(value = "流水号", dataType = "String", name = "serverId")
    private String serverId;
    //@EnumCheck(clazz = AreaCodeEnum.class, message = "cityNum枚举不在范围内或者大小写不匹配")//数字枚举比较一定要忽略大小写，否则枚举不匹配
    @ApiModelProperty(value = "地市移动编号", dataType = "String", name = "cityNum")
    private String cityNum;
    //@EnumCheck(clazz = AreaCodeEnum.class, message = "areaNum枚举不在范围内或者大小写不匹配")//数字枚举比较一定要忽略大小写，否则枚举不匹配
    @ApiModelProperty(value = "区县移动编号", dataType = "String", name = "areaNum")
    private String areaNum;
    //@EnumCheck(clazz = AreaCodeEnum.class, message = "areaCode枚举不在范围内或者大小写不匹配")//数字枚举比较一定要忽略大小写，否则枚举不匹配
    @ApiModelProperty(value = "地市区域标识联通数据", dataType = "String", name = "areaCode")
    private String areaCode;
    @Length(max = 32)
    @ApiModelProperty(value = "用户分组id:移动数据", dataType = "String", name = "userGroupId")
    private String userGroupId;
    @Length(max = 128)
    @ApiModelProperty(value = "用户分组名称：移动数据", dataType = "String", name = "userGroupName")
    private String userGroupName;
    @Length(max = 128)
    @ApiModelProperty(value = "用户组标识联通数据", dataType = "String", name = "userGroup")
    private String userGroup;
    @EnumCheck(clazz = EnablePlatForm.class, message = "platformBusiness枚举不在范围内或者大小写不匹配")
    @ApiModelProperty(value = "用户所属能力平台HW华为ZTE中兴", dataType = "String", name = "platformBusiness")
    private String platformBusiness;
    @Length(max = 64)
    @ApiModelProperty(value = "机顶盒设备id", dataType = "String", name = "stbId")
    private String stbId;
    @Length(max = 40)
    @ApiModelProperty(value = "机顶盒mac", dataType = "String", name = "mac")
    private String mac;
    @Length(max = 255)
    @ApiModelProperty(value = "住址", dataType = "String", name = "address")
    private String address;
    @Length(max = 60)
    @ApiModelProperty(value = "基础产品标识：联通数据", dataType = "String", name = "productId")
    private String productId;
    @DateFormat(patterns = {"yyyy-MM-dd HH:mm:ss", "yyyyMMddHHmmss"}, message = "startTime日期解析错误")
    @ApiModelProperty(value = "基础产品订购开始时间yyyy-MM-dd HH:mm:ss或yyyyMMddHH24mmss", dataType = "String", name = "startTime")
    private String startTime;
    @DateFormat(patterns = {"yyyy-MM-dd HH:mm:ss", "yyyyMMddHHmmss"}, message = "endTime日期解析错误")
    @ApiModelProperty(value = "基础产品订购结束时间yyyy-MM-dd HH:mm:ss或yyyyMMddHH24mmss", dataType = "String", name = "startTime")
    private String endTime;
    @Length(max = 32)
    @ApiModelProperty(value = "token", dataType = "String", name = "token")
    private String token;
    @Length(max = 64)
    @ApiModelProperty(value = "设备数量", dataType = "String", name = "deviceNum")
    private String deviceNum;
    @Length(max = 256)
    @ApiModelProperty(value = "渠道", dataType = "String", name = "channelNum")
    private String channelNum;
    @Length(max = 64)
    @ApiModelProperty(value = "局向", dataType = "String", name = "juXiangNum")
    private String juXiangNum;
    @Length(max = 64)
    @ApiModelProperty(value = "营业员号", dataType = "String", name = "peopleNum")
    private String peopleNum;
    @Length(max = 128)
    @ApiModelProperty(value = "企业代码", dataType = "String", name = "spNum")
    private String spNum;
    @Length(max = 128)
    @ApiModelProperty(value = "服务代码", dataType = "String", name = "opNum")
    private String opNum;
    @Length(max = 128)
    @ApiModelProperty(value = "sn", dataType = "String", name = "sn")
    private String sn;
    @DateFormat(patterns = {"yyyy-MM-dd HH:mm:ss", "yyyyMMddHHmmss"}, message = "timestamp日期解析错误")
    @ApiModelProperty(value = "操作时间yyyy-MM-dd HH:mm:ss或yyyyMMddHH24mmss", dataType = "String", name = "timestamp")
    private String timestamp;
}
