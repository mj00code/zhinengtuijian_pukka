package com.ubo.iptv.job.handler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ubo.iptv.job.mapper.MyUserTagMapper;
import com.ubo.iptv.manage.api.TagApi;
import com.ubo.iptv.mybatis.recommend.entity.TagGroupsDO;
import com.ubo.iptv.mybatis.recommend.mapper.TagGroupsMapper;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.util.ShardingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: xuning
 * @Date: 2020-11-06
 */
@Service
@JobHandler(value = "tagSummaryJobHandler")
@Slf4j
public class TagSummaryJobHandler extends IJobHandler {
    @Resource
    private TagGroupsMapper tagGroupsMapper;
    @Resource
    private MyUserTagMapper myUserTagMapper;
    @Autowired
    private TagApi tagApi;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        long begin = System.currentTimeMillis();
        // 分片参数
        ShardingUtil.ShardingVO shardingVO = ShardingUtil.getShardingVo();
        XxlJobLogger.log("分片参数: 当前分片序号={}, 总分片数={}", shardingVO.getIndex(), shardingVO.getTotal());


        List<TagGroupsDO> list = tagGroupsMapper.selectList(new LambdaQueryWrapper<>());
        for (int i = 0; i < list.size(); i++) {
            tagApi.saveDailyGroupStatus(list.get(i).getId());
        }


        XxlJobLogger.log("任务完成：共耗时 {}秒", (System.currentTimeMillis() - begin) / 1000);
        return SUCCESS;
    }
}
