package com.ubo.iptv.entity.gdgd.recommend;

import lombok.Data;

import java.util.List;

/**
 * <p>
 * 推荐偏好配置
 * </p>
 *
 * @author gz_recommend
 * @since 2020-10-29
 */
@Data
public class RecommendEngineDTO extends RecommendCommonDTO {

    /**
     * 推荐引擎
     */
    private Integer recommendEngine;
    /**
     * 偏好设置专有
     */
    List<MediaTypeTopDTO> mediaTypeList;
    /**
     * 偏好设置专有(细化到收付费层面的数量)
     */
    List<MediaChargeFreeDTO> mediaChargeFreeList;
    /**
     * 是否计算题材
     */
    Boolean isCalculateKind;
}
