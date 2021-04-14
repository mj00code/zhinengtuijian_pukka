package com.ubo.iptv.recommend.service;

import com.ubo.iptv.core.CommonResponse;
import com.ubo.iptv.recommend.response.IPTVMediaVO;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


/**
 * Created by uboo on 2019/6/17.
 */
public interface IPTVService {
    CommonResponse<List<IPTVMediaVO>> recommendPrograms(@ApiParam(value = "渠道id", required = true) @RequestParam(value = "sysId") String sysId,
                                                        @ApiParam(value = "用户id", required = true) @RequestParam(value = "userId") String userId,
                                                        @ApiParam(value = "场景编号", required = true) @RequestParam(value = "sceneId") Integer sceneId,
                                                        @ApiParam(value = "推荐数量", required = true) @RequestParam(value = "size") Integer size,
                                                        @ApiParam(value = "栏目Id") @RequestParam(value = "categoryId", required = false) Integer categoryId,
                                                        @ApiParam(value = "媒资id") @RequestParam(value = "mediaId", required = false) String mediaId);
}
