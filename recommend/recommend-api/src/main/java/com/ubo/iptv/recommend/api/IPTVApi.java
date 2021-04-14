package com.ubo.iptv.recommend.api;

import com.ubo.iptv.core.CommonResponse;
import com.ubo.iptv.core.annotation.APIType;
import com.ubo.iptv.core.enums.APITypeEnum;
import com.ubo.iptv.recommend.api.fallback.IPTVApiFallback;
import com.ubo.iptv.recommend.response.IPTVMediaVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author: xuning
 * @Date: 2020-09-30
 */
@Api(value = "recommend api", tags = "IPTV", description = "IPTV相关接口")
@FeignClient(value = "iptv-recommend", fallbackFactory = IPTVApiFallback.class)
@APIType(APITypeEnum.PUBLIC)
public interface IPTVApi {

    /**
     * 智能推荐
     *
     * @param sysId
     * @param userId
     * @param sceneId
     * @param size
     * @param mediaId
     * @return
     */
    @RequestMapping(value = "/v1/iptv/recommend/media", method = RequestMethod.GET)
    CommonResponse<List<IPTVMediaVO>> recommend(@ApiParam(value = "渠道id", required = true) @RequestParam(value = "sysId") String sysId,
                                                @ApiParam(value = "用户id", required = true) @RequestParam(value = "userId") String userId,
                                                @ApiParam(value = "场景编号", required = true) @RequestParam(value = "sceneId") Integer sceneId,
                                                @ApiParam(value = "推荐数量", required = true) @RequestParam(value = "size") Integer size,
                                                @ApiParam(value = "栏目Id") @RequestParam(value = "categoryId", required = false) Integer categoryId,
                                                @ApiParam(value = "媒资code") @RequestParam(value = "mediaId", required = false) String mediaId,
                                                @ApiParam(value = "页面url") @RequestParam(value = "pageUrl", required = false) String pageUrl);


}
