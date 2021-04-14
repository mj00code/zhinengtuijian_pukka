package com.ubo.iptv.entity.gdgd.recommend;

import lombok.Data;

import java.util.List;

/**
 * @Author: xuning
 * @Date: 2020-09-30
 */
@Data
public class MediaTypeTopDTO extends RecommendCommonDTO {
    /**
     * 媒资类型
     */
    Integer mediaType;
    /**
     * 媒资题材列表
     */
    List<MediaKindTopDTO> mediaKindList;
}
