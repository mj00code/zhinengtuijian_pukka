package com.ubo.iptv.entity.gdgd;

import com.ubo.iptv.mybatis.recommend.entity.SceneDO;
import lombok.Data;

/**
 * @Author: xuning
 * @Date: 2020-09-30
 */
@Data
public class SceneDTO {
    private SceneDO sceneDO;
    private StrategyDetailDTO codeBootStrategyDetail;
    private StrategyDetailDTO recommendStrategyDetail;
}
