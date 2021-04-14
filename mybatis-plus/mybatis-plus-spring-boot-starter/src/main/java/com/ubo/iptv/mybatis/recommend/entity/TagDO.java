package com.ubo.iptv.mybatis.recommend.entity;

import java.math.BigDecimal;
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
    * 
    * </p>
 *
 * @author gz_recommend
 * @since 2021-01-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("gz_recommend.t_tag")
public class TagDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 0:用户标签 1:媒资标签
     */
    @TableField("classify")
    private Integer classify;

    /**
     * 0:事实标签 1:活跃特征 2:订购类型特征 3:付费能力特征 4:偏好特征 5:媒资特征
     */
    @TableField("type")
    private Integer type;

    /**
     * 标签划分
     */
    @TableField("divide")
    private String divide;

    /**
     * 标签值
     */
    @TableField("value")
    private String value;

    /**
     * 标签名称
     */
    @TableField("name")
    private String name;

    /**
     * 是否是区间
     */
    @TableField("is_range")
    private Boolean isRange;

    /**
     * 开始数据
     */
    @TableField("range_from")
    private BigDecimal rangeFrom;

    /**
     * 结束数据
     */
    @TableField("range_to")
    private BigDecimal rangeTo;

    /**
     * 是否用于计算
     */
    @TableField("is_used_to_count")
    private Boolean isUsedToCount;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 字段名称
     */
    @TableField("field")
    private String field;

    /**
     * 规则说明
     */
    @TableField("mark")
    private String mark;


}
