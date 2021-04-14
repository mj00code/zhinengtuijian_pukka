package com.ubo.iptv.manage.response;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: xuning
 * @Date: 2020-11-09
 */
@Data
public class StrategyInfoVO {
    @ApiModelProperty(value = "场景列表")
    IPage<StrategyVO> page;

    @ApiModelProperty(value = "开关")
    private Boolean openFlag;
}
