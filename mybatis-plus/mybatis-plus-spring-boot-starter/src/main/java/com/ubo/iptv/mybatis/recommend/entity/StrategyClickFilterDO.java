package com.ubo.iptv.mybatis.recommend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
    * 点击过滤规则
    * </p>
 *
 * @author gz_recommend
 * @since 2020-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("gz_recommend.t_strategy_click_filter")
public class StrategyClickFilterDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 策略id
     */
    @TableField("strategy_id")
    private Long strategyId;

    /**
     * 媒资类型
     */
    @TableField("media_type")
    private Integer mediaType;

    /**
     * 播放分钟数
     */
    @TableField("play_minutes")
    private Integer playMinutes;

    /**
     * 不点击天数
     */
    @TableField("ignore_days")
    private Integer ignoreDays;

    /**
     * 排除天数
     */
    @TableField("freeze_days")
    private Integer freezeDays;


}
