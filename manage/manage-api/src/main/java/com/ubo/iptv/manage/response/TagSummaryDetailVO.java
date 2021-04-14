package com.ubo.iptv.manage.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author gz_recommend
 * @since 2020-12-09
 */
@Data
public class TagSummaryDetailVO {

    @ApiModelProperty(value = "标签说明")
    private String mark;

    @ApiModelProperty(value = "本次标签分布")
    private TagSummary tagSummary;

    @ApiModelProperty(value = "历史标签分布")
    private Map<String, TagSummary> historyTagSummary;

}
