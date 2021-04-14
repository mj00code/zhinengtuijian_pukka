package com.ubo.iptv.manage.requset;

import com.ubo.iptv.manage.response.TagVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: xuning
 * @Date: 2020-11-10
 */
@Data
public class TagCondition {

    @ApiModelProperty(value = "标签id")
    private Long tagId;
    @ApiModelProperty(value = "左侧条件名称")
    private String conditionName;
    @ApiModelProperty(value = "查询条件 0:等于 1:不等于")
    private Integer searchCondition;
    @ApiModelProperty(value = "右侧条件列表")
    private List<TagVO> conditionList;
    @ApiModelProperty(value = "右侧如果是输入框,则传输入内容")
    private String inputValue;

}
