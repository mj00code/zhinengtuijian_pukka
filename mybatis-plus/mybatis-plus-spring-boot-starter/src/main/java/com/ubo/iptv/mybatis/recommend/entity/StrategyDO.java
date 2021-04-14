package com.ubo.iptv.mybatis.recommend.entity;

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
    * 推荐策略
    * </p>
 *
 * @author gz_recommend
 * @since 2020-11-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("gz_recommend.t_strategy")
public class StrategyDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 策略id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 运营商id
     */
    @TableField("sys_id")
    private String sysId;

    /**
     * 策略名称
     */
    @TableField("name")
    private String name;

    /**
     * 策略类型：0-冷启动；1-智能推荐
     */
    @TableField("type")
    private Integer type;

    /**
     * 场景id
     */
    @TableField("scene_id")
    private Long sceneId;

    /**
     * 创建人编号
     */
    @TableField("createor_id")
    private Long createorId;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;


}
