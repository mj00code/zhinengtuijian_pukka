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
    * 人工干预配置
    * </p>
 *
 * @author gz_recommend
 * @since 2021-01-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("gz_recommend.t_strategy_manual")
public class StrategyManualDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 策略id
     */
    @TableField("strategy_id")
    private Long strategyId;

    /**
     * 媒资id
     */
    @TableField("media_id")
    private Integer mediaId;

    /**
     * 媒资类型
     */
    @TableField("media_type")
    private Integer mediaType;

    /**
     * 媒资名称
     */
    @TableField("media_name")
    private String mediaName;

    /**
     * 媒资code
     */
    @TableField("media_code")
    private String mediaCode;

    /**
     * 是否置顶 1:置顶；0:必推
     */
    @TableField("top")
    private Boolean top;

    /**
     * 生效开始时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 截止时间
     */
    @TableField("expire_time")
    private LocalDateTime expireTime;

    /**
     * 状态：1:生效；0:失效
     */
    @TableField("status")
    private Integer status;

    /**
     * 海报图片url
     */
    @TableField("poster_url")
    private String posterUrl;


}
