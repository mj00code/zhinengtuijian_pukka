package com.ubo.iptv.entity.gdgd.recommend;

import lombok.Data;

/**
 * @Author: xuning
 * @Date: 2020-09-30
 */
@Data
public class MediaChargeFreeDTO {
    /**
     * 媒资类型
     */
    Integer mediaType;
    /**
     * 媒资题材
     */
    Integer mediaKind;
    /**
     * 是否收费
     */
    Integer isCharge;
    /**
     * 数量
     */
    private Integer size;
}
