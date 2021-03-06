package com.ubo.iptv.job.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.ubo.iptv.core.constant.ElasticSearchConstant;
import com.ubo.iptv.core.constant.RedisConstant;
import com.ubo.iptv.core.enums.MediaSortEnum;
import com.ubo.iptv.core.enums.MediaTypeEnum;
import com.ubo.iptv.core.enums.SysEnum;
import com.ubo.iptv.core.util.DateUtil;
import com.ubo.iptv.entity.gdgd.MediaDTO;
import com.ubo.iptv.job.service.EsService;
import com.ubo.iptv.mybatis.gsgd.mapper.*;
import com.ubo.iptv.mybatis.gzdp.entity.TContentCategoryDO;
import com.ubo.iptv.mybatis.gzdp.entity.TContentDO;
import com.ubo.iptv.mybatis.gzdp.mapper.TChannelMapper;
import com.ubo.iptv.mybatis.gzdp.mapper.TContentCategoryMapper;
import com.ubo.iptv.mybatis.gzdp.mapper.TContentMapper;
import com.ubo.iptv.mybatis.gzdp.mapper.TpUserMapper;
import com.ubo.iptv.mybatis.recommend.entity.StrategyMediaSortDO;
import com.ubo.iptv.mybatis.recommend.mapper.StrategyMapper;
import com.ubo.iptv.mybatis.recommend.mapper.StrategyMediaSortMapper;
import com.ubo.iptv.redis.RedisService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.util.ShardingUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.max.ParsedMax;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: xuning
 * @Date: 2020-11-02
 * @description ????????????????????????
 */
@Service
@JobHandler(value = "mediaSyncJobHandler")
@Slf4j
public class MediaSyncJobHandler extends IJobHandler {

    private static BigDecimal maxPlayCount = BigDecimal.ZERO;
    private static BigDecimal maxSearchCount = BigDecimal.ZERO;
    private static Map<Long, List<StrategyMediaSortDO>> sortStrategyMap = new HashMap<>();

    @Value("${post.url.prefix}")
    private String prefixUrl;

    @Autowired
    private RedisService redisService;
    @Autowired
    private EsService esService;
    @Autowired
    private Gson gson;
    @Resource
    private GSContentinfoMapper gsContentinfoMapper;
    @Resource
    private GSContentPicUrlMapper gsContentPicUrlMapper;
    @Resource
    private GSChannelPirurlMapper gsChannelPirurlMapper;
    @Resource
    private GSContentCatalogMapper gsContentCatalogMapper;
    @Resource
    private GSServiceContentMapper gsServiceContentMapper;
    @Resource
    private GSServiceMapper gsServiceMapper;
    @Resource
    private StrategyMapper strategyMapper;
    @Resource
    private StrategyMediaSortMapper strategyMediaSortMapper;

    @Resource
    private TContentMapper contentMapper;

    @Resource
    private TContentCategoryMapper categoryMapper;

    @Resource
    private TpUserMapper userMapper;

