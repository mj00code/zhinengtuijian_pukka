package com.ubo.iptv.manage.response;

import com.ubo.iptv.manage.requset.TagCondition;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: xuning
 * @Date: 2020-11-10
 */
@Data
public class TagConditionVO {

    @ApiModelProperty(value = "标签id")
    private List<TagCondition> conditions;

    @ApiModelProperty(value = "查询条件 0:且 1:或")
    private Integer andOrFlag;

    @ApiModelProperty(value = "组名称")
    private String groupName;

    @ApiModelProperty(value = "组分类: 0用户组 1:媒资组")
    private Integer groupType;

    @ApiModelProperty(value = "组分类: 0用户组 1:媒资组")
    private Long groupId;

}
