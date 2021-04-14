package com.ubo.iptv.manage.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubo.iptv.core.CommonResponse;
import com.ubo.iptv.manage.requset.TagConditionDTO;
import com.ubo.iptv.manage.response.*;
import io.swagger.annotations.ApiParam;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: xuning
 * @Date: 2020-11-07
 */
public interface TagService {

    CommonResponse<List<TagMediaSummaryVO>> mediaGroupDetail(Long groupId);

    CommonResponse groupAdd(Long userId, TagConditionDTO dto);

    CommonResponse<Set<String>> selectConditionDivide(@ApiParam(value = "标签分类", required = true) @RequestParam(value = "classify") Integer classify);

    CommonResponse<List<TagVO>> selectConditionDivideResult(@ApiParam(value = "标签划分", required = true) @RequestParam(value = "divide") String divide);

    CommonResponse<Map<String, String>> userTagDivideList(@ApiParam(value = "标签分类", required = true) @RequestParam(value = "classify") Integer classify,
                                                          @ApiParam(value = "标签区分", required = true) @RequestParam(value = "type") Integer type);

    CommonResponse<TagSummaryDetailVO> tagSummaryDetail(String tagIds);

    CommonResponse<Map<String, TagSummary>> tagSummarySort(String tagIds, String sortDate, Boolean sortAsc);


    CommonResponse<UserTagImageVO> drawUserImage(Long userId);

    CommonResponse<IPage<GroupInfoVO>> userGroups(Page page);

    CommonResponse<UserGroupDetailVO> userGroupDetail(Long groupId);

    CommonResponse<IPage<GroupInfoVO>> mediaGroups(Page page);


    CommonResponse saveDailyGroupStatus(Long groupId);

    CommonResponse<IPage<TagUserSummaryVO>> tagUserList(String tagIds, Page page);

    CommonResponse groupDelete(Long groupId);

    CommonResponse<TagConditionVO> groupSelect(Long groupId);

    CommonResponse groupUpdate(Long userId, TagConditionDTO dto);
}
