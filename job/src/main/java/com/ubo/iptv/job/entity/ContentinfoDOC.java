package com.ubo.iptv.job.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author
 * @since 2020-10-26
 */
@Data
public class ContentinfoDOC implements Serializable {

//    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;


    private String Name;


    private String NameEN;


    private String Code;


    private Integer ParentID;


    private String[] ActorDisplay;


    private String[] ActorDisplayEN;


    private String[] WriterDisplay;


    private String[] WriterDisplayEN;


    private String Description;


    private String DescriptionEN;


    private Integer ContentCountryID;


    private String OriginalCountry;


    private String OriginalCountryEN;


    private String Language;


    private String LanguageEN;


    private Integer ContentYearID;


    private String ReleaseYear;


    private String ReleaseYearEN;


    private String LicensingWindowStart;


    private String LicensingWindowEnd;


    private Integer EpisodeCount;


    private Integer EpisodeIndex;


    private Integer Duration;


    private String ThumbImg;


    private String PosterImg;


    private String StillImg;


    private String Score;


    private Integer HitCount;


    private Integer RecommendCount;


    private LocalDateTime CreateTime;


    private LocalDateTime UpdateTime;


    private Integer ContentID;


    private String ContentCode;


    private Integer ContentType;


    private Integer CategoryID;


    private String CategoryName;


    private String CategoryNameEN;


    private String CatalogID;


    private String[] CatalogName;


    private Integer spid;


    private String SPName;


    private Integer Status;

    /**
     * 1 发布成功
     * 2 未发布
     * 3 发布中
     * 4 发布失败
     * 5 取消发布中
     * 6 修改发布中
     * 7 修改发布失败
     * 8 取消发布失败
     * 16 删除发布失败
     */

    private Integer ISPublish;


    private Integer PPVCategoryID;


    private Integer isppv;


    private String PPVPrice;


    private Integer PPVDuration;


    private String LimitAreaID;


    private String Thumbimg2;


    private String Posterimg2;


    private Integer cpid;


    private String CPName;


    private String Producer;


    private String ContentKindID;


    private String ContentKindName;


    private String ContentKindNameEN;


    private Integer pc;


    private String pcAreaid;


    private String pcAreaname;


    private String pcAreanameen;


    private LocalDate pcStarttime;


    private LocalDate pcEndtime;


    private Integer stb;


    private String stbAreaid;


    private String stbAreaname;


    private String stbAreanameen;


    private LocalDate stbStarttime;


    private LocalDate stbEndtime;


    private Integer Mobile;


    private String mobileAreaid;


    private String mobileAreaname;


    private String mobileAreanameen;


    private LocalDate mobileStarttime;


    private LocalDate mobileEndtime;


    private String StillImg2;


    private String PriceTaxIn;


    private Long CreateTimelong;


    private Long UpdateTimelong;


    private Long pcStarttimelong;


    private Long pcEndtimelong;


    private Long stbStarttimelong;


    private Long stbEndtimelong;


    private Long mobileStarttimelong;


    private Long mobileEndtimelong;


    private String Slideimg;


    private String Slideimg2;


    private Integer Sequence;


    private Integer OrderingType;


    private String Midimg2;


    private String Midimg;


    private String Midimg3;


    private String extendimg;


    private Integer isDelete;


    private String isPublishDesc;


    private Integer huaweiStatus;


    private Integer zhongxingStatus;


    private String huaweiStatusDesc;


    private String zhongxingStatusDesc;


    private Integer ReviewStatus;


    private String ReviewDesc;


    private String BitRateID;

    /**
     * 方便sp查看的41位cp内容编码, 8位cp内容编码 + "-" + 32位随机
     */

    private String PredefineContentCode;

    /**
     * SP审核状态
     */

    private Integer SPCheckStatus;

    /**
     * SP审核备注
     */

    private String SPCheckDesc;

    /**
     * SP审核时间
     */

