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
     * 1 发布成功
2 未发布
3 发布中
4 发布失败
5 取消发布中
6 修改发布中
7 修改发布失败
8 取消发布失败
16 删除发布失败
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
     * 方便sp查看的41位cp内容编码, 8位cp内容编码 + "-" + 32位随机
     */
    @TableField("PredefineContentCode")
    private String PredefineContentCode;

    /**
     * SP审核状态
     */
    @TableField("SPCheckStatus")
    private Integer SPCheckStatus;

    /**
     * SP审核备注
     */
    @TableField("SPCheckDesc")
    private String SPCheckDesc;

    /**
     * SP审核时间
     */
    @TableField("SPCheckTime")
    private LocalDateTime SPCheckTime;

    /**
     * SP审核人
     */
    @TableField("SPChecker")
    private String SPChecker;

    @TableField("SearchName")
    private String SearchName;

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

    /**
     * 编剧
     */
    @TableField("Scriptwriter")
    private String Scriptwriter;

    /**
     * 修改备注
     */
    @TableField("modifydesc")
    private String modifydesc;

    /**
     * 更新至多少集
     */
    @TableField("UpdatedEpisodes")
    private Integer UpdatedEpisodes;

    /**
     * 是否免费  0.false  1.true
     */
    @TableField("IsFree")
    private Integer IsFree;

    /**
     * 是否包月  0.false  1.true
     */
    @TableField("IsMonthly")
    private Integer IsMonthly;

    /**
     * 是否PPV  0.false  1.true
     */
    @TableField("IsSinglePpv")
    private Integer IsSinglePpv;

    /**
     * 包月价格
     */
    @TableField("MonthlyPrice")
    private String MonthlyPrice;

    /**
     * PPV价格
     */
    @TableField("PPVSinglePrice")
    private String PPVSinglePrice;

    /**
     * 图标
     */
    @TableField("Icon")
    private String Icon;

    /**
     * 清晰度  0.空 1.标清 2.高清 3.4K
     */
    @TableField("Definition")
    private Integer Definition;

    /**
     * 自审时间
     */
    @TableField("CheckTime")
    private LocalDateTime CheckTime;

    /**
     * 自审人员
     */
    @TableField("Checker")
    private String Checker;

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
     * 原上下线状态
     */
    @TableField("oldonline")
    private Integer oldonline;

    /**
     * 产品包ids
     */
    @TableField("Serviceids")
    private String Serviceids;

    /**
     * 产品包names
     */
    @TableField("Servicenames")
    private String Servicenames;

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

    /**
     * 频道logo
     */
    @TableField("channelLogo")
    private String channelLogo;

    /**
     * 百事通反馈状态0:待反馈1:反馈失败2:反馈成功
     */
    @TableField("bstfeedbackstatus")
    private Integer bstfeedbackstatus;

    /**
     * 百事通反馈状态0:待反馈1:反馈失败2:反馈成功
     */
    @TableField("bstfeedbackpriority")
    private Integer bstfeedbackpriority;

    @TableField("cornername")
    private String cornername;

    @TableField("cornerpicurl")
    private String cornerpicurl;

    /**
     * 置换ID状态0:待置换1:置换中2:置换完成3:置换失败
     */
    @TableField("iptvStatus")
    private Integer iptvStatus;

    /**
     * 切片状态0:待切片1:切片中2:切片完成3:切片失败
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
     * 标签
     */
    @TableField("namelabel")
    private String namelabel;


}
