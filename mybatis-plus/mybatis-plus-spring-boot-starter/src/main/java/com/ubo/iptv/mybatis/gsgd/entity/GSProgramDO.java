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
 * @since 2020-12-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ottdb_gsgd.t_program")
public class GSProgramDO implements Serializable {

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

    @TableField("ProgramType")
    private Integer ProgramType;

    @TableField("ParentID")
    private Integer ParentID;

    @TableField("ActorDisplay")
    private String ActorDisplay;

    @TableField("WriterDisplayEN")
    private String WriterDisplayEN;

    @TableField("WriterDisplay")
    private String WriterDisplay;

    @TableField("ActorDisplayEN")
    private String ActorDisplayEN;

    @TableField("Description")
    private String Description;

    @TableField("DescriptionEN")
    private String DescriptionEN;

    @TableField("OrderNumber")
    private String OrderNumber;

    @TableField("SortName")
    private String SortName;

    @TableField("SearchName")
    private String SearchName;

    @TableField("Genre")
    private String Genre;

    @TableField("OriginalCountry")
    private String OriginalCountry;

    @TableField("OriginalCountryEN")
    private String OriginalCountryEN;

    @TableField("Language")
    private String Language;

    @TableField("LanguageEN")
    private String LanguageEN;

    @TableField("ReleaseYear")
    private String ReleaseYear;

    @TableField("OrgAirDate")
    private String OrgAirDate;

    @TableField("LicensingWindowStart")
    private String LicensingWindowStart;

    @TableField("LicensingWindowEnd")
    private String LicensingWindowEnd;

    @TableField("DisplayAsNew")
    private String DisplayAsNew;

    @TableField("DisplayAsLastChance")
    private String DisplayAsLastChance;

    @TableField("Macrovision")
    private Integer Macrovision;

    @TableField("PriceTaxIn")
    private String PriceTaxIn;

    @TableField("Status")
    private Integer Status;

    @TableField("EpisodeCount")
    private Integer EpisodeCount;

    @TableField("EpisodeIndex")
    private Integer EpisodeIndex;

    @TableField("Type")
    private String Type;

    @TableField("TypeEN")
    private String TypeEN;

    @TableField("Keywords")
    private String Keywords;

    @TableField("Tags")
    private String Tags;

    @TableField("ViewPoint")
    private String ViewPoint;

    @TableField("StarLevel")
    private Integer StarLevel;

    @TableField("Thumbimg2")
    private String Thumbimg2;

    @TableField("Posterimg2")
    private String Posterimg2;

    @TableField("Duration")
    private Integer Duration;

    @TableField("ContentProvider")
    private String ContentProvider;

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

    @TableField("CategoryID")
    private Integer CategoryID;

    @TableField("CategoryName")
    private String CategoryName;

    @TableField("CheckDesc")
    private String CheckDesc;

    @TableField("Producer")
    private String Producer;

    @TableField("CreateTime")
    private LocalDateTime CreateTime;

    @TableField("UpdateTime")
    private LocalDateTime UpdateTime;

    @TableField("LimitAreaID")
    private String LimitAreaID;

    @TableField("LimitAreaName")
    private String LimitAreaName;

    @TableField("CPID")
    private Integer cpid;

    @TableField("CPName")
    private String CPName;

    @TableField("SPID")
    private String spid;

    @TableField("SPName")
    private String SPName;

    @TableField("ContentCountryID")
    private Integer ContentCountryID;

    @TableField("ContentYearID")
    private Integer ContentYearID;

    @TableField("SrcImg")
    private String SrcImg;

    @TableField("ImgStatus")
    private Integer ImgStatus;

    @TableField("ImgCheckDesc")
    private String ImgCheckDesc;

    @TableField("ContentKindID")
    private String ContentKindID;

    @TableField("SrcID")
    private Integer SrcID;

    @TableField("SrcStatus")
    private Integer SrcStatus;

    @TableField("ContentKindName")
    private String ContentKindName;

    @TableField("PPVCategoryID")
    private Integer PPVCategoryID;

    @TableField("CategoryNameEN")
    private String CategoryNameEN;

    @TableField("ContentKindNameEN")
    private String ContentKindNameEN;

    @TableField("LimitAreaNameEN")
    private String LimitAreaNameEN;

    @TableField("ReleaseYearEN")
    private String ReleaseYearEN;

    @TableField("PC")
    private Integer pc;

    @TableField("PC_AreaID")
    private String pcAreaid;

    @TableField("PC_AreaName")
    private String pcAreaname;

    @TableField("PC_AreaNameEN")
    private String pcAreanameen;

    @TableField("PC_StartTime")
    private LocalDateTime pcStarttime;

    @TableField("PC_EndTime")
    private LocalDateTime pcEndtime;

    @TableField("STB")
    private Integer stb;

    @TableField("STB_AreaID")
    private String stbAreaid;

    @TableField("STB_AreaName")
    private String stbAreaname;

    @TableField("STB_AreaNameEN")
    private String stbAreanameen;

    @TableField("STB_StartTime")
    private LocalDateTime stbStarttime;

    @TableField("STB_EndTime")
    private LocalDateTime stbEndtime;

    @TableField("Mobile")
    private Integer Mobile;

    @TableField("Mobile_AreaID")
    private String mobileAreaid;

    @TableField("Mobile_AreaName")
    private String mobileAreaname;

    @TableField("Mobile_AreaNameEN")
    private String mobileAreanameen;