    @Resource
    private TChannelMapper channelMapper;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        long begin = System.currentTimeMillis();
        // ????????????
        ShardingUtil.ShardingVO shardingVO = ShardingUtil.getShardingVo();
        XxlJobLogger.log("????????????: ??????????????????={}, ????????????={}", shardingVO.getIndex(), shardingVO.getTotal());
//        int s = 1;
//        while (s < 11) {
//            QueryWrapper<TContentDO> contentWrapper = new QueryWrapper<>();
//            List<TContentDO> contentList = contentMapper.selectList(contentWrapper);
//            QueryWrapper<TpUserDO> userQueryMapper = new QueryWrapper<>();
//            List<TpUserDO> userList = userMapper.selectList(userQueryMapper);
//            int j = 0;
//            int sequence = s * 1000;
//            FileOutputStream out = new FileOutputStream(new File("D:\\test" + s + ".txt"));
//            try {
//                for (int i = 0; i < contentList.size(); i++) {
//                    LogBody logBody = new LogBody();
//                    logBody.setLog_time(String.valueOf(Instant.now().getEpochSecond()));
//                    //???5??????????????????
//                    if (i % 5 == 0) {
//                        TpUserDO user = userList.get(j);
//                        if (StringUtils.isNotEmpty(user.getWxOpenid())) {
//                            logBody.setUser_id(user.getWxOpenid());
//                        }
//                        logBody.setEvent_type("RECOMMEND_LOCATION_CLICK");
//                        logBody.setPage_url("pages/index/index/20190902151528279");
//                        j++;
//                    } else {
//                        TpUserDO user = userList.get(j);
//                        if (StringUtils.isNotEmpty(user.getWxOpenid())) {
//                            logBody.setUser_id(user.getWxOpenid());
//                        }
//                        logBody.setEvent_type("RECOMMEND_LOCATION_CLICK");
//                        logBody.setPage_url("pages/index/index/20200404193623741");
//                    }
//
//                    logBody.setUser_type("user_type");
//                    logBody.setNettype("4G");
//                    logBody.setSys_id("CM");
//                    logBody.setOstype("Android");
//                    logBody.setSource_channel("GJTV");
//                    logBody.setSeqid(String.valueOf(sequence));
//                    logBody.setMedia_code(contentList.get(i).getContentId());
//                    logBody.setArea_num("1-1");
//                    logBody.setContent_type(contentList.get(i).getContentType());
//
//                    String json = JSON.toJSONString(logBody);
//                    out.write(json.getBytes());
//                    out.write(",".getBytes());
//                    out.write(System.getProperty("line.separator").getBytes());
//                    sequence++;
//                    out.flush();
//                    if (sequence == 753) {
//                        System.out.println(1);
//                    }
//                }
//            } catch (Exception e) {
//            } finally {
//                out.close();
//            }
//            s++;
//        }

        // ????????????
        strategySortMap();
        // ???????????????????????????????????????
        maxCount();
        // ????????????
        syncMedia(shardingVO);
        // ??????3?????????????????????
        //cleanMedia(3);

