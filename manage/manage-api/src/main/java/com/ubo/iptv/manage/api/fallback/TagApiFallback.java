package com.ubo.iptv.manage.api.fallback;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ubo.iptv.core.CommonResponse;
import com.ubo.iptv.manage.api.TagApi;
import com.ubo.iptv.manage.requset.TagConditionDTO;
import com.ubo.iptv.manage.response.*;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: xuning
 * @Date: 2020-11-09
 */
@Component
@Slf4j
public class TagApiFallback implements FallbackFactory<TagApi> {
    @Override
    public TagApi create(Throwable throwable) {
        return new TagApi() {
            @Override
            public CommonResponse<Map<String, String>> userTagDivideList(Integer classify, Integer type) {
                log.error(throwable.getMessage());
                return CommonResponse.FALL_BACK;
            }

            @Override
            public CommonResponse<TagSummaryDetailVO> tagSummaryDetail(String tagIds) {
                log.error(throwable.getMessage());
                return CommonResponse.FALL_BACK;
            }

            @Override
            public CommonResponse<IPage<TagUserSummaryVO>> tagUserList(String tagIds, Integer current, Integer size) {
                log.error(throwable.getMessage());
                return CommonResponse.FALL_BACK;
            }

            @Override
            public CommonResponse<UserTagImageVO> drawUserImage(Long userId) {
                log.error(throwable.getMessage());
                return CommonResponse.FALL_BACK;
            }

            @Override
            public CommonResponse<IPage<GroupInfoVO>> userGroups( Integer current, Integer size) {
                log.error(throwable.getMessage());
                return CommonResponse.FALL_BACK;
            }

            @Override
            public CommonResponse<UserGroupDetailVO> userGroupDetail(Long groupId) {
                log.error(throwable.getMessage());
                return CommonResponse.FALL_BACK;
            }

            @Override
            public CommonResponse<List<TagMediaSummaryVO>> mediaGroupDetail(Long groupId) {
                log.error(throwable.getMessage());
                return CommonResponse.FALL_BACK;
            }


            @Override
            public void exportUserGroup(Long groupId) {
            }


            @Override
            public CommonResponse<IPage<GroupInfoVO>> mediaGroups( Integer current, Integer size) {
                log.error(throwable.getMessage());
                return CommonResponse.FALL_BACK;
            }

            @Override
            public CommonResponse groupAdd(TagConditionDTO dto) {
                log.error(throwable.getMessage());
                return CommonResponse.FALL_BACK;
            }

            @Override
            public CommonResponse groupUpdate(TagConditionDTO dto) {
                log.error(throwable.getMessage());
                return CommonResponse.FALL_BACK;
            }

            @Override
            public CommonResponse groupDelete(Long groupId) {
                return null;
            }

            @Override
            public CommonResponse<TagConditionVO> groupSelect(Long groupId) {
                return null;
            }

            @Override
            public void exportMediaGroup(Long groupId) {
            }


            @Override
            public CommonResponse<Set<String>> selectConditionDivide(Integer classify) {
                log.error(throwable.getMessage());
                return CommonResponse.FALL_BACK;
            }

            @Override
            public CommonResponse<List<TagVO>> selectConditionDivideResult(String divide) {
                log.error(throwable.getMessage());
                return CommonResponse.FALL_BACK;
            }

            @Override
            public CommonResponse<Map<String, TagSummary>> tagSummarySort(String tagIds, String sortDate, Boolean sortAsc) {
                log.error(throwable.getMessage());
                return CommonResponse.FALL_BACK;
            }

            @Override
            public CommonResponse saveDailyGroupStatus(Long groupId) {
                log.error(throwable.getMessage());
                return CommonResponse.FALL_BACK;
            }
        };
    }
}
