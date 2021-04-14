package com.ubo.iptv.recommend.controller;

import com.ubo.iptv.core.AbstractController;
import com.ubo.iptv.core.CommonResponse;
import com.ubo.iptv.recommend.api.IPTVApi;
import com.ubo.iptv.recommend.response.IPTVMediaVO;
import com.ubo.iptv.recommend.service.IPTVService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: xuning
 * @Date: 2020-09-30
 */
@Api(value = "iptv api", tags = "IPTV", description = "IPTV相关接口")
@RestController
public class IPTVController extends AbstractController implements IPTVApi {
    @Autowired
    IPTVService iptvService;

    @Override
    public CommonResponse<List<IPTVMediaVO>> recommend(@ApiParam(value = "渠道id", required = true) @RequestParam(value = "sysId") String sysId,
                                                       @ApiParam(value = "用户id", required = true) @RequestParam(value = "userId") String userId,
                                                       @ApiParam(value = "场景编号", required = true) @RequestParam(value = "sceneId") Integer sceneId,
                                                       @ApiParam(value = "推荐数量", required = true) @RequestParam(value = "size") Integer size,
                                                       @ApiParam(value = "栏目Id") @RequestParam(value = "categoryId", required = false) Integer categoryId,
                                                       @ApiParam(value = "媒资id") @RequestParam(value = "mediaId", required = false) String mediaId,
                                                       @ApiParam(value = "页面url") @RequestParam(value = "pageUrl", required = false) String pageUrl) {
        return iptvService.recommendPrograms(sysId, userId, sceneId, size, categoryId,mediaId);
    }
}
