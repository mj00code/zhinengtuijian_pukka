package com.ubo.iptv.job.handler;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.ubo.iptv.core.constant.ElasticSearchConstant;
import com.ubo.iptv.core.constant.RedisConstant;
import com.ubo.iptv.core.util.DateUtil;
import com.ubo.iptv.entity.gzdp.UserDTO;
import com.ubo.iptv.job.service.EsService;
import com.ubo.iptv.mybatis.gzdp.entity.TpUserDO;
import com.ubo.iptv.mybatis.gzdp.mapper.TpUserMapper;
import com.ubo.iptv.mybatis.recommend.entity.UserTagDO;
import com.ubo.iptv.mybatis.recommend.mapper.UserTagMapper;
import com.ubo.iptv.redis.RedisService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.util.ShardingUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: xuning
 * @Date: 2020-11-06
 */
@Service
@JobHandler(value = "userSyncJobHandler")
@Slf4j
public class UserSyncJobHandler extends IJobHandler {

    @Autowired
    private RedisService redisService;
    @Autowired
    private EsService esService;
    @Autowired
    private Gson gson;
    @Resource
    private TpUserMapper tpUserMapper;
    @Resource
    private UserTagMapper userTagMapper;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        long begin = System.currentTimeMillis();
        // ????????????
        ShardingUtil.ShardingVO shardingVO = ShardingUtil.getShardingVo();
        XxlJobLogger.log("????????????: ??????????????????={}, ????????????={}", shardingVO.getIndex(), shardingVO.getTotal());

        // ????????????
        syncUser(shardingVO);
        // ??????3?????????????????????
        //cleanUser(3);

        XxlJobLogger.log("???????????????????????? {}???", (System.currentTimeMillis() - begin) / 1000);
        return SUCCESS;
    }

    /**
     * ????????????
     *
     * @param shardingVO
     */
    private void syncUser(ShardingUtil.ShardingVO shardingVO) {
        int index = 0;
        int size = 2000;
        while (true) {
            // ??????????????????
            if (shardingVO.getTotal() > 1 && (index + 1) % shardingVO.getTotal() != shardingVO.getIndex()) {
                index++;
                continue;
            }
            QueryWrapper<TpUserDO> wrapper = new QueryWrapper<>();
            // ????????????
            wrapper.eq("`status`", 5);

            // ??????
            wrapper.last("LIMIT " + index * size + "," + size);
            List<TpUserDO> list = tpUserMapper.selectListWithCity(wrapper);

            Map<String, String> hash = new HashMap<>(list.size());
            Map<String, String> user = new HashMap<>(list.size());
            BulkRequest request = new BulkRequest();
            list.forEach(i -> {
                // ???????????????user??????
                if (StringUtils.isEmpty(i.getWxOpenid())) {
                    return;
                }
                UserDTO dto = new UserDTO(i);
                dto.setCreateTime(DateUtil.format(LocalDateTime.now()));
                // ????????????
                String key = String.format(RedisConstant.USER_KEY, dto.getUserId());
                // ????????????
                List<UserTagDO> tags = userTagMapper.selectList(new LambdaQueryWrapper<UserTagDO>().eq(UserTagDO::getUserId, dto.getUserId()));
                if (!tags.isEmpty()) {
                    dto.setTagId(tags.stream().map(UserTagDO::getTagId).toArray(Long[]::new));
                    dto.setTag(tags.stream().map(UserTagDO::getTagName).toArray(String[]::new));
                }

                // ??????code2id??????
                hash.put(dto.getWxOpenId(), key);
                // ??????redisMap??????
                user.put(key, JSONObject.toJSONString(dto));
                // ??????BulkRequest??????
                IndexRequest indexRequest = new IndexRequest(ElasticSearchConstant.user_index, ElasticSearchConstant.type_default, key);
                indexRequest.source(gson.toJson(dto), XContentType.JSON);
                request.add(indexRequest);
            });
            // ?????????redis
            boolean redisResponse = redisService.setHash(RedisConstant.HASH_USER_CODE2ID_KEY, hash, 864000L);
            if (!redisResponse) {
                log.error("Redis setHash error: index={}, size={}", index, size);
            }
            redisResponse = redisService.set(user, 864000L);
            if (!redisResponse) {
                log.error("Redis set error: index={}, size={}", index, size);
            }
            //  ?????????es
            boolean esResponse = esService.bulk(request);
            if (!esResponse) {
                log.error("ES bulk error: index={}, size={}", index, size);
            }

            // ??????????????????
            if (list.size() < size) {
                break;
            }
            XxlJobLogger.log("???????????? : index={}, size={}", index, size);
            index++;
        }
    }

    /**
     * ??????x?????????????????????
     *
     * @param days
     */
    private void cleanUser(long days) {
        DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(ElasticSearchConstant.user_index);
        deleteByQueryRequest.setConflicts("proceed");
        deleteByQueryRequest.setQuery(QueryBuilders.rangeQuery("createTime").lte(DateUtil.format(LocalDate.now().minusDays(days), "yyyy-MM-dd")));
        try {
            BulkByScrollResponse response = esService.getClient().deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);
            XxlJobLogger.log("?????????????????? : days={}, deleted={}", days, response.getDeleted());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
