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

/**
 * <p>
    * 媒资类型分布
    * </p>
 *
 * @author gz_recommend
 * @since 2020-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("gz_recommend.t_strategy_media_type")
public class StrategyMediaTypeDO implements Serializable {

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
     * 权重
     */
    @TableField("weight")
    private BigDecimal weight;

    /**
     * 收费比例
     */
    @TableField("charge_ratio")
    private BigDecimal chargeRatio;


}
