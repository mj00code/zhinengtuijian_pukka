package com.ubo.iptv.manage.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author gz_recommend
 * @since 2020-12-09
 */
@Data
public class TagVO implements Serializable {

    /**
     * 主键
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 0:用户标签 1:媒资标签
     */
    @ApiModelProperty(value = "classify")
    private Integer classify;

    /**
     * 0:事实标签 1:活跃特征 2:订购类型特征 3:付费能力特征 4:偏好特征
     */
    @ApiModelProperty(value = "type")
    private Integer type;

    /**
     * 标签划分
     */
    @ApiModelProperty(value = "divide")
    private String divide;

    /**
     * 标签值
     */
    @ApiModelProperty(value = "value")
    private String value;

    /**
     * 标签名称
     */
    @ApiModelProperty(value = "name")
    private String name;

    /**
     * 是否是区间
     */
    @ApiModelProperty(value = "is_range")
    private Boolean isRange;

    /**
     * 开始数据
     */
    @ApiModelProperty(value = "range_from")
    private Integer rangeFrom;

    /**
     * 结束数据
     */
    @ApiModelProperty(value = "range_to")
    private Integer rangeTo;

    /**
     * 是否用于计算
     */
    @ApiModelProperty(value = "is_used_to_count")
    private Boolean isUsedToCount;


}
