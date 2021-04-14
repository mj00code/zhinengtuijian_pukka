package com.ubo.iptv.manage.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ubo.iptv.core.CommonResponse;
import com.ubo.iptv.core.annotation.APIType;
import com.ubo.iptv.core.enums.APITypeEnum;
import com.ubo.iptv.manage.api.fallback.BlacklistApiFallback;
import com.ubo.iptv.manage.requset.BlacklistAddDTO;
import com.ubo.iptv.manage.response.BlacklistVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author: xuning
 * @Date: 2020-11-09
 */
@FeignClient(value = "iptv-manage", fallbackFactory = BlacklistApiFallback.class)
@Api(value = "blacklist api", tags = "黑名单", description = "黑名单相关接口")
@APIType(APITypeEnum.PUBLIC)
public interface BlacklistApi {

    /**
     * 黑名单列表
     *
     * @param content
     * @param page
     * @param size
     * @return
     */
    @ApiOperation(value = "黑名单列表", httpMethod = "GET", notes = "0:成功，1：失败")
    @RequestMapping(value = "/v1/blacklist/list", method = RequestMethod.GET)
    CommonResponse<IPage<BlacklistVO>> listBlacklist(@ApiParam(value = "媒资名称/id") @RequestParam(value = "content", required = false) String content,
                                                     @ApiParam(value = "当前页") @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                     @ApiParam(value = "每页数量") @RequestParam(value = "size", required = false, defaultValue = "10") Integer size);

    /**
     * 黑名单搜索媒资列表
     *
     * @param content
     * @return
     */
    @ApiOperation(value = "黑名单搜索媒资列表", httpMethod = "GET", notes = "0:成功，1：失败")
    @RequestMapping(value = "/v1/blacklist/search", method = RequestMethod.GET)
    CommonResponse<List<BlacklistVO>> searchMedia(@ApiParam(value = "运营商id") @RequestParam(value = "sysId", required = false) String sysId,
                                                  @ApiParam(value = "媒资名称/id", required = true) @RequestParam(value = "content") String content);

    /**
     * 加入黑名单
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "加入黑名单", httpMethod = "POST", notes = "0:成功，1：失败")
    @RequestMapping(value = "/v1/blacklist/add", method = RequestMethod.POST)
    CommonResponse addBlackList(@ApiParam(value = "参数", required = true) @RequestBody @Valid BlacklistAddDTO dto);

    /**
     * 取消黑名单
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "取消黑名单", httpMethod = "POST", notes = "0:成功，1：失败")
    @RequestMapping(value = "/v1/blacklist/remove", method = RequestMethod.POST)
    CommonResponse removeBlackList(@ApiParam(value = "黑名单id", required = true) @RequestParam(value = "id") Long id);
}
