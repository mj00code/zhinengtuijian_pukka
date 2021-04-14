package com.ubo.iptv.job.handler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ubo.iptv.job.mapper.MyUserTagMapper;
import com.ubo.iptv.mybatis.gsgd.entity.GSGsydOrderDO;
import com.ubo.iptv.mybatis.gsgd.mapper.GSGsydOrderMapper;
import com.ubo.iptv.mybatis.gsgd.mapper.GSGsydUserMapper;
import com.ubo.iptv.mybatis.recommend.entity.TagDO;
import com.ubo.iptv.mybatis.recommend.mapper.TagMapper;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.util.ShardingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: xuning
 * @Date: 2020-11-06
 */
@Service
@JobHandler(value = "userTagJobHandler")
@Slf4j
public class UserTagJobHandler extends IJobHandler {

    @Resource
    private GSGsydUserMapper gsGsydUserMapper;
    @Resource
    private GSGsydOrderMapper gsGsydOrderMapper;
    @Resource
    private TagMapper tagMapper;
    @Resource
    private MyUserTagMapper myUserTagMapper;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        long begin = System.currentTimeMillis();
        // 分片参数
        ShardingUtil.ShardingVO shardingVO = ShardingUtil.getShardingVo();
        XxlJobLogger.log("分片参数: 当前分片序号={}, 总分片数={}", shardingVO.getIndex(), shardingVO.getTotal());

        // 用户标签
        List<TagDO> list = tagMapper.selectList(new LambdaQueryWrapper<TagDO>().eq(TagDO::getClassify, 0));
        Map<String, List<TagDO>> map = list.stream().collect(Collectors.groupingBy(TagDO::getDivide));
        // 清空t_user_tag
        myUserTagMapper.truncate();
        // 最近一次订购成功距当前时间
        lastOrderDays(map.get("最近一次订购成功距当前时间"));
        // 订购次数
        orderCount(map.get("订购次数"));
        // 付费能力
        payAbility(map.get("付费能力"));
        // 付费范围分布
        payDistribution(map.get("付费范围分布"));

        XxlJobLogger.log("任务完成：共耗时 {}秒", (System.currentTimeMillis() - begin) / 1000);
        return SUCCESS;
    }

    /**
     * 最近一次订购成功距当前时间
     *
     * @param list
     */
    private void lastOrderDays(List<TagDO> list) {
        try {
            // 插入最近一次订购成功距当前时间相关user tag
            int res = myUserTagMapper.insertLastOrderTag(list);
            XxlJobLogger.log("最近一次订购成功距当前时间：共插入{}条", res);
        } catch (Exception e) {
            XxlJobLogger.log(e);
        }
    }

    /**
     * 订购次数
     *
     * @param list
     */
    private void orderCount(List<TagDO> list) {
        try {
            // 插入订购次数相关user tag
            int res = myUserTagMapper.insertOrderCountTag(list);
            XxlJobLogger.log("订购次数：共插入{}条", res);
        } catch (Exception e) {
            XxlJobLogger.log(e);
        }
    }

    /**
     * 付费能力
     *
     * @param list
     */
    private void payAbility(List<TagDO> list) {
        try {
            // 付费金额排行
            QueryWrapper<GSGsydOrderDO> rankWrapper = new QueryWrapper<>();
            rankWrapper.select("DISTINCT SUM(Price) AS TotalPrice");
            rankWrapper.eq("`Status`", 1);
            rankWrapper.gt("Price", 0);
            rankWrapper.apply("CreateTime > DATE_SUB(NOW(),INTERVAL 30 DAY)");
            rankWrapper.groupBy("UserID");
            rankWrapper.orderByDesc("TotalPrice");
            List<Object> payRank = gsGsydOrderMapper.selectObjs(rankWrapper);
            if (!payRank.isEmpty()) {
                // 按比例计算边界
                list.forEach(tagDO -> {
                    BigDecimal from = tagDO.getRangeFrom();
                    BigDecimal to = tagDO.getRangeTo();
                    if (from != null && to != null) {
                        int fromIndex = new BigDecimal(payRank.size()).multiply(from).intValue();
                        Double fromPrice = (Double) payRank.get(fromIndex);
                        tagDO.setRangeTo(new BigDecimal(fromPrice));

                        int toIndex = new BigDecimal(payRank.size()).multiply(to).intValue() - 1;
                        Double toPrice = (Double) payRank.get(toIndex);
                        tagDO.setRangeFrom(new BigDecimal(toPrice));
                    }
                });
            }
            // 插入付费能力相关user tag
            int res = myUserTagMapper.insertPayTag(list);
            XxlJobLogger.log("付费能力：共插入{}条", res);
        } catch (Exception e) {
            XxlJobLogger.log(e);
        }
    }

    /**
     * 付费范围分布
     *
     * @param list
     */
    private void payDistribution(List<TagDO> list) {
        try {
            // 插入付费分布相关user tag
            int res = myUserTagMapper.insertPayTag(list);
            XxlJobLogger.log("付费范围分布：共插入{}条", res);
        } catch (Exception e) {
            XxlJobLogger.log(e);
        }
    }

}