        XxlJobLogger.log("???????????????????????? {}???", (System.currentTimeMillis() - begin) / 1000);
        return SUCCESS;
    }

    /**
     * ????????????
     *
     * @param shardingVO
     */
    private void syncMedia(ShardingUtil.ShardingVO shardingVO) {
        int index = 0;
        int size = 100;
        while (true) {
            //??????????????????
            if (shardingVO.getTotal() > 1 && (index + 1) % shardingVO.getTotal() != shardingVO.getIndex()) {
                index++;
                continue;
            }
            QueryWrapper<TContentCategoryDO> categoryWrapper = new QueryWrapper<>();
            // ????????????
            categoryWrapper.eq("t_category.parentcode", "1100");
            categoryWrapper.eq("t_category.status", "VALID");
            categoryWrapper.eq("t_category.spid", 1);
            categoryWrapper.isNotNull("t_content_category.category_code");
            categoryWrapper.orderByAsc("t_category.id");
            // ??????
            categoryWrapper.last("LIMIT " + index * size + "," + size);
            //????????????
            List<TContentCategoryDO> list = categoryMapper.selectContentList(categoryWrapper);
            QueryWrapper<TContentCategoryDO> categoryWrapper2 = new QueryWrapper<>();
            // ????????????
            categoryWrapper2.eq("t_category.id", 9663);
            categoryWrapper2.eq("t_category.status", "VALID");
            categoryWrapper2.eq("t_category.spid", 1);
            categoryWrapper2.isNotNull("t_content_category.category_code");
            categoryWrapper2.orderByAsc("t_category.id");
            //????????????
            List<TContentCategoryDO> list2 = categoryMapper.selectContentList(categoryWrapper2);
            list.addAll(list2);
            //????????????
            Map<String, String> hash = new HashMap<>(list.size());
            Map<String, String> mediaMap = new HashMap<>(list.size());
            BulkRequest request = new BulkRequest();
            list.forEach(contentCategory -> {
                QueryWrapper<TContentDO> contentWrapper = new QueryWrapper<>();
                contentWrapper.eq("t1.status", "VALID");
                //???????????????
                //contentWrapper.isNotNull("t_content.poster_img");
                contentWrapper.eq("t1.online_status", "ONLINE");
                contentWrapper.eq("t1.spid", 1);
                //contentWrapper.eq("t_content.content_id", contentCategory.getContentCode());
                //contentWrapper.eq("t_content.content_type", contentCategory.getContentType());
                contentWrapper.and(wrapper -> wrapper.eq("t1.content_id", contentCategory.getContentCode()).or().eq("t1.content_series_code", contentCategory.getContentCode()));
                List<TContentDO> contentList = contentMapper.selectListWithSummary(contentWrapper);
                contentList.forEach(content -> {
                    content.setCategoryId(contentCategory.getId());
                    content.setCategoryName(contentCategory.getCategoryName());
                    MediaTypeEnum typeEnum = MediaTypeEnum.getMediaType(contentCategory.getCategoryId());
                    content.setMediaTypeId(typeEnum.intValue());
                    content.setMediaType(contentCategory.getContentType());
                    MediaDTO dto = new MediaDTO(content);
                    if (dto.getMediaCode() == null) {
                        XxlJobLogger.log("??????mediaCode???mediaId={}", dto.getMediaId());
                        return;
                    }
                    dto.setCreateTime(DateUtil.format(LocalDateTime.now()));
                    dto.setSysId(SysEnum.getSysIdBySpId(dto.getSpId()));
                    // ??????????????????
                    dto.setMediaType(typeEnum.intValue());
                    dto.setMediaTypeName(typeEnum.description());
                    dto.setPosterUrl(prefixUrl + content.getPosterImg());
                    // ???????????????
                    if ("EPISODE".equalsIgnoreCase(dto.getMediaTypeId())) {
                        dto.setParentMediaId(content.getContentSeriesId());
                        dto.setPublishStatus(0);
                    }
                    //?????????????????????
                    if (content.getPosterImg() == null || content.getPosterImg() == "" || content.getPosterImg().length() == 0) {
                        dto.setPublishStatus(0);
                    }
                    // ??????????????????
                    JSONObject json = strategyScore(dto);
                    // ????????????
                    String key = String.format(RedisConstant.MEDIA_KEY, dto.getMediaId());
                    // ??????code2id??????
                    hash.put(dto.getMediaCode(), key);
                    // ??????redisMap??????
                    mediaMap.put(key, json.toString());
                    // ????????????mediaType_free;
                    json.put("mediaTypeFree", dto.getMediaType() + "_" + dto.getFree());
                    // ??????BulkRequest??????
                    IndexRequest indexRequest = new IndexRequest(ElasticSearchConstant.media_index, ElasticSearchConstant.type_default, key);
                    indexRequest.source(gson.toJson(json), XContentType.JSON);
                    request.add(indexRequest);
                });
            });
            //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
//            //????????????
//            QueryWrapper<TContentCategoryDO> wrapper2 = new QueryWrapper<>();
//            // ????????????
//            wrapper2.eq("t_category.parentid", 4);
//            wrapper2.eq("t_category.status", "VALID");
//            wrapper2.eq("t_category.spid", 1);
//            wrapper2.isNotNull("t_content_category.category_code");
//            // ??????
//            //wrapper2.last("LIMIT " + index * size + "," + size);
//            //????????????
//            List<TContentCategoryDO> categoryList = categoryMapper.selectContentList(wrapper2);
//            categoryList.forEach(media -> {
//                QueryWrapper<TChannelDO> channelWrapper = new QueryWrapper<>();
//                channelWrapper.eq("t_channel.status", 1);
//                //???????????????
//                channelWrapper.isNotNull("t_channel.logo");
//                channelWrapper.eq("t_channel.online_status", "VALID");
//                channelWrapper.eq("t_channel.spid", 1);
//                channelWrapper.eq("t_channel.channel_id", media.getContentCode());
//                channelWrapper.eq("t_content_category.content_type", media.getContentType());
//                List<TChannelDO> channelList = channelMapper.selectChannelListWithSummary(channelWrapper);
//                channelList.forEach(channel -> {
//                    channel.setCategoryId(media.getCategoryId());
//                    channel.setCategoryName(media.getCategoryName());
//                    channel.setContentType(media.getContentType());
//                    MediaTypeEnum typeEnum = MediaTypeEnum.getMediaType(media.getCategoryId());
//                    channel.setMediaTypeId(typeEnum.intValue());
//                    MediaDTO dto = new MediaDTO(channel);
//                    if (dto.getMediaCode() == null) {
//                        XxlJobLogger.log("??????mediaCode???mediaId={}", dto.getMediaId());
//                        return;
//                    }
//                    dto.setCreateTime(DateUtil.format(LocalDateTime.now()));
//                    dto.setSysId(SysEnum.getSysIdBySpId(dto.getSpId()));
//                    // ??????????????????
//                    dto.setMediaType(typeEnum.intValue());
//                    dto.setMediaTypeName(typeEnum.description());
//                    dto.setPosterUrl(prefixUrl + channel.getLogo());
//                    //?????????????????????
//                    if (dto.getPosterUrl() == null || dto.getPosterUrl() == "" || dto.getPosterUrl().length() == 0) {
//                        dto.setPublishStatus(0);
//                    }
//                    // ??????????????????
//                    JSONObject json = strategyScore(dto);
//                    // ????????????
//                    String key = String.format(RedisConstant.MEDIA_KEY, dto.getMediaCode());
//                    // ??????code2id??????
//                    hash.put(dto.getMediaCode(), key);
//                    // ??????redisMap??????
//                    mediaMap.put(key, json.toString());
//                    // ????????????mediaType_free;
//                    json.put("mediaTypeFree", dto.getMediaType() + "_" + dto.getFree());
//                    // ??????BulkRequest??????
//                    IndexRequest indexRequest = new IndexRequest(ElasticSearchConstant.media_index, ElasticSearchConstant.type_default, key);
//                    indexRequest.source(gson.toJson(json), XContentType.JSON);
//                    request.add(indexRequest);
//                });
//            });
            // ?????????redis
            boolean redisResponse = redisService.setHash(RedisConstant.HASH_MEDIA_CODE2ID_KEY, hash, 864000L);
            if (!redisResponse) {
                log.error("Redis setHash error: index={}, size={}", index, size);
            }
            redisResponse = redisService.set(mediaMap, 864000L);
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
     * ????????????
     */
    private void strategySortMap() {
        sortStrategyMap.clear();
        strategyMapper.selectList(null).forEach(strategyDO -> {
            List<StrategyMediaSortDO> sortDOList = strategyMediaSortMapper.selectList(new LambdaQueryWrapper<StrategyMediaSortDO>()
                    .eq(StrategyMediaSortDO::getStrategyId, strategyDO.getId())
                    .gt(StrategyMediaSortDO::getWeight, 0));
            sortStrategyMap.put(strategyDO.getId(), sortDOList);
        });
    }

    /**
     * ??????????????????
     * ??????????????????
     */
    private void maxCount() {
        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        searchSourceBuilder.aggregation(AggregationBuilders.max("maxPlayCount").field("playCount"));
        searchSourceBuilder.aggregation(AggregationBuilders.max("maxSearchCount").field("searchCount"));
        searchSourceBuilder.size(0);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(searchSourceBuilder);
        searchRequest.indices(ElasticSearchConstant.media_index);
        searchRequest.types(ElasticSearchConstant.type_default);
        try {
            SearchResponse response = esService.getClient().search(searchRequest, RequestOptions.DEFAULT);
            ParsedMax max = response.getAggregations().get("maxPlayCount");
            if (max.getValue() > 0) {
                maxPlayCount = new BigDecimal(max.getValue());
            }
            max = response.getAggregations().get("maxSearchCount");
            if (max.getValue() > 0) {
                maxSearchCount = new BigDecimal(max.getValue());
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * ?????????????????????
     *
     * @param mediaDTO
     * @return
     */
    private JSONObject strategyScore(MediaDTO mediaDTO) {

        JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(mediaDTO));
        if (mediaDTO.getMediaId() == 455589) {
            System.out.println(1);
        }
        sortStrategyMap.forEach((key, value) -> {
            BigDecimal strategyScore = BigDecimal.ZERO;
            if (mediaDTO.getMediaId() == 54530) {
                System.out.println(1);
            }
            for (StrategyMediaSortDO sortDO : value) {
                if (sortDO.getMediaSort().equals(MediaSortEnum.BY_PLAY.intValue())) {
                    if (mediaDTO.getPlayCount() != null && maxPlayCount.compareTo(BigDecimal.ZERO) > 0) {
                        //????????????????????????????????????????????????????????????????????????????????????????????????????????????BigDecimal.ROUND_HALF_UP:???????????????
                        BigDecimal s = new BigDecimal(mediaDTO.getPlayCount()).divide(maxPlayCount, 4, RoundingMode.HALF_UP);
                        strategyScore = strategyScore.add(s.multiply(sortDO.getWeight()));
                    }
                } else if (sortDO.getMediaSort().equals(MediaSortEnum.BY_SEARCH.intValue())) {
                    if (mediaDTO.getSearchCount() != null && maxSearchCount.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal s = new BigDecimal(mediaDTO.getSearchCount()).divide(maxSearchCount, 4, RoundingMode.HALF_UP);
                        strategyScore = strategyScore.add(s.multiply(sortDO.getWeight()));
                    }
                } else if (sortDO.getMediaSort().equals(MediaSortEnum.BY_SCORE.intValue())) {
                    if (mediaDTO.getScore() != null) {
                        BigDecimal s = mediaDTO.getScore().movePointLeft(1);
                        strategyScore = strategyScore.add(s.multiply(sortDO.getWeight()));
                    }
                } else if (sortDO.getMediaSort().equals(MediaSortEnum.BY_RELEASE_TIME.intValue())) {
                    if (mediaDTO.getReleaseYear() != null) {
                        BigDecimal s = BigDecimal.ONE.divide(new BigDecimal(LocalDate.now().getYear() - mediaDTO.getReleaseYear() + 1), 4, RoundingMode.HALF_UP);
                        strategyScore = strategyScore.add(s.multiply(sortDO.getWeight()));
                    }
                } else if (sortDO.getMediaSort().equals(MediaSortEnum.BY_PUBLIC_TIME.intValue())) {
                    if (mediaDTO.getOnLineDate() != null) {
                        BigDecimal s = BigDecimal.ONE.divide(new BigDecimal(Period.between(mediaDTO.getOnLineDate().toLocalDate(), LocalDate.now()).getMonths() + 1), 4, RoundingMode.HALF_UP);
                        strategyScore = strategyScore.add(s.multiply(sortDO.getWeight()));
                    }
                }
            }
            json.put("strategyScore" + key, strategyScore.floatValue());
        });
        return json;
    }

    /**
     * ??????x????????????????????????
     *
     * @param hours
     */
    private void cleanMedia(long hours) {
        DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(ElasticSearchConstant.media_index);
        deleteByQueryRequest.setConflicts("proceed");
        deleteByQueryRequest.setQuery(QueryBuilders.rangeQuery("createTime").lte(DateUtil.format(LocalDateTime.now().minusHours(hours))));
        try {
            BulkByScrollResponse response = esService.getClient().deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);
            XxlJobLogger.log("?????????????????? : hours={}, deleted={}", hours, response.getDeleted());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}