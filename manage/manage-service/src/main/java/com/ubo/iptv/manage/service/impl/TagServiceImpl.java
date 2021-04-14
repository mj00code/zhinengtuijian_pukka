package com.ubo.iptv.manage.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubo.iptv.core.CommonResponse;
import com.ubo.iptv.core.constant.RedisConstant;
import com.ubo.iptv.core.enums.SysEnum;
import com.ubo.iptv.core.util.DateUtil;
import com.ubo.iptv.entity.gdgd.UserDTO;
import com.ubo.iptv.manage.entity.TagRate;
import com.ubo.iptv.manage.enums.ManageErrorEnum;
import com.ubo.iptv.manage.mapper.TagSummaryMapper;
import com.ubo.iptv.manage.requset.TagCondition;
import com.ubo.iptv.manage.requset.TagConditionDTO;
import com.ubo.iptv.manage.response.*;
import com.ubo.iptv.manage.service.EsReportService;
import com.ubo.iptv.manage.service.RedisService;
import com.ubo.iptv.manage.service.TagService;
import com.ubo.iptv.mybatis.recommend.entity.*;
import com.ubo.iptv.mybatis.recommend.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: xuning
 * @Date: 2020-11-07
 */
@Service
@Slf4j
public class TagServiceImpl implements TagService {
    @Autowired
    TagMapper tagMapper;
    @Autowired
    UserTagMapper userTagMapper;
    @Autowired
    TagDailySummaryMapper tagDailySummaryMapper;
    @Autowired
    TagSummaryMapper tagSummaryMapper;
    @Autowired
    TagGroupsMapper tagGroupsMapper;
    @Autowired
    GroupDailySummaryMapper groupDailySummaryMapper;
    @Autowired
    GroupTypeDailySummaryMapper groupTypeDailySummaryMapper;
    @Autowired
    AccountMapper accountMapper;
    @Autowired
    EsReportService esReportService;
    @Autowired
    RedisService redisService;

    @Override
    public CommonResponse<List<TagMediaSummaryVO>> mediaGroupDetail(Long groupId) {
        List<TagMediaSummaryVO> list = new ArrayList<>();
        TagGroupsDO tagGroupsDO = tagGroupsMapper.selectById(groupId);
        if (null != tagGroupsDO) {
            list = (List<TagMediaSummaryVO>) esReportService.groupMatchList(tagGroupsDO, 10);
        }
        return CommonResponse.success(list);
    }

    @Override
    public CommonResponse groupAdd(Long userId, TagConditionDTO dto) {

        TagGroupsDO tagGroupsDO = new TagGroupsDO();
        tagGroupsDO.setGroupName(dto.getGroupName());
        tagGroupsDO.setType(dto.getGroupType());
        tagGroupsDO.setQuery(JSONObject.toJSONString(dto));
        AccountDO accountDO = accountMapper.selectById(userId);
        if (null != accountDO) {
            tagGroupsDO.setCreateUser(accountDO.getName());
        }
        tagGroupsMapper.insert(tagGroupsDO);
        //获取数量
        saveDailyGroupStatus(tagGroupsDO.getId());
        return CommonResponse.SUCCESS;
    }

    @Override
    public CommonResponse<Set<String>> selectConditionDivide(Integer classify) {
        List<TagDO> tagDOS = tagMapper.selectList(new LambdaQueryWrapper<TagDO>().eq(TagDO::getClassify, classify).orderByAsc(TagDO::getId));
        Set<String> set = new LinkedHashSet<>();
        if (!CollectionUtils.isEmpty(tagDOS)) {
            tagDOS.forEach(tagDO -> {
                set.add(tagDO.getDivide());
            });
        }
        return CommonResponse.success(set);
    }

