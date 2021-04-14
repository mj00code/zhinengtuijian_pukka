package com.ubo.iptv.entity.gdgd.recommend;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RecommendCommonDTO {
    /**
     * 媒资类型打分(题材偏好专用)
     */
    BigDecimal score;
    /**
     * 权重
     */
    private BigDecimal weight;
    /**
     * 收费比重
     */
    private BigDecimal chargeRatio;

    /**
     * 推荐数量
     */
    private Integer size;

}
