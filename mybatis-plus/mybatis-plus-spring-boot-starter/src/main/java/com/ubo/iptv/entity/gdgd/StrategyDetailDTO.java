package com.ubo.iptv.entity.gdgd;

import com.ubo.iptv.mybatis.recommend.entity.*;
import lombok.Data;

import java.util.List;

/**
 * @Author: xuning
 * @Date: 2020-09-30
 */
@Data
public class StrategyDetailDTO {
    private StrategyDO strategyDO;
    private StrategyColdBootDO strategyColdBootDO;
    private StrategyBrowseFilterDO strategyBrowseFilterDO;
    private List<StrategyClickFilterDO> strategyClickFilterList;
    private List<StrategyRecommendEngineDO> strategyRecommendEngineList;
    private List<StrategyManualDO> strategyManualList;
    private List<StrategyMediaSortDO> strategyMediaSortList;
    private List<StrategyMediaTypeDO> strategyMediaTypeList;
}