    @Override
    public CommonResponse<List<TagVO>> selectConditionDivideResult(String divide) {
        List<TagDO> tagDOS = tagMapper.selectList(new LambdaQueryWrapper<TagDO>().eq(TagDO::getDivide, divide));
        List<TagVO> tagVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(tagDOS)) {
            tagDOS.forEach(tagDO -> {
                TagVO vo = new TagVO();
                BeanUtils.copyProperties(tagDO, vo);
                tagVOS.add(vo);
            });
        }
        return CommonResponse.success(tagVOS);
    }

    @Override
    public CommonResponse<Map<String, String>> userTagDivideList(Integer classify, Integer type) {
        List<TagDO> tagDOS = tagMapper.selectList(new LambdaQueryWrapper<TagDO>().eq(TagDO::getClassify, classify).eq(TagDO::getType, type).orderByAsc(TagDO::getId));
        Map<String, String> map = new LinkedHashMap<>();
        if (!CollectionUtils.isEmpty(tagDOS)) {
            tagDOS.forEach(tagDO -> {
                //如果是区间,则保存划分,不是区间,保存标签名称
                if (tagDO.getIsRange()) {
                    if (!map.containsKey(tagDO.getDivide())) {
                        String tagIds = tagDO.getId().toString();
                        map.put(tagDO.getDivide(), tagIds);
                        return;
                    }
                    String tagIds = map.get(tagDO.getDivide());
                    tagIds = tagIds + "," + tagDO.getId().toString();
                    map.put(tagDO.getDivide(), tagIds);

                } else {
                    String tagIds = tagDO.getId().toString();
                    map.put(tagDO.getValue(), tagIds);
                }
            });
        }
        return CommonResponse.success(map);
    }

    @Override
    public CommonResponse<TagSummaryDetailVO> tagSummaryDetail(String tagIds) {
        String[] tags = tagIds.split(",");
        if (tags.length <= 0) {
            return CommonResponse.build(ManageErrorEnum.NON_TAG_SELECT);
        }
        TagDO tagDO = tagMapper.selectOne(new LambdaQueryWrapper<TagDO>().eq(TagDO::getId, tags[0]));
        TagSummaryDetailVO vo = new TagSummaryDetailVO();
        vo.setMark(tagDO.getMark());
        TagSummary tagSummary = new TagSummary();
        List<TagDaySummary> tagDaySummarys = new ArrayList<>();
        LambdaQueryWrapper<TagDailySummaryDO> wrapper = new LambdaQueryWrapper();
        wrapper.in(TagDailySummaryDO::getTagId, tags);
        //今日
        List<TagRate> tadayTagRates = tagSummaryMapper.tadayTagRate(wrapper);
        tadayTagRates.forEach(tagRate -> {
            String date = DateUtil.format(tagRate.getDate(), "yyyy-MM-dd");
            TagDaySummary tagDaySummary = new TagDaySummary();
            tagDaySummary.setSummaryDate(date);
            tagDaySummary.setAllCount(tagRate.getAllCount());
            tagDaySummary.setRate(tagRate.getRate());
            tagDaySummary.setSummaryCount(tagRate.getCount());
            tagDaySummary.setTagName(tagRate.getTagName());
            tagDaySummary.setTagId(tagRate.getTagId());
            tagDaySummarys.add(tagDaySummary);
        });
        tagSummary.setTagAllDaysSummarys(tagDaySummarys);
        vo.setTagSummary(tagSummary);

        //历史
        CommonResponse<Map<String, TagSummary>> historyTagSummaryMapComm = tagSummarySort(tagIds, null, null);
        if (historyTagSummaryMapComm._isOk()) {
            Map<String, TagSummary> historyTagSummaryMap = historyTagSummaryMapComm.getData();
            vo.setHistoryTagSummary(historyTagSummaryMap);
        }
        return CommonResponse.success(vo);
    }

    @Override
    public CommonResponse<Map<String, TagSummary>> tagSummarySort(String tagIds, String sortDate, Boolean sortAsc) {
        //历史
        String[] tags = tagIds.split(",");
        if (tags.length <= 0) {
            return CommonResponse.build(ManageErrorEnum.NON_TAG_SELECT);
        }
        Map<String, TagSummary> historyTagSummaryMap = new LinkedHashMap<>();
        LambdaQueryWrapper<TagDailySummaryDO> wrapper = new LambdaQueryWrapper();
        wrapper.in(TagDailySummaryDO::getTagId, tags);
        QueryWrapper<TagDailySummaryDO> wrapper1 = new QueryWrapper();
        wrapper1.in("v.tag_id", tags);
        List<TagRate> tagRates = tagSummaryMapper.tagRate(wrapper, wrapper1);
        Set<String> dates = new HashSet<>();
        tagRates.forEach(tagRate -> {
            //按照tagId区分
            String tagName = tagRate.getTagName();
            if (!historyTagSummaryMap.containsKey(tagName)) {
                TagSummary historyTagSummary = new TagSummary();
                List<TagDaySummary> tagDaySummaryList = new ArrayList<>();
                historyTagSummary.setTagAllDaysSummarys(tagDaySummaryList);
                historyTagSummaryMap.put(tagName, historyTagSummary);
            }
            TagSummary historyTagSummary = historyTagSummaryMap.get(tagName);
            List<TagDaySummary> tagDaySummaryList = historyTagSummary.getTagAllDaysSummarys();
            TagDaySummary tagDaySummary = new TagDaySummary();
            tagDaySummary.setAllCount(tagRate.getAllCount());
            String date = DateUtil.format(tagRate.getDate(), "yyyy-MM-dd");
            tagDaySummary.setSummaryDate(date);
            tagDaySummary.setRate(tagRate.getRate());
            tagDaySummary.setSummaryCount(tagRate.getCount());
            tagDaySummary.setTagName(tagRate.getTagName());
            tagDaySummary.setTagId(tagRate.getTagId());
            tagDaySummaryList.add(tagDaySummary);
            historyTagSummary.setTagAllDaysSummarys(tagDaySummaryList);
            dates.add(date);
            historyTagSummaryMap.put(tagName, historyTagSummary);
        });
        if (historyTagSummaryMap.size() > 0) {
            if (StringUtils.isEmpty(sortDate)) {
                historyTagSummaryMap.keySet().stream().sorted(Comparator.naturalOrder());
                return CommonResponse.success(historyTagSummaryMap);
            }
            historyTagSummaryMap.entrySet().stream().sorted((o1, o2) -> {
                Integer o1Count = 0;
                Integer o2Count = 0;
                for (TagDaySummary tagDaySummary : o1.getValue().getTagAllDaysSummarys()) {
                    if (tagDaySummary.getSummaryDate().equals(sortDate)) {
                        o1Count = tagDaySummary.getSummaryCount();
                        break;
                    }
                }
                for (TagDaySummary tagDaySummary : o2.getValue().getTagAllDaysSummarys()) {
                    if (tagDaySummary.getSummaryDate().equals(sortDate)) {
                        o2Count = tagDaySummary.getSummaryCount();
                        break;
                    }
                }
                return sortAsc ? o1Count.compareTo(o2Count) : o2Count.compareTo(o1Count);
            });
        }
        return CommonResponse.success(historyTagSummaryMap);
    }

    @Override
    public CommonResponse<UserTagImageVO> drawUserImage(Long userId) {
        UserTagImageVO vo = new UserTagImageVO();

        UserBasicInfo userBasicInfo = new UserBasicInfo();
        String userKey = String.format(RedisConstant.USER_KEY, userId);
        UserDTO user = redisService.get(userKey, UserDTO.class);
        userBasicInfo.setUserId(userId);
        if (null != user) {
            userBasicInfo.setAddress(user.getCityName());
            userBasicInfo.setSysId(SysEnum.description(user.getSysId()));
        }
        vo.setUserBasicInfo(userBasicInfo);

        Map<String, List<TagVO>> tags = new LinkedHashMap<>();
        List<UserTagDO> userTagDOS = userTagMapper.selectList(new LambdaQueryWrapper<UserTagDO>().eq(UserTagDO::getUserId, userId).orderByAsc(UserTagDO::getTagId));
        // 1:活跃特征 2:订购类型特征 3:付费能力特征 4:偏好特征
        List<TagVO> activeTag = new ArrayList<>();
        List<TagVO> orderTypeTag = new ArrayList<>();
        List<TagVO> orderAblityTag = new ArrayList<>();
        List<TagVO> preferTag = new ArrayList<>();
        userTagDOS.forEach(userTagDO -> {
            TagVO tagVO = new TagVO();
            tagVO.setDivide(userTagDO.getDivide());
            tagVO.setName(userTagDO.getTagName());
            if (1 == userTagDO.getType()) {
                activeTag.add(tagVO);
            }
            if (2 == userTagDO.getType()) {
                orderTypeTag.add(tagVO);
            }
            if (3 == userTagDO.getType()) {
                orderAblityTag.add(tagVO);
            }
            if (4 == userTagDO.getType()) {
                preferTag.add(tagVO);
            }
        });
        tags.put("活跃特征", activeTag);
        tags.put("订购类型特征", orderTypeTag);
        tags.put("付费能力特征", orderAblityTag);
        tags.put("偏好特征", preferTag);
        vo.setTags(tags);
        return CommonResponse.success(vo);

    }

    @Override
    public CommonResponse<IPage<GroupInfoVO>> userGroups(Page page) {
        List<GroupInfoVO> list = new ArrayList<>();
        IPage<TagGroupsDO> tagGroupsDOIPage = tagGroupsMapper.selectPage(page, new LambdaQueryWrapper<TagGroupsDO>().eq(TagGroupsDO::getType, 0).orderByDesc(TagGroupsDO::getCreateTime));
        List<TagGroupsDO> tagGroupsDOS = tagGroupsDOIPage.getRecords();
        tagGroupsDOS.forEach(tagGroupsDO -> {
            GroupInfoVO vo = new GroupInfoVO();
            vo.setGroupId(tagGroupsDO.getId());
            vo.setGroupName(tagGroupsDO.getGroupName());
            vo.setGroupCount(0L);
            GroupDailySummaryDO groupDailySummary = groupDailySummaryMapper.selectOne(new LambdaQueryWrapper<GroupDailySummaryDO>().eq(GroupDailySummaryDO::getGroupId, tagGroupsDO.getId()).orderByDesc(GroupDailySummaryDO::getSummaryDate).last("limit 1"));
            if (null != groupDailySummary) {
                vo.setGroupCount(groupDailySummary.getGroupCount());
            }
            vo.setCreateTime(DateUtil.format(tagGroupsDO.getCreateTime()));
            vo.setUpdateTime(DateUtil.format(tagGroupsDO.getUpdateTime()));
            vo.setCreateUser(tagGroupsDO.getCreateUser());
            list.add(vo);
        });
        page.setRecords(list);
        return CommonResponse.success(page);
    }

    @Override
    public CommonResponse<UserGroupDetailVO> userGroupDetail(Long groupId) {
        TagGroupsDO tagGroupsDO = tagGroupsMapper.selectOne(new LambdaQueryWrapper<TagGroupsDO>().eq(TagGroupsDO::getId, groupId));
        if (null == tagGroupsDO) {
            return CommonResponse.build(ManageErrorEnum.GROUP_NOT_EXSIT);
        }
        UserGroupDetailVO vo = new UserGroupDetailVO();
        List<GroupDailySummaryDO> groupDailySummaryDOS = groupDailySummaryMapper.selectList(new LambdaQueryWrapper<GroupDailySummaryDO>().eq(GroupDailySummaryDO::getGroupId, tagGroupsDO.getId()).orderByDesc(GroupDailySummaryDO::getSummaryDate).last("limit 7"));
        if (!CollectionUtils.isEmpty(groupDailySummaryDOS)) {
            //用户分群历史趋势
            Map<String, Long> countHistory = new LinkedHashMap<>();
            //活跃特性
            Map<String, List<UserGroupTagInfo>> activeHistory = new LinkedHashMap<>();
            //付费能力
            Map<String, List<UserGroupTagInfo>> orderAbilityHistory = new LinkedHashMap<>();
            //订购类型
            Map<String, List<UserGroupTagInfo>> orderTypeHistory = new LinkedHashMap<>();
            //偏好特性
            Map<String, List<UserGroupTagInfo>> preferHistory = new LinkedHashMap<>();

            groupDailySummaryDOS.forEach(groupDailySummaryDO -> {
                String groupKey = DateUtil.format(groupDailySummaryDO.getSummaryDate(), "yyyyMMdd");
                long groupCount = groupDailySummaryDO.getGroupCount();
                if (!countHistory.containsKey(groupKey)) {
                    countHistory.put(groupKey, groupCount);
                }

                List<GroupTypeDailySummaryDO> groupTypeDailySummaryDOS = groupTypeDailySummaryMapper.selectList(new LambdaQueryWrapper<GroupTypeDailySummaryDO>().eq(GroupTypeDailySummaryDO::getGroupId, groupDailySummaryDO.getGroupId()).eq(GroupTypeDailySummaryDO::getSummaryDate, groupDailySummaryDO.getSummaryDate()));
                if (!CollectionUtils.isEmpty(groupTypeDailySummaryDOS)) {
                    groupTypeDailySummaryDOS.forEach(groupTypeDailySummaryDO -> {

                        //0:事实标签 1:活跃特征 2:订购类型特征 3:付费能力特征 4:偏好特征 5:媒资特征
                        if (1 == groupTypeDailySummaryDO.getType()) {
                            setUserGroupTagInfo(activeHistory, groupDailySummaryDO, groupTypeDailySummaryDO);
                        } else if (2 == groupTypeDailySummaryDO.getType()) {
                            setUserGroupTagInfo(orderTypeHistory, groupDailySummaryDO, groupTypeDailySummaryDO);
                        } else if (3 == groupTypeDailySummaryDO.getType()) {
                            setUserGroupTagInfo(orderAbilityHistory, groupDailySummaryDO, groupTypeDailySummaryDO);
                        } else if (4 == groupTypeDailySummaryDO.getType()) {
                            setUserGroupTagInfo(preferHistory, groupDailySummaryDO, groupTypeDailySummaryDO);
                        }
                    });
                }

            });
            //用户列表
            List<TagUserSummaryVO> userList = new ArrayList<>();
            //获取用户列表
            userList = (List<TagUserSummaryVO>) esReportService.groupMatchList(tagGroupsDO, 3);
            vo.setCountHistory(countHistory.entrySet().stream().map(entry -> {
                GroupDetail groupDetail = new GroupDetail();
                groupDetail.setCount(entry.getValue());
                groupDetail.setDate(entry.getKey());
                return groupDetail;
            }).collect(Collectors.toList()));

            vo.setActiveHistory(activeHistory.entrySet().stream().map(entry -> {
                GroupDetail groupDetail = new GroupDetail();
                groupDetail.setList(entry.getValue());
                groupDetail.setDate(entry.getKey());
                groupDetail.setTagList(setTagList(1));
                return groupDetail;
            }).collect(Collectors.toList()));
            vo.setOrderTypeHistory(orderTypeHistory.entrySet().stream().map(entry -> {
                GroupDetail groupDetail = new GroupDetail();
                groupDetail.setList(entry.getValue());
                groupDetail.setDate(entry.getKey());
                groupDetail.setTagList(setTagList(2));
                return groupDetail;
            }).collect(Collectors.toList()));
            vo.setOrderAbilityHistory(orderAbilityHistory.entrySet().stream().map(entry -> {
                GroupDetail groupDetail = new GroupDetail();
                groupDetail.setList(entry.getValue());
                groupDetail.setDate(entry.getKey());
                groupDetail.setTagList(setTagList(3));
                return groupDetail;
            }).collect(Collectors.toList()));
            vo.setPreferHistory(preferHistory.entrySet().stream().map(entry -> {
                GroupDetail groupDetail = new GroupDetail();
                groupDetail.setList(entry.getValue());
                groupDetail.setDate(entry.getKey());
                groupDetail.setTagList(setTagList(4));
                return groupDetail;
            }).collect(Collectors.toList()));
            vo.setUserList(userList);
        }
        return CommonResponse.success(vo);
    }

    public Set<String> setTagList(Integer type) {
        Set<String> tags = new LinkedHashSet<>();
        List<TagDO> tagDOS = tagMapper.selectList(new LambdaQueryWrapper<TagDO>().eq(TagDO::getType, type).eq(TagDO::getIsUsedToCount, 1));
        tagDOS.forEach(tagDO -> {
            tags.add(tagDO.getValue());
        });
        return tags;
    }

    public void setUserGroupTagInfo(Map<String, List<UserGroupTagInfo>> map, GroupDailySummaryDO groupDailySummaryDO, GroupTypeDailySummaryDO groupTypeDailySummaryDO) {
        long tagCount = groupTypeDailySummaryDO.getTagCount();
        String tagKey = DateUtil.format(groupTypeDailySummaryDO.getSummaryDate(), "yyyyMMdd");
        if (!map.containsKey(tagKey)) {
            List<UserGroupTagInfo> list = new ArrayList<>();
            map.put(tagKey, list);
        }
        List<UserGroupTagInfo> list = map.get(tagKey);
        TagDO tagDO = tagMapper.selectOne(new LambdaQueryWrapper<TagDO>().eq(TagDO::getId, groupTypeDailySummaryDO.getTagId()).eq(TagDO::getIsUsedToCount, 1));
        if (tagDO != null) {
            UserGroupTagInfo userGroupTagInfo = new UserGroupTagInfo();
            userGroupTagInfo.setCount(tagCount);
            userGroupTagInfo.setTagId(groupTypeDailySummaryDO.getTagId());
            userGroupTagInfo.setTagName(tagDO.getValue());
            list.add(userGroupTagInfo);
        }
        map.put(tagKey, list);
    }

    @Override
    public CommonResponse<IPage<GroupInfoVO>> mediaGroups(Page page) {
        List<GroupInfoVO> list = new ArrayList<>();
        IPage<TagGroupsDO> tagGroupsDOIPage = tagGroupsMapper.selectPage(page, new LambdaQueryWrapper<TagGroupsDO>().eq(TagGroupsDO::getType, 1).orderByDesc(TagGroupsDO::getCreateTime));
        List<TagGroupsDO> tagGroupsDOS = tagGroupsDOIPage.getRecords();
        tagGroupsDOS.forEach(tagGroupsDO -> {
            GroupInfoVO vo = new GroupInfoVO();
            vo.setGroupId(tagGroupsDO.getId());
            vo.setGroupCount(0L);
            GroupDailySummaryDO groupDailySummary = groupDailySummaryMapper.selectOne(new LambdaQueryWrapper<GroupDailySummaryDO>().eq(GroupDailySummaryDO::getGroupId, tagGroupsDO.getId()).orderByDesc(GroupDailySummaryDO::getSummaryDate).last("limit 1"));
            if (null != groupDailySummary) {
                vo.setGroupCount(groupDailySummary.getGroupCount());
            }
            vo.setGroupName(tagGroupsDO.getGroupName());
            vo.setCreateTime(DateUtil.format(tagGroupsDO.getCreateTime()));
            vo.setUpdateTime(DateUtil.format(tagGroupsDO.getUpdateTime()));
            vo.setCreateUser(tagGroupsDO.getCreateUser());
            list.add(vo);
        });
        page.setRecords(list);
        return CommonResponse.success(page);
    }


    @Override
    public CommonResponse saveDailyGroupStatus(Long groupId) {
        TagGroupsDO tagGroupsDO = tagGroupsMapper.selectOne(new LambdaQueryWrapper<TagGroupsDO>().eq(TagGroupsDO::getId, groupId));
        if (null == tagGroupsDO) {
            return CommonResponse.build(ManageErrorEnum.GROUP_NOT_EXSIT);
        }


        List<TagDO> list = tagMapper.selectList(new LambdaQueryWrapper<TagDO>().eq(TagDO::getIsUsedToCount, true));
        Map<Long, TagDO> map = new HashMap<>();
        list.forEach(l -> map.put(l.getId(), l));
        SearchResponse response = esReportService.groupMatchListWithSummary(tagGroupsDO, map.keySet());
        if (response == null) {
            return CommonResponse.FAIL;
        }

        long total = response.getHits().totalHits;
        GroupDailySummaryDO groupDailySummaryDO = new GroupDailySummaryDO();
        groupDailySummaryDO.setGroupId(tagGroupsDO.getId());
        groupDailySummaryDO.setGroupName(tagGroupsDO.getGroupName());
        groupDailySummaryDO.setGroupCount(total);
        groupDailySummaryDO.setSummaryDate(LocalDate.now());
        groupDailySummaryMapper.delete(new LambdaQueryWrapper<GroupDailySummaryDO>().eq(GroupDailySummaryDO::getGroupId, groupDailySummaryDO.getGroupId()).eq(GroupDailySummaryDO::getSummaryDate, groupDailySummaryDO.getSummaryDate()));
        groupDailySummaryMapper.insert(groupDailySummaryDO);


        Filter filter = response.getAggregations().get("filter");
        Terms terms = filter.getAggregations().get("terms");


        terms.getBuckets().forEach(t -> {
            long tagId = Long.parseLong(t.getKeyAsString());
            GroupTypeDailySummaryDO gtd = new GroupTypeDailySummaryDO();
            gtd.setTagCount(t.getDocCount());
            gtd.setGroupId(tagGroupsDO.getId());
            gtd.setGroupName(tagGroupsDO.getGroupName());
            gtd.setTagId(tagId);
            gtd.setSummaryDate(LocalDate.now());
            //根据tagid获取特征
            TagDO tagDO = tagMapper.selectOne(new LambdaQueryWrapper<TagDO>().eq(TagDO::getId, tagId));
            if (null != tagDO) {
                gtd.setType(tagDO.getType());
            }
            groupTypeDailySummaryMapper.delete(new LambdaQueryWrapper<GroupTypeDailySummaryDO>().eq(GroupTypeDailySummaryDO::getGroupId,groupId).eq(GroupTypeDailySummaryDO::getTagId, tagId).eq(GroupTypeDailySummaryDO::getSummaryDate, gtd.getSummaryDate()));
            groupTypeDailySummaryMapper.insert(gtd);
        });

        return CommonResponse.SUCCESS;
    }

    @Override
    public CommonResponse<IPage<TagUserSummaryVO>> tagUserList(String tagIds, Page page) {
        List<TagUserSummaryVO> list = new ArrayList<>();
        String[] tags = tagIds.split(",");
        if (tags.length <= 0) {
            return CommonResponse.build(ManageErrorEnum.NON_TAG_SELECT);
        }
        LambdaQueryWrapper<UserTagDO> wrapper = new LambdaQueryWrapper();
        wrapper.in(UserTagDO::getTagId, tags);
        IPage<UserTagDO> userTagDOIPage = userTagMapper.selectPage(page, wrapper);
        List<UserTagDO> userTagDOList = userTagDOIPage.getRecords();
        userTagDOList.forEach(userTagDO -> {
            TagUserSummaryVO vo = new TagUserSummaryVO();
            vo.setUserId(userTagDO.getUserId().longValue());
            String userKey = String.format(RedisConstant.USER_KEY, userTagDO.getUserId());
            UserDTO user = redisService.get(userKey, UserDTO.class);
            if (null != user) {
                vo.setAddress(user.getCityName());
                vo.setSysId(SysEnum.description(user.getSysId()));
                vo.setActiveTime(DateUtil.format(user.getActiveTime(), "yyyyMMdd"));
            }
            list.add(vo);
        });
        page.setRecords(list);
        return CommonResponse.success(page);
    }

    @Override
    public CommonResponse groupDelete(Long groupId) {
        tagGroupsMapper.deleteById(groupId);
        return CommonResponse.SUCCESS;
    }

    @Override
    public CommonResponse<TagConditionVO> groupSelect(Long groupId) {
        TagGroupsDO tagGroupsDO = tagGroupsMapper.selectOne(new LambdaQueryWrapper<TagGroupsDO>().eq(TagGroupsDO::getId, groupId));
        if (null == tagGroupsDO) {
            return CommonResponse.build(ManageErrorEnum.GROUP_NOT_EXSIT);
        }
        String queryCondition = tagGroupsDO.getQuery();
        if (StringUtils.isEmpty(queryCondition)) {
            return CommonResponse.build(ManageErrorEnum.GROUP_CONDITION_NOT_EXSIT);
        }
        TagConditionDTO dto = JSONObject.parseObject(queryCondition, TagConditionDTO.class);
        TagConditionVO vo = JSONObject.parseObject(JSONObject.toJSONString(dto), TagConditionVO.class);
        vo.setGroupId(tagGroupsDO.getId());
        List<TagCondition> conditions = vo.getConditions();
        if (!CollectionUtils.isEmpty(conditions)) {
            conditions.forEach(tagCondition -> {
                Long tagId = tagCondition.getTagId();
                TagDO tagDO = tagMapper.selectOne(new LambdaQueryWrapper<TagDO>().eq(TagDO::getId, tagId).last("limit 1"));
                if (null != tagDO) {
                    CommonResponse<List<TagVO>> commonResponse = selectConditionDivideResult(tagDO.getDivide());
                    if (commonResponse._isOk()) {
                        tagCondition.setConditionList(commonResponse.getData());
                    }
                }
            });
        }
        vo.setConditions(conditions);
        return CommonResponse.success(vo);
    }

    @Override
    public CommonResponse groupUpdate(Long userId, TagConditionDTO dto) {
        TagGroupsDO tagGroupsDO = tagGroupsMapper.selectOne(new LambdaQueryWrapper<TagGroupsDO>().eq(TagGroupsDO::getId, dto.getGroupId()));
        if (null == tagGroupsDO) {
            return CommonResponse.build(ManageErrorEnum.GROUP_NOT_EXSIT);
        }
        tagGroupsDO.setGroupName(dto.getGroupName());
        tagGroupsDO.setType(dto.getGroupType());
        tagGroupsDO.setQuery(JSONObject.toJSONString(dto));
        AccountDO accountDO = accountMapper.selectById(userId);
        if (null != accountDO) {
            tagGroupsDO.setCreateUser(accountDO.getName());
        }
        tagGroupsMapper.updateById(tagGroupsDO);
        //获取数量
        saveDailyGroupStatus(tagGroupsDO.getId());
        return CommonResponse.SUCCESS;
    }
}