    @TableField("Mobile_StartTime")
    private LocalDateTime mobileStarttime;

    @TableField("Mobile_EndTime")
    private LocalDateTime mobileEndtime;

    @TableField("StillImg2")
    private String StillImg2;

    @TableField("Slideimg")
    private String Slideimg;

    @TableField("Slideimg2")
    private String Slideimg2;

    @TableField("Midimg2")
    private String Midimg2;

    @TableField("Midimg")
    private String Midimg;

    @TableField("Midimg3")
    private String Midimg3;

    @TableField("Aliasname1")
    private String Aliasname1;

    @TableField("Aliasname2")
    private String Aliasname2;

    @TableField("Aliasname3")
    private String Aliasname3;

    @TableField("Studio")
    private String Studio;

    @TableField("Subtitle1")
    private String Subtitle1;

    @TableField("Subtitle2")
    private String Subtitle2;

    @TableField("Subtitle3")
    private String Subtitle3;

    @TableField("Subtitlename1")
    private String Subtitlename1;

    @TableField("Subtitlename2")
    private String Subtitlename2;

    @TableField("Subtitlename3")
    private String Subtitlename3;

    @TableField("Poster3x4")
    private String Poster3x4;

    @TableField("Poster4x3")
    private String Poster4x3;

    @TableField("Poster16x9")
    private String Poster16x9;

    @TableField("thumbimg3")
    private String thumbimg3;

    @TableField("posterimg3")
    private String posterimg3;

    @TableField("extendimg")
    private String extendimg;

    @TableField("ReviewStatus")
    private Integer ReviewStatus;

    @TableField("ReviewDesc")
    private String ReviewDesc;

    @TableField("ModifyDesc")
    private String ModifyDesc;

    /**
     * 方便sp查看的41位cp内容编码, 8位cp内容编码 + "-" + 32位随机
     */
    @TableField("ContentCode")
    private String ContentCode;

    /**
     * 审核时间
     */
    @TableField("CheckTime")
    private LocalDateTime CheckTime;

    /**
     * 审核人
     */
    @TableField("Checker")
    private String Checker;

    @TableField("Originalname")
    private String Originalname;

    @TableField("Kpeople")
    private String Kpeople;

    @TableField("Scriptwriter")
    private String Scriptwriter;

    @TableField("Compere")
    private String Compere;

    @TableField("Guest")
    private String Guest;

    @TableField("Reporter")
    private String Reporter;

    @TableField("Opincharge")
    private String Opincharge;

    @TableField("Copyright")
    private String Copyright;

    @TableField("SourceType")
    private Integer SourceType;

    @TableField("SeriesFlag")
    private Integer SeriesFlag;

    @TableField("VspCode")
    private String VspCode;

    @TableField("OldId")
    private String OldId;

    /**
     * op审核0##op未审核1##op审核未通过2##op审核通过
     */
    @TableField("opcheckstatus")
    private Integer opcheckstatus;

    /**
     * op审核描述
     */
    @TableField("opcheckdesc")
    private String opcheckdesc;

    @TableField("c2status")
    private Integer c2status;

    @TableField("OldCode")
    private String OldCode;

    @TableField("C2writerdisplay")
    private String C2writerdisplay;

    @TableField("mgmd5")
    private String mgmd5;

    /**
     * 清晰度  0.空 1.标清 2.高清 3.4K
     */
    @TableField("Definition")
    private Integer Definition;

    /**
     * 提交自审状态0##未提交自审1##已提交自审
     */
    @TableField("commitCheckStatus")
    private Integer commitCheckStatus;

    /**
     * 提交自审时间
     */
    @TableField("commitCheckStatusTime")
    private LocalDateTime commitCheckStatusTime;

    /**
     * 终审人员
     */
    @TableField("opchecker")
    private String opchecker;

    /**
     * 终审时间
     */
    @TableField("opchecktime")
    private LocalDateTime opchecktime;

    /**
     * 内容编辑锁状态0##未锁定1##已锁定
     */
    @TableField("editLockStatus")
    private Integer editLockStatus;

    /**
     * 操作锁时间
     */
    @TableField("editLockTime")
    private LocalDateTime editLockTime;

    /**
     * 操作锁人员
     */
    @TableField("editLockChecker")
    private String editLockChecker;

    /**
     * 审批号
     */
    @TableField("approvalNumber")
    private String approvalNumber;

    /**
     * 置换后的移动侧ID
     */
    @TableField("iptvId")
    private String iptvId;

    /**
     * 置换ID状态0:待置换1:置换中2:置换完成3:置换失败
     */
    @TableField("iptvStatus")
    private Integer iptvStatus;

    /**
     * 置换ID优先级,数值越大优先级越高
     */
    @TableField("iptvPriority")
    private Integer iptvPriority;

    @TableField("cornername")
    private String cornername;

    @TableField("cornerpicurl")
    private String cornerpicurl;

    /**
     * 切片状态0:待切片1:切片中2:切片完成3:切片失败
     */
    @TableField("slicestatus")
    private Integer slicestatus;

    /**
     * 切片地址
     */
    @TableField("sliceurl")
    private String sliceurl;

    @TableField("onlinestatus")
    private Integer onlinestatus;

    @TableField("adminid")
    private Integer adminid;

    @TableField("adminname")
    private String adminname;

    @TableField("adminip")
    private String adminip;

    /**
     * 标签
     */
    @TableField("namelabel")
    private String namelabel;


}
