package com.ubo.iptv.job.handler;

import com.alibaba.fastjson.JSONArray;
import com.ubo.iptv.core.constant.RedisConstant;
import com.ubo.iptv.core.util.DateUtil;
import com.ubo.iptv.entity.gdgd.recommend.MediaKindTopDTO;
import com.ubo.iptv.entity.gdgd.recommend.MediaTypeTopDTO;
import com.ubo.iptv.job.service.RedisService;
import com.ubo.iptv.mybatis.recommend.mapper.UserDailySummaryMapper;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.util.ShardingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;

/**
 * @Author: xuning
 * @Date: 2020-11-06
 */
@Service
@JobHandler(value = "userFavourJobHandler")
@Slf4j
public class UserFavourJobHandler extends IJobHandler {

    @Autowired
    private RedisService redisService;
    @Resource
    private UserDailySummaryMapper userDailySummaryMapper;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        long begin = System.currentTimeMillis();
        // 分片参数
        ShardingUtil.ShardingVO shardingVO = ShardingUtil.getShardingVo();
        XxlJobLogger.log("分片参数: 当前分片序号={}, 总分片数={}", shardingVO.getIndex(), shardingVO.getTotal());

        cacheFavour(shardingVO);

        XxlJobLogger.log("任务完成：共耗时 {}秒", (System.currentTimeMillis() - begin) / 1000);
        return SUCCESS;
    }

    /**
     * 缓存用户偏好
     *
     * @param shardingVO
     */
    private void cacheFavour(ShardingUtil.ShardingVO shardingVO) {
        int index = 0;
        int size = 2000;
        while (true) {
            // 利用页数分片
            if (shardingVO.getTotal() > 1 && (index + 1) % shardingVO.getTotal() != shardingVO.getIndex()) {
                index++;
                continue;
            }
            String yesterday = DateUtil.format(LocalDate.now().minusDays(1L), "yyyyMMdd");
            Set<String> set = redisService.getZSet(String.format(RedisConstant.DAY_ACTIVE_USER_IDS, yesterday), index, size);

            Map<String, String> map = new HashMap<>(set.size());
            set.forEach(s -> {
                Integer userId = Integer.valueOf(s);
                // 类型偏好
                List<MediaTypeTopDTO> mediaTypeList = new ArrayList<>();
                userDailySummaryMapper.selectMediaTypeTop(userId, 10)
                        .forEach(m -> {
                            // 题材偏好
                            List<MediaKindTopDTO> mediaKindList = new ArrayList<>();
                            userDailySummaryMapper.selectMediaKindTop(userId, m.getMediaType(), 3)
                                    .forEach(n -> {
                                        MediaKindTopDTO mediaKind = new MediaKindTopDTO();
                                        mediaKind.setMediaKind(n.getMediaKindId());
                                        mediaKind.setScore(n.getScore());
                                        mediaKindList.add(0, mediaKind);
                                    });
                            MediaTypeTopDTO mediaType = new MediaTypeTopDTO();
                            mediaType.setMediaType(m.getMediaType());
                            mediaType.setScore(m.getScore());
                            mediaType.setMediaKindList(mediaKindList);
                            mediaTypeList.add(0, mediaType);
                        });

                // 组装redisMap集合
                map.put(String.format(RedisConstant.MEDIA_TYPE_TOP5, userId), JSONArray.toJSONString(mediaTypeList));
            });
            // 同步至redis
            boolean redisResponse = redisService.set(map, 864000L);
            if (!redisResponse) {
                log.error("Redis set error: index={}, size={}", index, size);
            }

            // 数量不足退出
            if (set.size() < size) {
                break;
            }
            XxlJobLogger.log("当前进度 : index={}, size={}", index, size);
            index++;
        }
    }
}
