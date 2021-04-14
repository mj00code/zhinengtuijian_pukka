package com.ubo.iptv.mybatis.recommend.entity;

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
 * 媒资每日小结
 * </p>
 *
 * @author gz_recommend
 * @since 2020-11-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("gz_recommend.t_media_summary")
public class MediaSummaryDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 运营商id
     */
    @TableField("sys_id")
    private String sysId;

    /**
     * 媒资id
     */
    @TableField("media_id")
    private Integer mediaId;

    @TableField("media_code")
    private String mediaCode;

    /**
     * 阶段搜索统计数
     */
    @TableField("search_count")
    private Integer searchCount;

    /**
     * 阶段搜索统计截至日期
     */
    @TableField("search_count_deadline")
    private LocalDateTime searchCountDeadline;

    /**
     * 阶段点播统计数
     */
    @TableField("play_count")
    private Integer playCount;

    /**
     * 阶段点播统计截至日期
     */
    @TableField("play_count_deadline")
    private LocalDateTime playCountDeadline;

    /**
     * 总搜索统计数
     */
    @TableField("search_total_count")
    private Integer searchTotalCount;

    /**
     * 总播放统计数
     */
    @TableField("play_total_count")
    private Integer playTotalCount;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

}
