package com.ubo.iptv.manage.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
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
public class UserTagImageVO {

    @ApiModelProperty(value = "用户基本信息")
    private UserBasicInfo userBasicInfo;

    @ApiModelProperty(value = "标签信息")
    private Map<String, List<TagVO>> tags;

}