    private LocalDateTime SPCheckTime;

    /**
     * SP审核人
     */

    private String SPChecker;


    private String SearchName;

    /**
     * 更新标记0##不需要同步1##需要同步##ISSEARCH
     */

    private Integer IsSynchronization;

    /**
     * 下发时间
     */

    private LocalDateTime PublishTime;

    /**
     * 编剧
     */

    private String Scriptwriter;

    /**
     * 修改备注
     */

    private String modifydesc;

    /**
     * 更新至多少集
     */

    private Integer UpdatedEpisodes;

    /**
     * 是否免费  0.false  1.true
     */

    private Integer IsFree;

    /**
     * 是否包月  0.false  1.true
     */

    private Integer IsMonthly;

    /**
     * 是否PPV  0.false  1.true
     */

    private Integer IsSinglePpv;

    /**
     * 包月价格
     */

    private String MonthlyPrice;

    /**
     * PPV价格
     */

    private String PPVSinglePrice;

    /**
     * 图标
     */

    private String Icon;

    /**
     * 清晰度  0.空 1.标清 2.高清 3.4K
     */

    private Integer Definition;

    /**
     * 自审时间
     */

    private LocalDateTime CheckTime;

    /**
     * 自审人员
     */

    private String Checker;

    /**
     * 重置操作人
     */

    private String resetOperator;

    /**
     * 重置操作时间
     */

    private LocalDateTime resetOperationTime;


    private Integer online;


    private LocalDateTime onlinetime;


    private LocalDateTime onlinefinishtime;


    private LocalDateTime offlinetime;


    private LocalDateTime offlinefinishtime;

    /**
     * 原上下线状态
     */

    private Integer oldonline;

    /**
     * 产品包ids
     */

    private String Serviceids;

    /**
     * 产品包names
     */

    private String Servicenames;

    /**
     * 自动智能代理发布0##不代理1##代理2##已写入队列
     */

    private Integer isSmartIssue;

    /**
     * 发布操作方式 1：单条下发  2：批量编排下发
     */

    private Integer PublicationMode;

    /**
     * 频道logo
     */

    private String channelLogo;

    /**
     * 百事通反馈状态0:待反馈1:反馈失败2:反馈成功
     */

    private Integer bstfeedbackstatus;

    /**
     * 百事通反馈状态0:待反馈1:反馈失败2:反馈成功
     */

    private Integer bstfeedbackpriority;


    private String cornername;


    private String cornerpicurl;

    /**
     * 置换ID状态0:待置换1:置换中2:置换完成3:置换失败
     */

    private Integer iptvStatus;

    /**
     * 切片状态0:待切片1:切片中2:切片完成3:切片失败
     */

    private Integer slicestatus;


    private Integer adminid;


    private String adminname;


    private String adminip;


    private String offlinedesc;


    private String unpublishdesc;


    private Integer publishautostatus;


    private String publishautodesc;


    private String publishautodetail;

    /**
     * 标签
     */

    private String namelabel;

    /**
     * 点播次数
     */
    private Integer playCount;


    public static void main(String[] args) {
        Field[] fields = ContentinfoDOC.class.getDeclaredFields();
        JSONObject obj = new JSONObject();
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            String cname = f.getType().getName();
            JSONObject type = new JSONObject();
            switch (cname) {
                case "java.lang.Integer":
                    type.put("type", "integer");
                    break;
                case "java.lang.Long":
                    type.put("type", "long");
                    break;
                case "java.lang.String":
                    type.put("type", "keyword");
                    break;
                case "java.time.LocalDateTime":
                case "java.time.LocalDate":
                    type.put("type", "date");
                    type.put("format", "yyyy-MM-dd HH:mm:ss || yyyy-MM-dd ||epoch_millis");
                    break;
                default:
                    System.out.println(cname);
            }
            obj.put(f.getName().toLowerCase(), type);
        }
        System.out.println(obj.toJSONString());
    }
}
