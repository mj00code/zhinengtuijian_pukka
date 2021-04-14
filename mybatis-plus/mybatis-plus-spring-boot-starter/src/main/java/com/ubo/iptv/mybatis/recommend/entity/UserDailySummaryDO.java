package com.ubo.iptv.mybatis.recommend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * <p>
 * 用户每日小结
 * </p>
 *
 * @author gz_recommend
 * @since 2020-11-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("gz_recommend.t_user_daily_summary")
public class UserDailySummaryDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 运营商id
     */
    @TableField("sys_id")
    private String sysId;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 媒资类型枚举
     */
    @TableField("media_type")
    private Integer mediaType;

    /**
     * 媒资类型id
     */
    @TableField("media_type_id")
    private Integer mediaTypeId;

    /**
     * 媒资类型名称
     */
    @TableField("media_type_name")
    private String mediaTypeName;

    /**
     * 媒资题材id
     */
    @TableField("media_kind_id")
    private Integer mediaKindId;

    /**
     * 媒资题材名称
     */
    @TableField("media_kind_name")
    private String mediaKindName;

    /**
     * 点击次数
     */
    @TableField("click_count")
    private Integer clickCount;

    /**
     * 播放次数
     */
    @TableField("play_count")
    private Integer playCount;

    /**
     * 日期
     */
    @TableField("day")
    private LocalDate day;

    @TableField(exist = false)
    private BigDecimal score;
}
