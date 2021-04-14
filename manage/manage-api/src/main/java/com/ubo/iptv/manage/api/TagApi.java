package com.ubo.iptv.manage.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ubo.iptv.core.CommonResponse;
import com.ubo.iptv.core.annotation.APIType;
import com.ubo.iptv.core.enums.APITypeEnum;
import com.ubo.iptv.manage.api.fallback.TagApiFallback;
import com.ubo.iptv.manage.requset.TagConditionDTO;
import com.ubo.iptv.manage.response.*;
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
import java.util.Map;
import java.util.Set;

/**
 * @Author: xuning
 * @Date: 2020-11-09
 */
@FeignClient(value = "iptv-manage", fallbackFactory = TagApiFallback.class)
@Api(value = "tag api", tags = "标签接口", description = "标签接口")
@APIType(APITypeEnum.PUBLIC)
public interface TagApi {

    /**
     * 3.8	用户标签-下拉列表接口
     *
     * @param classify
     * @param type
     * @return
     */
    @ApiOperation(value = "用户标签-下拉列表接口", httpMethod = "GET", notes = "0:成功，1：失败")
    @RequestMapping(value = "/v1/tag/condition/user/tag", method = RequestMethod.GET)
    CommonResponse<Map<String, String>> userTagDivideList(@ApiParam(value = "标签分类", required = true) @RequestParam(value = "classify") Integer classify,
                                                          @ApiParam(value = "标签区分", required = true) @RequestParam(value = "type") Integer type);


    /**
     * 3.9	用户标签-标签统计接口
     *
     * @param tagIds
     * @return
     */
    @ApiOperation(value = "用户标签-标签统计接口", httpMethod = "GET", notes = "0:成功，1：失败")
    @RequestMapping(value = "/v1/tag/summary/detail", method = RequestMethod.GET)
    CommonResponse<TagSummaryDetailVO> tagSummaryDetail(@ApiParam(value = "标签", required = true) @RequestParam(value = "tagIds") String tagIds);

    /**
     * 3.9	用户标签-标签用户列表接口
     *
     * @param tagIds
     * @return
     */
    @ApiOperation(value = "用户标签-标签用户列表接口", httpMethod = "GET", notes = "0:成功，1：失败")
    @RequestMapping(value = "/v1/tag/summary/user/list", method = RequestMethod.GET)
    CommonResponse<IPage<TagUserSummaryVO>> tagUserList(@ApiParam(value = "标签", required = true) @RequestParam(value = "tagIds") String tagIds,
                                                        @ApiParam(value = "current") @RequestParam(value = "current", required = false, defaultValue = "1") Integer current,
                                                        @ApiParam(value = "size") @RequestParam(value = "size", required = false, defaultValue = "10") Integer size);

    /**
     * 3.10	用户标签-单用户画像接口
     *
     * @param userId
     * @return
     */
    @ApiOperation(value = "用户标签-单用户画像接口", httpMethod = "GET", notes = "0:成功，1：失败")
    @RequestMapping(value = "/v1/tag/user/image", method = RequestMethod.GET)
    CommonResponse<UserTagImageVO> drawUserImage(@ApiParam(value = "用户id", required = true) @RequestParam(value = "userId") Long userId);

    /**
     * 3.11	用户分群-列表接口
     *
     * @return
     */
    @ApiOperation(value = "用户分群-列表接口", httpMethod = "GET", notes = "0:成功，1：失败")
    @RequestMapping(value = "/v1/tag/user/group/list", method = RequestMethod.GET)
    CommonResponse<IPage<GroupInfoVO>> userGroups(@ApiParam(value = "current") @RequestParam(value = "current", required = false, defaultValue = "1") Integer current,
                                                 @ApiParam(value = "size") @RequestParam(value = "size", required = false, defaultValue = "10") Integer size);

    /**
     * 3.12	用户分群-详情接口
     *
     * @return
     */
    @ApiOperation(value = "用户分群-详情接口", httpMethod = "GET", notes = "0:成功，1：失败")
    @RequestMapping(value = "/v1/tag/user/group/detail", method = RequestMethod.GET)
    CommonResponse<UserGroupDetailVO> userGroupDetail(@ApiParam(value = "分组id", required = true) @RequestParam(value = "groupId") Long groupId);

    /**
     * 13 新建分群接口
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "新建分群接口", httpMethod = "POST", notes = "0:成功，1：失败")
    @RequestMapping(value = "/v1/tag/group/add", method = RequestMethod.POST)
    CommonResponse groupAdd(@ApiParam(value = "参数", required = true) @RequestBody @Valid TagConditionDTO dto);

    /**
     * 13 新建分群接口
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "新建分群接口", httpMethod = "POST", notes = "0:成功，1：失败")
    @RequestMapping(value = "/v1/tag/group/update", method = RequestMethod.POST)
    CommonResponse groupUpdate(@ApiParam(value = "参数", required = true) @RequestBody @Valid TagConditionDTO dto);

    /**
     * 13 删除分群接口
     *
     * @param
     * @return
     */
    @ApiOperation(value = "删除分群接口", httpMethod = "GET", notes = "0:成功，1：失败")
    @RequestMapping(value = "/v1/tag/group/delete", method = RequestMethod.GET)
    CommonResponse groupDelete(@ApiParam(value = "分组id", required = true) @RequestParam(value = "groupId") Long groupId);

