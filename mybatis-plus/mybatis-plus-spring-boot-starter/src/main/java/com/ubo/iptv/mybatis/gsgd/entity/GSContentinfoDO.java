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
 * @since 2020-11-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ottdb_gsgd.t_contentinfo")
public class GSContentinfoDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @TableField("Name")
    private String Name;

    @TableField("NameEN")
    private String NameEN;

    @TableField("Code")
    private String Code;

    @TableField("ParentID")
    private Integer ParentID;

    @TableField("ActorDisplay")
    private String ActorDisplay;

    @TableField("ActorDisplayEN")
    private String ActorDisplayEN;

    @TableField("WriterDisplay")
    private String WriterDisplay;

    @TableField("WriterDisplayEN")
    private String WriterDisplayEN;

    @TableField("Description")
    private String Description;

    @TableField("DescriptionEN")
    private String DescriptionEN;

    @TableField("ContentCountryID")
    private Integer ContentCountryID;

    @TableField("OriginalCountry")
    private String OriginalCountry;

    @TableField("OriginalCountryEN")
    private String OriginalCountryEN;

    @TableField("Language")
    private String Language;

    @TableField("LanguageEN")
    private String LanguageEN;

    @TableField("ContentYearID")
    private Integer ContentYearID;

    @TableField("ReleaseYear")
    private String ReleaseYear;

    @TableField("ReleaseYearEN")
    private String ReleaseYearEN;

    @TableField("LicensingWindowStart")
    private String LicensingWindowStart;

    @TableField("LicensingWindowEnd")
    private String LicensingWindowEnd;

    @TableField("EpisodeCount")
    private Integer EpisodeCount;

    @TableField("EpisodeIndex")
    private Integer EpisodeIndex;

    @TableField("Duration")
    private Integer Duration;

    @TableField("ThumbImg")
    private String ThumbImg;

    @TableField("PosterImg")
    private String PosterImg;

    @TableField("StillImg")
    private String StillImg;

    @TableField("Score")
    private String Score;

    @TableField("HitCount")
    private Integer HitCount;

    @TableField("RecommendCount")
    private Integer RecommendCount;

    @TableField("CreateTime")
    private LocalDateTime CreateTime;

    @TableField("UpdateTime")
    private LocalDateTime UpdateTime;

    @TableField("ContentID")
    private Integer ContentID;

    @TableField("ContentCode")
    private String ContentCode;

    @TableField("ContentType")
    private Integer ContentType;

    @TableField("CategoryID")
    private Integer CategoryID;

    @TableField("CategoryName")
    private String CategoryName;

    @TableField("CategoryNameEN")
    private String CategoryNameEN;

    @TableField("CatalogID")
    private String CatalogID;

    @TableField("CatalogName")
    private String CatalogName;

    @TableField("SPID")
    private Integer spid;

    @TableField("SPName")
    private String SPName;

    @TableField("Status")
    private Integer Status;

    /**
     * 1 ????????????
2 ?????????
3 ?????????
4 ????????????
5 ???????????????
6 ???????????????
7 ??????????????????
8 ??????????????????
16 ??????????????????
     */
    @TableField("ISPublish")
    private Integer ISPublish;

    @TableField("PPVCategoryID")
    private Integer PPVCategoryID;

    @TableField("ISPPV")
    private Integer isppv;

    @TableField("PPVPrice")
    private String PPVPrice;

    @TableField("PPVDuration")
    private Integer PPVDuration;

    @TableField("LimitAreaID")
    private String LimitAreaID;

    @TableField("Thumbimg2")
    private String Thumbimg2;

    @TableField("Posterimg2")
    private String Posterimg2;

    @TableField("CPID")
    private Integer cpid;

    @TableField("CPName")
    private String CPName;

    @TableField("Producer")
    private String Producer;

    @TableField("ContentKindID")
    private String ContentKindID;

    @TableField("ContentKindName")
    private String ContentKindName;

    @TableField("ContentKindNameEN")
    private String ContentKindNameEN;

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

    @TableField("StillImg2")
    private String StillImg2;

    @TableField("PriceTaxIn")
    private String PriceTaxIn;

    @TableField("CreateTimelong")
    private Long CreateTimelong;

    @TableField("UpdateTimelong")
    private Long UpdateTimelong;

    @TableField("PC_StartTimelong")
    private Long pcStarttimelong;

    @TableField("PC_EndTimelong")
    private Long pcEndtimelong;

    @TableField("STB_StartTimelong")
    private Long stbStarttimelong;

    @TableField("STB_EndTimelong")
    private Long stbEndtimelong;

    @TableField("Mobile_StartTimelong")
    private Long mobileStarttimelong;

    @TableField("Mobile_EndTimelong")
    private Long mobileEndtimelong;

    @TableField("Slideimg")
    private String Slideimg;

    @TableField("Slideimg2")
    private String Slideimg2;

    @TableField("Sequence")
    private Integer Sequence;

    @TableField("OrderingType")
    private Integer OrderingType;

    @TableField("Midimg2")
    private String Midimg2;

    @TableField("Midimg")
    private String Midimg;

    @TableField("Midimg3")
    private String Midimg3;

    @TableField("extendimg")
    private String extendimg;

    @TableField("isDelete")
    private Integer isDelete;

    @TableField("isPublishDesc")
    private String isPublishDesc;

    @TableField("huaweiStatus")
    private Integer huaweiStatus;

    @TableField("zhongxingStatus")
    private Integer zhongxingStatus;

    @TableField("huaweiStatusDesc")
    private String huaweiStatusDesc;

    @TableField("zhongxingStatusDesc")
    private String zhongxingStatusDesc;

    @TableField("ReviewStatus")
    private Integer ReviewStatus;

    @TableField("ReviewDesc")
    private String ReviewDesc;

    @TableField("BitRateID")
    private String BitRateID;

    /**
     * ??????sp?????????41???cp????????????, 8???cp???????????? + "-" + 32?????????
     */
    @TableField("PredefineContentCode")
    private String PredefineContentCode;

    /**
     * SP????????????
     */
    @TableField("SPCheckStatus")
    private Integer SPCheckStatus;

    /**
     * SP????????????
     */
    @TableField("SPCheckDesc")
    private String SPCheckDesc;

    /**
     * SP????????????
     */
    @TableField("SPCheckTime")
    private LocalDateTime SPCheckTime;

    /**
     * SP?????????
     */
    @TableField("SPChecker")
    private String SPChecker;

    @TableField("SearchName")
    private String SearchName;

    /**
     * ????????????0##???????????????1##????????????##ISSEARCH
     */
    @TableField("IsSynchronization")
    private Integer IsSynchronization;

    /**
     * ????????????
     */
    @TableField("PublishTime")
    private LocalDateTime PublishTime;

    /**
     * ??????
     */
    @TableField("Scriptwriter")
    private String Scriptwriter;

    /**
     * ????????????
     */
    @TableField("modifydesc")
    private String modifydesc;

    /**
     * ??????????????????
     */
    @TableField("UpdatedEpisodes")
    private Integer UpdatedEpisodes;

    /**
     * ????????????  0.false  1.true
     */
    @TableField("IsFree")
    private Integer IsFree;

    /**
     * ????????????  0.false  1.true
     */
    @TableField("IsMonthly")
    private Integer IsMonthly;

    /**
     * ??????PPV  0.false  1.true
     */
    @TableField("IsSinglePpv")
    private Integer IsSinglePpv;

    /**
     * ????????????
     */
    @TableField("MonthlyPrice")
    private String MonthlyPrice;

    /**
     * PPV??????
     */
    @TableField("PPVSinglePrice")
    private String PPVSinglePrice;

    /**
     * ??????
     */
    @TableField("Icon")
    private String Icon;

    /**
     * ?????????  0.??? 1.?????? 2.?????? 3.4K
     */
    @TableField("Definition")
    private Integer Definition;

    /**
     * ????????????
     */
    @TableField("CheckTime")
    private LocalDateTime CheckTime;

    /**
     * ????????????
     */
    @TableField("Checker")
    private String Checker;

    /**
     * ???????????????
     */
    @TableField("resetOperator")
    private String resetOperator;

    /**
     * ??????????????????
     */
    @TableField("resetOperationTime")
    private LocalDateTime resetOperationTime;

    @TableField("online")
    private Integer online;

    @TableField("onlinetime")
    private LocalDateTime onlinetime;

    @TableField("onlinefinishtime")
    private LocalDateTime onlinefinishtime;

    @TableField("offlinetime")
    private LocalDateTime offlinetime;

    @TableField("offlinefinishtime")
    private LocalDateTime offlinefinishtime;

    /**
     * ??????????????????
     */
    @TableField("oldonline")
    private Integer oldonline;

    /**
     * ?????????ids
     */
    @TableField("Serviceids")
    private String Serviceids;

    /**
     * ?????????names
     */
    @TableField("Servicenames")
    private String Servicenames;

    /**
     * ????????????????????????0##?????????1##??????2##???????????????
     */
    @TableField("isSmartIssue")
    private Integer isSmartIssue;

    /**
     * ?????????????????? 1???????????????  2?????????????????????
     */
    @TableField("PublicationMode")
    private Integer PublicationMode;

    /**
     * ??????logo
     */
    @TableField("channelLogo")
    private String channelLogo;

    /**
     * ?????????????????????0:?????????1:????????????2:????????????
     */
    @TableField("bstfeedbackstatus")
    private Integer bstfeedbackstatus;

    /**
     * ?????????????????????0:?????????1:????????????2:????????????
     */
    @TableField("bstfeedbackpriority")
    private Integer bstfeedbackpriority;

    @TableField("cornername")
    private String cornername;

    @TableField("cornerpicurl")
    private String cornerpicurl;

    /**
     * ??????ID??????0:?????????1:?????????2:????????????3:????????????
     */
    @TableField("iptvStatus")
    private Integer iptvStatus;

    /**
     * ????????????0:?????????1:?????????2:????????????3:????????????
     */
    @TableField("slicestatus")
    private Integer slicestatus;

    @TableField("adminid")
    private Integer adminid;

    @TableField("adminname")
    private String adminname;

    @TableField("adminip")
    private String adminip;

    @TableField("offlinedesc")
    private String offlinedesc;

    @TableField("unpublishdesc")
    private String unpublishdesc;

    @TableField("publishautostatus")
    private Integer publishautostatus;

    @TableField("publishautodesc")
    private String publishautodesc;

    @TableField("publishautodetail")
    private String publishautodetail;

    /**
     * ??????
     */
    @TableField("namelabel")
    private String namelabel;


}
