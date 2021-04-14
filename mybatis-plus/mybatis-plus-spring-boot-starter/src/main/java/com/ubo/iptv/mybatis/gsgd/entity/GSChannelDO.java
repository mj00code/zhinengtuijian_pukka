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
    * 频道表
    * </p>
 *
 * @author ottdb_gsgd
 * @since 2020-12-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ottdb_gsgd.t_channel")
public class GSChannelDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @TableField("Name")
    private String Name;

    @TableField("NameEN")
    private String NameEN;

    @TableField("Code")
    private String Code;

    @TableField("CPContentID")
    private String CPContentID;

    @TableField("ChannelNumber")
    private String ChannelNumber;

    @TableField("CallSign")
    private String CallSign;

    @TableField("TimeShift")
    private Integer TimeShift;

    @TableField("StorageDuration")
    private Integer StorageDuration;

    @TableField("TimeShiftDuration")
    private Integer TimeShiftDuration;

    @TableField("Description")
    private String Description;

    @TableField("DescriptionEN")
    private String DescriptionEN;

    @TableField("Country")
    private String Country;

    @TableField("State")
    private String State;

    @TableField("City")
    private String City;

    @TableField("ZipCode")
    private String ZipCode;

    @TableField("Type")
    private Integer Type;

    @TableField("SubType")
    private Integer SubType;

    @TableField("Language")
    private String Language;

    @TableField("Status")
    private Integer Status;

    @TableField("StartTime")
    private LocalDateTime StartTime;

    @TableField("EndTime")
    private LocalDateTime EndTime;

    @TableField("Macrovision")
    private Integer Macrovision;

    @TableField("VideoType")
    private String VideoType;

    @TableField("AudioType")
    private String AudioType;

    @TableField("StreamType")
    private Integer StreamType;

    @TableField("Bilingual")
    private Integer Bilingual;

    @TableField("MulticastIP")
    private String MulticastIP;

    @TableField("MulticastPORT")
    private Integer MulticastPORT;

    @TableField("TimeShiftURL")
    private String TimeShiftURL;

    @TableField("ChannelURL")
    private String ChannelURL;

    @TableField("Logo")
    private String Logo;

    @TableField("CPID")
    private Integer cpid;

    @TableField("CPName")
    private String CPName;

    @TableField("SPID")
    private String spid;

    @TableField("SPName")
    private String SPName;

    @TableField("LimitAreaID")
    private String LimitAreaID;

    @TableField("LimitAreaName")
    private String LimitAreaName;

    @TableField("ContentKindNameEN")
    private String ContentKindNameEN;

    @TableField("LimitAreaNameEN")
    private String LimitAreaNameEN;

    @TableField("ContentKindID")
    private Integer ContentKindID;

    @TableField("ContentKindName")
    private String ContentKindName;

    @TableField("PC")
    private Integer pc;

    @TableField("PC_AreaID")
    private String pcAreaid;

    @TableField("PC_AreaName")
    private String pcAreaname;

    @TableField("PC_AreaNameEN")
    private String pcAreanameen;

    @TableField("PC_StartTime")
    private LocalDate pcStarttime;

    @TableField("PC_EndTime")
    private LocalDate pcEndtime;

    @TableField("STB")
    private Integer stb;

    @TableField("STB_AreaID")
    private String stbAreaid;

    @TableField("STB_AreaName")
    private String stbAreaname;

    @TableField("STB_AreaNameEN")
    private String stbAreanameen;

    @TableField("STB_StartTime")
    private LocalDate stbStarttime;

    @TableField("STB_EndTime")
    private LocalDate stbEndtime;

    @TableField("Mobile")
    private Integer Mobile;

    @TableField("Mobile_AreaID")
    private String mobileAreaid;

    @TableField("Mobile_AreaName")
    private String mobileAreaname;

    @TableField("Mobile_AreaNameEN")
    private String mobileAreanameen;

    @TableField("Mobile_StartTime")
    private LocalDate mobileStarttime;

    @TableField("Mobile_EndTime")
    private LocalDate mobileEndtime;

    @TableField("LiveCMSName")
    private String LiveCMSName;

    @TableField("IsLiveCMS")
    private Integer IsLiveCMS;

    @TableField("Slideimg")
    private String Slideimg;

    @TableField("Slideimg2")
    private String Slideimg2;

    @TableField("DRM")
    private Integer drm;

    @TableField("DRMVendors")
    private Integer DRMVendors;

    @TableField("DRMUrl")
    private String DRMUrl;

    @TableField("DRMKey")
    private String DRMKey;

    @TableField("PolicyGroupID")
    private String PolicyGroupID;

    @TableField("PackageID")
    private String PackageID;

    @TableField("isThirdPart")
    private Integer isThirdPart;

    @TableField("ThirdPartUrl")
    private String ThirdPartUrl;

    @TableField("UserID")
    private String UserID;

    @TableField("UserKey")
    private String UserKey;

    @TableField("ContentID")
    private String ContentID;

    @TableField("TokenExpireTime")
    private LocalDateTime TokenExpireTime;

    @TableField("LicenseToken")
    private String LicenseToken;

    @TableField("LiveConfig_ID")
    private Integer liveconfigId;

    @TableField("PG_M_ID")
    private Integer pgMId;

    @TableField("channelpic")
    private String channelpic;

    @TableField("channelbawpic")
    private String channelbawpic;

    @TableField("channelnamepic")
    private String channelnamepic;

    @TableField("VspCode")
    private String VspCode;

    @TableField("StartTimeStr")
    private String StartTimeStr;

    @TableField("EndTimeStr")
    private String EndTimeStr;

    @TableField("OldCode")
    private String OldCode;

    @TableField("CreateTime")
    private LocalDateTime CreateTime;

    @TableField("UpdateTime")
    private LocalDateTime UpdateTime;

    /**
     * 置换ID优先级,数值越大优先级越高
     */
    @TableField("iptvPriority")
    private Integer iptvPriority;

    /**
     * 置换后的移动侧ID
     */
    @TableField("iptvId")
    private String iptvId;

    /**
     * 置换ID状态0:待置换1:置换中2:置换完成3:置换失败
     */
    @TableField("iptvStatus")
    private String iptvStatus;


}
