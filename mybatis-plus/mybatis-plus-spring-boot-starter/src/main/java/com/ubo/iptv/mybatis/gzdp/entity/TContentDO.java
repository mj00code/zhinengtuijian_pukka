package com.ubo.iptv.mybatis.gzdp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 内容
 * </p>
 *
 * @author gzdp
 * @since 2021-03-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("gzdp.t_content")
public class TContentDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 节目ID
     */
    @TableField("content_id")
    private String contentId;

    /**
     * 节目名称
     */
    @TableField("name")
    private String name;

    /**
     * 节目code
     */
    @TableField("code")
    private String code;

    /**
     * 节目订购编号
     */
    @TableField("order_number")
    private String orderNumber;

    /**
     * 原名
     */
    @TableField("original_name")
    private String originalName;

    /**
     * 索引发布时间提供节目排序
     */
    @TableField("sort_name")
    private String sortName;

    /**
     * 搜索名称共节目搜索
     */
    @TableField("search_name")
    private String searchName;

    /**
     * 演员列表
     */
    @TableField("actor_display")
    private String actorDisplay;

    /**
     * 作者列表
     */
    @TableField("writer_display")
    private String writerDisplay;

    /**
     * 国家地区
     */
    @TableField("original_country")
    private String originalCountry;

    /**
     * 语言
     */
    @TableField("language")
    private String language;

    /**
     * 上映年份（YYYY）
     */
    @TableField("relase_year")
    private String relaseYear;

    /**
     * 首播时间（YYYYMMDD）
     */
    @TableField("org_air_date")
    private String orgAirDate;

    /**
     * 有效开始时间(YYYYMMDDHH24MiSS)
     */
    @TableField("licensing_window_start")
    private String licensingWindowStart;

    /**
     * 有效结束时间(YYYYMMDDHH24MiSS)
     */
    @TableField("licensing_window_end")
    private String licensingWindowEnd;

    /**
     * 新到天数
     */
    @TableField("display_as_new")
    private String displayAsNew;

    /**
     * 剩余天数
     */
    @TableField("display_as_last_chance")
    private String displayAsLastChance;

    /**
     * 拷贝保护标识
     */
    @TableField("macrovision")
    private String macrovision;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

    /**
     * l列表定价
     */
    @TableField("price_tax_in")
    private String priceTaxIn;

    /**
     * 状态（0=失效；1=生效）
     */
    @TableField("status")
    private String status;

    /**
     * 节目类型（1=视频类；2=图文类）
     */
    @TableField("source_type")
    private String sourceType;

    /**
     * 剧集类型（0=影片；1=单集）
     */
    @TableField("series_flag")
    private String seriesFlag;

    /**
     * 主要人物
     */
    @TableField("kpeople")
    private String kpeople;

    /**
     * 导演
     */
    @TableField("director")
    private String director;

    @TableField("script_writer")
    private String scriptWriter;

    /**
     * 节目主持人
     */
    @TableField("compere")
    private String compere;

    /**
     * 受访者
     */
    @TableField("guest")
    private String guest;

    /**
     * 记者
     */
    @TableField("reporter")
    private String reporter;

    /**
     * 其他责任人
     */
    @TableField("opincharge")
    private String opincharge;

    /**
     * 内容服务平台标识
     */
    @TableField("vsp_code")
    private String vspCode;

    /**
     * 版权方标识
     */
    @TableField("copy_right")
    private String copyRight;

    /**
     * 内容提供商
     */
    @TableField("content_provider")
    private String contentProvider;

    /**
     * 节目时长（分钟）
     */
    @TableField("duration")
    private String duration;

    /**
     * 剧集类型（0=连续剧；1=系列片）
     */
    @TableField("series_type")
    private String seriesType;

    /**
     * 剧集总集数
     */
    @TableField("volumn_count")
    private String volumnCount;

    /**
     * 创建时间
     */
    @TableField("gmt_create")
    private LocalDateTime gmtCreate;

    /**
     * 更新时间
     */
    @TableField("gmt_modified")
    private LocalDateTime gmtModified;

    /**
     * 缩略图
     */
    @TableField("thumb_img")
    private String thumbImg;

    /**
     * 海报
     */
    @TableField("poster_img")
    private String posterImg;

    /**
     * 剧照
     */
    @TableField("still_img")
    private String stillImg;

    /**
     * 一级分类标签
     */
    @TableField("type")
    private String type;

    /**
     * 二级分类标签
     */
    @TableField("tags")
    private String tags;

    /**
     * 关键字。多个关键字之间使用逗号分隔
     */
    @TableField("key_words")
    private String keyWords;

    /**
     * 看点,非常简短的剧情描述
     */
    @TableField("view_point")
    private String viewPoint;

    /**
     * 推荐星级从1－10，数字越大推荐星级越高，缺省为6 ，为3 颗星
     */
    @TableField("start_level")
    private String startLevel;

    /**
     * 限制类别。采用国际通用的Rating 等级，
     */
    @TableField("rating")
    private String rating;

    /**
     * 所含奖项。多个奖项之间使用；分隔
     */
    @TableField("awards")
    private String awards;

    /**
     * 类型。电影类节目1000：电影--录制类节目1101：新闻/时事1102：财经1103：体育1104：专题1105：法制1106：访谈1107：综艺娱乐1108：音乐1109：戏剧1110：外语――广告类节目1200：广告
     */
    @TableField("program_type")
    private String programType;

    /**
     * 保留字段1
     */
    @TableField("reserve1")
    private String reserve1;

    /**
     * 保留字段2
     */
    @TableField("reserve2")
    private String reserve2;

    /**
     * CPID
     */
    @TableField("cp_id")
    private Integer cpId;

    /**
     * CPName
     */
    @TableField("cp_name")
    private String cpName;

    /**
     * 内容类型（program=影片；series=剧集；episode=单集）
     */
    @TableField("content_type")
    private String contentType;

    /**
     * 剧集ID
     */
    @TableField("content_series_id")
    private Integer contentSeriesId;

    /**
     * 剧集CODE
     */
    @TableField("content_series_code")
    private String contentSeriesCode;

    /**
     * 单集第几集
     */
    @TableField("episode_index")
    private Integer episodeIndex;

    /**
     * 剧集更新至第几集
     */
    @TableField("episodes_update")
    private Integer episodesUpdate;

    /**
     * 上线状态（ONLINE=在线状态；OFFLINE=下线状态）
     */
    @TableField("online_status")
    private String onlineStatus;

    /**
     * 上线时间
     */
    @TableField("onlline_date")
    private LocalDateTime onllineDate;

    /**
     * 下线时间
     */
    @TableField("offline_date")
    private LocalDateTime offlineDate;

    /**
     * 首播时间（YYYYMMDD）日期
     */
    @TableField("org_air_date_dt")
    private LocalDateTime orgAirDateDt;

    /**
     * 是否锁定（lock=锁定；unlock=解锁）
     */
    @TableField("lock_status")
    private String lockStatus;

    /**
     * 播放地址
     */
    @TableField("play_url")
    private String playUrl;

    /**
     * 运营商标识（CTC=电信；CU=联通；CM=移动）
     */
    @TableField("isp")
    private String isp;

    /**
     * SPID
     */
    @TableField("spid")
    private Integer spid;

    /**
     * SP名称
     */
    @TableField("spname")
    private String spname;

    /**
     * 是否有小屏播放版权（YES=有版权；NO=无版权）
     */
    @TableField("is_copyright")
    private String isCopyright;

    /**
     * 审核人
     */
    @TableField("audit_by")
    private String auditBy;

    /**
     * 审核人ID
     */
    @TableField("audit_by_id")
    private Long auditById;

    /**
     * 审核时间
     */
    @TableField("audit_time")
    private LocalDateTime auditTime;

    /**
     * 审核结果（TO_AUDIT=待审核；AUDIT_PASS=审核通过；AUDIT_FAIL=审核未过）
     */
    @TableField("audit_status")
    private String auditStatus;

    /**
     * 审核结果描述或建议
     */
    @TableField("audit_status_desc")
    private String auditStatusDesc;

    /**
     * 审核人
     */
    @TableField("audit2_by")
    private String audit2By;

    /**
     * 审核人ID
     */
    @TableField("audit2_by_id")
    private Long audit2ById;

    /**
     * 审核时间
     */
    @TableField("audit2_time")
    private LocalDateTime audit2Time;

    /**
     * 审核结果（TO_AUDIT=待审核；AUDIT_PASS=审核通过；AUDIT_FAIL=审核未过）
     */
    @TableField("audit2_status")
    private String audit2Status;

    /**
     * 审核结果描述或建议
     */
    @TableField("audit2_status_desc")
    private String audit2StatusDesc;

    /**
     * 审核人
     */
    @TableField("audit3_by")
    private String audit3By;

    /**
     * 审核人ID
     */
    @TableField("audit3_by_id")
    private Long audit3ById;

    /**
     * 审核时间
     */
    @TableField("audit3_time")
    private LocalDateTime audit3Time;

    /**
     * 审核结果（TO_AUDIT=待审核；AUDIT_PASS=审核通过；AUDIT_FAIL=审核未过）
     */
    @TableField("audit3_status")
    private String audit3Status;

    /**
     * 审核结果描述或建议
     */
    @TableField("audit3_status_desc")
    private String audit3StatusDesc;

    /**
     * 是否片花预告片（YES=是；NO=否）
     */
    @TableField("is_trailer")
    private String isTrailer;

    /**
     * 片花预告片对应的正片主键 ID
     */
    @TableField("main_content_id")
    private Long mainContentId;

    /**
     * 评论总数
     */
    @TableField("comment_count")
    private Long commentCount;

    /**
     * 01为IPTV，02为小屏。当即为IPTV又为小屏时，为01,02
     */
    @TableField("program_terminal")
    private String programTerminal;

    /**
     * 播放次数
     */
    @TableField("play_count")
    private Long playCount;

    /**
     * 点赞总数
     */
    @TableField("thumb_up_count")
    private Long thumbUpCount;

    /**
     * 媒资类型
     */
    @TableField(exist = false)
    private String mediaType;
    /**
     * 搜索次数
     */
    @TableField(exist = false)
    private Integer searchCount;

    /**
     * 播放次数
     */
    @TableField(exist = false)
    private Integer recommendCount;

    /**
     * 栏目ID
     */
    @TableField(exist = false)
    private Integer categoryId;
    @TableField(exist = false)
    private String categoryName;
    @TableField(exist = false)
    private Integer mediaTypeId;
}