    /**
     * 13 查看分群接口
     *
     * @param
     * @return
     */
    @ApiOperation(value = "查看分群接口", httpMethod = "GET", notes = "0:成功，1：失败")
    @RequestMapping(value = "/v1/tag/group/select", method = RequestMethod.GET)
    CommonResponse<TagConditionVO> groupSelect(@ApiParam(value = "分组id", required = true) @RequestParam(value = "groupId") Long groupId);

    /**
     * 3.13	媒资分群-详情接口
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "媒资分群-详情接口", httpMethod = "GET", notes = "0:成功，1：失败")
    @RequestMapping(value = "/v1/tag/media/group/detail", method = RequestMethod.GET)
    CommonResponse<List<TagMediaSummaryVO>> mediaGroupDetail(@ApiParam(value = "分组id", required = true) @RequestParam(value = "groupId") Long groupId);


    /**
     * 3.14	用户分群-用户分群导出接口
     *
     * @param groupId
     * @return
     */
    @ApiOperation(value = "用户分群-用户分群导出接口", httpMethod = "GET", notes = "0:成功，1：失败")
    @RequestMapping(value = "/v1/tag/user/group/export", method = RequestMethod.GET)
    void exportUserGroup(@ApiParam(value = "分组id", required = true) @RequestParam(value = "groupId") Long groupId);

    /**
     * 3.15	媒资分群-列表接口
     *
     * @return
     */
    @ApiOperation(value = "媒资分群-列表接口", httpMethod = "GET", notes = "0:成功，1：失败")
    @RequestMapping(value = "/v1/tag/media/group/list", method = RequestMethod.GET)
    CommonResponse<IPage<GroupInfoVO>> mediaGroups(@ApiParam(value = "current") @RequestParam(value = "current", required = false, defaultValue = "1") Integer current,
                                                  @ApiParam(value = "size") @RequestParam(value = "size", required = false, defaultValue = "10") Integer size);


    /**
     * 3.17	媒资分群-媒资分群导出接口
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "媒资分群-媒资分群导出接口", httpMethod = "GET", notes = "0:成功，1：失败")
    @RequestMapping(value = "/v1/tag/media/group/export", method = RequestMethod.GET)
    void exportMediaGroup(@ApiParam(value = "分组id", required = true) @RequestParam(value = "groupId") Long groupId);

    /**
     * 3.18	用户,媒资分群通用-分群条件列表接口
     *
     * @param classify
     * @return
     */
    @ApiOperation(value = "用户,媒资分群通用-分群条件列表接口", httpMethod = "GET", notes = "0:成功，1：失败")
    @RequestMapping(value = "/v1/tag/condition/group/divide", method = RequestMethod.GET)
    CommonResponse<Set<String>> selectConditionDivide(@ApiParam(value = "标签分类", required = true) @RequestParam(value = "classify") Integer classify);

    /**
     * 3.19	用户,媒资分群通用-分群条件结果列表接口
     *
     * @param divide
     * @return
     */
    @ApiOperation(value = "用户,媒资分群通用-分群条件结果列表接口", httpMethod = "GET", notes = "0:成功，1：失败")
    @RequestMapping(value = "/v1/tag/condition/group/divide/result", method = RequestMethod.GET)
    CommonResponse<List<TagVO>> selectConditionDivideResult(@ApiParam(value = "标签划分", required = true) @RequestParam(value = "divide") String divide);

    /**
     * 历史标签统计排序接口
     *
     * @param tagIds
     * @return
     */
    @ApiOperation(value = "历史标签统计排序接口", httpMethod = "GET", notes = "0:成功，1：失败")
    @RequestMapping(value = "/v1/tag/summary/sort", method = RequestMethod.GET)
    CommonResponse<Map<String, TagSummary>> tagSummarySort(@ApiParam(value = "标签分类", required = true) @RequestParam(value = "tagIds") String tagIds,
                                                           @ApiParam(value = "排序日期") @RequestParam(value = "sortDate", required = false) String sortDate,
                                                           @ApiParam(value = "是否正序") @RequestParam(value = "sortAsc", required = false) Boolean sortAsc);


    /**
     * 保存分组目前的数据 (人数,活跃分析等)
     *
     * @param groupId
     * @return
     */
    @ApiOperation(value = "保存分组目前的数据", httpMethod = "GET", notes = "0:成功，1：失败")
    @RequestMapping(value = "/v1/tag/daily/summary", method = RequestMethod.GET)
    CommonResponse saveDailyGroupStatus(@ApiParam(value = "分组id", required = true) @RequestParam(value = "groupId") Long groupId);
}
