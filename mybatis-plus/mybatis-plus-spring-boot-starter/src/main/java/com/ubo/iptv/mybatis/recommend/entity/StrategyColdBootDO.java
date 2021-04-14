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
    * 冷启动规则
    * </p>
 *
 * @author gz_recommend
 * @since 2020-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("gz_recommend.t_strategy_cold_boot")
public class StrategyColdBootDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 策略id
     */
    @TableField("strategy_id")
    private Long strategyId;

    /**
     * 点击次数
     */
    @TableField("click_nums")
    private Integer clickNums;


}
