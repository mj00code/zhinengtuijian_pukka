package com.ubo.iptv.job.handler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.ubo.iptv.job.mapper.MyUserTagMapper;
import com.ubo.iptv.job.service.TagMarkService;
import com.ubo.iptv.mybatis.gsgd.entity.GSGsydUserDO;
import com.ubo.iptv.mybatis.gsgd.mapper.GSGsydUserMapper;
import com.ubo.iptv.mybatis.recommend.entity.TagDO;
import com.ubo.iptv.mybatis.recommend.entity.UserTagDO;
import com.ubo.iptv.mybatis.recommend.mapper.TagMapper;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.util.ShardingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: xuning
 * @Date: 2020-11-06
 */
@Service
@JobHandler(value = "userTagMarkJobHandler")
@Slf4j
public class UserTagMarkJobHandler extends IJobHandler {

    @Resource
    private GSGsydUserMapper gsGsydUserMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private TagMarkService[] tagServices;
    @Autowired
    private MyUserTagMapper myUserTagMapper;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        long begin = System.currentTimeMillis();
        // 分片参数
        ShardingUtil.ShardingVO shardingVO = ShardingUtil.getShardingVo();
        XxlJobLogger.log("分片参数: 当前分片序号={}, 总分片数={}", shardingVO.getIndex(), shardingVO.getTotal());

        markUser(shardingVO);
        // 统计标签内人数
        tagDailySummary();
        XxlJobLogger.log("任务完成：共耗时 {}秒", (System.currentTimeMillis() - begin) / 1000);
        return SUCCESS;
    }

    private void tagDailySummary() {
        try {
            // 插入付费分布相关user tag
            int delRes = myUserTagMapper.deleteTagDailySummary();
            int res = myUserTagMapper.insertTagDailySummary();
            XxlJobLogger.log("付费范围分布：共插入{}条", res);
        } catch (Exception e) {
            XxlJobLogger.log(e);
        }
    }

    /**
     * 获取所有标签
     *
     * @return
     */
    private Map<String, List<TagDO>> tagsMap() {
        List<TagDO> list = tagMapper.selectList(new LambdaQueryWrapper<TagDO>().eq(TagDO::getClassify, 0));
        Map<String, List<TagDO>> map = list.stream().collect(Collectors.groupingBy(TagDO::getDivide));
        return map;
    }

    /**
     * 标记用户
     *
     * @param shardingVO
     */
    private void markUser(ShardingUtil.ShardingVO shardingVO) {
        int index = 0;
        int size = 2000;
        while (true) {
            // 利用页数分片
            if (shardingVO.getTotal() > 1 && (index + 1) % shardingVO.getTotal() != shardingVO.getIndex()) {
                index++;
                continue;
            }
            QueryWrapper<GSGsydUserDO> wrapper = new QueryWrapper<>();
            // 筛选条件
            wrapper.eq("`Status`", 5);
            wrapper.in("t_gsyd_user.platform", 1, 2);
            // 分页
            wrapper.last("LIMIT " + index * size + "," + size);
            List<GSGsydUserDO> list = gsGsydUserMapper.selectList(wrapper);
            if (list.size() > 0) {
                Map<String, List<TagDO>> tagsMap = tagsMap();
                List<UserTagDO> batchUserTags = new ArrayList<>();
                //处理单个用户可以计算的标签
                list.forEach(u -> {
                    List<TagDO> userTags = new ArrayList<>();
                    for (int i = 0; i < tagServices.length; i++) {
                        try {
                            tagServices[i].mark(tagsMap, u, userTags);
                        } catch (Exception e) {
                            XxlJobLogger.log("打标签失败:" + tagServices[i].getClass().getName());
                        }

                    }
                    if (userTags.size() > 0) {
                        userTags.forEach(i -> {
                            UserTagDO ut = new UserTagDO();
                            ut.setUserId(u.getId());
                            ut.setClassify(i.getClassify());
                            ut.setType(i.getType());
                            ut.setDivide(i.getDivide());
                            ut.setTagId(i.getId());
                            ut.setTagName(i.getName());
                            batchUserTags.add(ut);
                        });
                    }
                });
                //删除老标签
//                long startId = list.get(0).getId();
//                long endId = list.get(list.size() - 1).getId();
//
//                myUserTagMapper.deleteRangeIds(startId, endId);

                for (List<UserTagDO> l : Lists.partition(batchUserTags, 1000)) {
                    myUserTagMapper.bacthInsert(l);
                }

            }
            // 数量不足退出
            if (list.size() < size) {
                break;
            }
            XxlJobLogger.log("当前进度 : index={}, size={}", index, size);
            index++;
        }
    }
}
