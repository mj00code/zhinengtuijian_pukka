package com.ubo.iptv.entity.gdgd;

import com.ubo.iptv.mybatis.gzdp.entity.TChannelDO;
import com.ubo.iptv.mybatis.gzdp.entity.TContentDO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author: ShawnLiao
 * @Date: 2020-11-02
 */
@Data
@NoArgsConstructor
public class MediaDTO implements Serializable {

    /**
     * 媒资id
     */
    private Integer mediaId;

    /**
     * 父媒资id，对应数据库content_series_id字段
     */
    private Integer parentMediaId;

    /**
     * 媒资code
     */
    private String mediaCode;

    // ----------媒资属性----------

    /**
     * 媒资名称
     */
    private String name;

    /**
     * 演员   --actor_display
     */
    private String[] actor;

    /**
     * 导演
     */
    private String[] director;

    /**
     * 编剧  script_writer
     */
    private String[] screenwriter;

    /**
     * 制片国家/地区  original_country
     */
    private String[] country;

    /**
     * 语言
     */
    private String language;

    /**
     * 上映年份
     */
    private Integer releaseYear;

    /**
     * 时长/分钟
     */
    private Integer duration;

    /**
     * 媒资类型id
     */
    private String mediaTypeId;

    /**
     * 媒资分类id
     */
    private Integer mediaCategoryId;

    /**
     * 媒资分类
     */
    private String mediaCategoryName;

    /**
     * 评分
     */
    private BigDecimal score;

    // ----------运营属性----------

    /**
     * 运营商
     */
    private String sysId;

    /**
     * cpID
     */
    private Integer cpId;

    /**
     * cp名称
     */
    private String cpName;

    /**
     * spID
     */
    private Integer spId;

    /**
     * sp名称
     */
    private String spName;

    /**
     * 首字母搜索
     */
    private String searchName;

    /**
     * 上下线状态 online_status
     */
    private Integer onlineStatus;

    /**
     * 上线时间
     */
    private LocalDateTime onLineDate;

    /**
     * 下线时间
     */
    private LocalDateTime offLineDate;

    /**
     * 是否为VIP
     */
    private Boolean free;

    // ----------其他属性----------

    /**
     * 媒资类型枚举
     */
    private Integer mediaType;

    /**
     * 媒资类型（电影/连续剧）
     */
    private String mediaTypeName;

    /**
     * 海报地址
     */
    private String posterUrl;

    /**
     * 角标地址
     */
    private String cornerUrl;

    /**
     * 7日搜索次数
     */
    private Integer searchCount;

    /**
     * 7日播放次数
     */
    private Integer playCount;

    /**
     * 创建时间
     */
    private String createTime;

    private Integer publishStatus;

    private String description;

    /**
     * 分割
     *
     * @param str
     * @return
     */
    private static String[] toArr(String str) {
        if (!StringUtils.isEmpty(str)) {
            return str.trim().replaceAll("\\+|\\||\\s|;", ",").split(",");
        }
        return null;
    }

    /**
     * 统一国内名称
     *
     * @param str
     * @return
     */
    private static String countryName(String str) {
        if (str != null) {
            str = str.replaceAll("台湾", "中国台湾");
            str = str.replaceAll("香港", "中国香港").replaceAll("港台", "中国香港");
            str = str.replaceAll("中国大陆", "中国").replaceAll("大陆", "中国").replaceAll("内地", "中国").replaceAll("国内", "中国");
        }
        return str;
    }

    /**
     * 移动content转media
     *
     * @param TContentDO
     */
    public <T> MediaDTO(T param) {
        if (param instanceof TContentDO) {
            TContentDO content = (TContentDO) param;
            this.mediaId = content.getId();
            this.mediaCode = content.getContentId();
            this.name = content.getName();
            this.actor = toArr(content.getActorDisplay());
            this.director = toArr(content.getDirector());
            this.country = toArr(countryName(content.getOriginalCountry()));
            this.language = content.getLanguage();
            this.releaseYear = getInt(content.getRelaseYear());
            this.duration = getInt(content.getDuration());
            this.mediaTypeId = content.getContentType();
            this.mediaType = content.getMediaTypeId();
            //所属栏目
            this.mediaCategoryId = content.getCategoryId();
            this.mediaCategoryName = content.getCategoryName();
            this.cpId = content.getCpId();
            this.cpName = content.getCpName();
            this.spId = content.getSpid();
            this.spName = content.getSpname();
            this.searchName = content.getSearchName();
            this.description = content.getDescription();
            //判断是否为子集
            if ("EPISODE".equalsIgnoreCase(content.getContentType())) {
                this.parentMediaId = content.getContentSeriesId();
            }
            //判断是否为VIP
            if ("VIP".equalsIgnoreCase(content.getCategoryName())) {
                this.free = true;
            } else {
                this.free = false;
            }
            if (content.getOnllineDate() != null) {
                this.onLineDate = content.getOnllineDate();
            }
            // 搜索统计使用了HitCount字段
            this.searchCount = content.getSearchCount();
            // 点播统计使用了RecommendCount字段
            this.playCount = content.getRecommendCount();
            if (content.getOnlineStatus() != null & content.getOnlineStatus() != "" && content.getOnlineStatus().length() > 0) {
                if ("ONLINE".equalsIgnoreCase(content.getOnlineStatus())) {
                    this.publishStatus = 1;
                }
                if ("OFFLINE".equalsIgnoreCase(content.getOnlineStatus())) {
                    this.publishStatus = 0;
                }
            } else {
                this.publishStatus = 0;
            }
        }
        if (param instanceof TChannelDO) {
            TChannelDO content = (TChannelDO) param;
            this.mediaId = content.getId();
            this.mediaCode = content.getChannelId();
            this.name = content.getName();
            this.country = toArr(countryName(content.getCountry()));
            this.language = content.getLanguage();
            this.mediaTypeId = content.getContentType();
            this.mediaType = content.getMediaTypeId();
            //所属栏目
            this.mediaCategoryId = content.getCategoryId();
            this.mediaCategoryName = content.getCategoryName();
            this.cpId = content.getCpId();
            this.cpName = content.getCpName();
            this.spId = content.getSpid();
            this.spName = content.getSpname();
            //判断是否为VIP
            if ("VIP".equalsIgnoreCase(content.getCategoryName())) {
                this.free = true;
            } else {
                this.free = false;
            }

            // 搜索统计使用了HitCount字段
            this.searchCount = content.getSearchCount();
            // 点播统计使用了RecommendCount字段
            this.playCount = content.getRecommendCount();
            if (content.getOnlineStatus() != null & content.getOnlineStatus() != "" && content.getOnlineStatus().length() > 0) {
                if ("ONLINE".equalsIgnoreCase(content.getOnlineStatus())) {
                    this.publishStatus = 1;
                }
                if ("OFFLINE".equalsIgnoreCase(content.getOnlineStatus())) {
                    this.publishStatus = 0;
                }
            } else {
                this.publishStatus = 0;
            }
        }
    }

    private Integer getInt(String param) {
        try {
            if (StringUtils.isEmpty(param)) {
                return 0;
            }
            return Integer.valueOf(param);
        } catch (Exception e) {
            return 0;
        }
    }
}