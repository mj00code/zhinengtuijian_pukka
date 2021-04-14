package com.ubo.iptv.logstash.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ubo.iptv.core.constant.ElasticSearchConstant;
import com.ubo.iptv.core.constant.RedisConstant;
import com.ubo.iptv.core.util.DateUtil;
import com.ubo.iptv.entity.gdgd.MediaDTO;
import com.ubo.iptv.entity.gzdp.UserDTO;
import com.ubo.iptv.logstash.entity.GzIptvLog;
import com.ubo.iptv.logstash.service.LogService;
import com.ubo.iptv.mybatis.recommend.entity.SceneDO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.time.LocalDate;

@Service
@Slf4j
public class LogServiceImpl implements LogService {
    @Autowired
    private RedisService redisService;
    @Autowired
    private EsService esService;

    @Override
    public void historyClean(JSONObject message) {
        cleanMessage(message, true);
    }

    @Override
    public void realtimeClean(JSONObject message) {

        cleanMessage(message, false);
    }

    @Override
    public void recommendSnapshotAdd(JSONObject jsonObject) {
//        RecommendSnapshotDTO dto = jsonObject.toJavaObject(RecommendSnapshotDTO.class);
        IndexRequest request = new IndexRequest();
        request.index(ElasticSearchConstant.recommend_snap);
        request.type(ElasticSearchConstant.type_default);
        request.id(jsonObject.getString("key"));
        request.source(jsonObject.toJSONString(), XContentType.JSON);
        esService.save(request);
    }

    private void cleanMessage(JSONObject message, boolean isHistory) {
        JSONArray list = message.getJSONArray("list");

        List<Number> activeUsers = new ArrayList<>();//线上活跃用户
        List<Number> activeMedia = new ArrayList<>(); //活跃媒资
        List<JSONObject> elasticDocs = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            JSONObject line = list.getJSONObject(i);
            try {
                cleanLine(line, activeUsers, activeMedia, elasticDocs);
            } catch (Exception e) {
                log.error("日志清洗异常", e);
            }
        }

        LocalDate date = LocalDate.now();
        if (isHistory) {
            date = LocalDate.parse(message.getString("date"));
        }
        saveActivateUsers(date, activeUsers);
        saveActivateMedia(date, activeMedia);
        saveToElasticSearch(elasticDocs);

    }

    private void cleanLine(JSONObject jsonObject, List<Number> activeUsers, List<Number> activeMedia, List<JSONObject> elasticDocs) {
        GzIptvLog iptvLog = new GzIptvLog();
        iptvLog.setSys_id(jsonObject.getString("sys_id"));
        iptvLog.setUser_id(jsonObject.getString("user_id"));
        iptvLog.setSeqid(jsonObject.getInteger("seqid"));
        iptvLog.setEvent_type(jsonObject.getString("event_type"));

        iptvLog.setApp_version(jsonObject.getString("app_version"));
        iptvLog.setDe_ua(jsonObject.getString("de_ua"));
        iptvLog.setDevice_brand(jsonObject.getString("device_brand"));
        iptvLog.setDevice_density(jsonObject.getString("device_density"));
        iptvLog.setDevice_model(jsonObject.getString("device_model"));
        iptvLog.setDevice_ph(jsonObject.getIntValue("device_ph"));
        iptvLog.setDevice_pw(jsonObject.getIntValue("device_pw"));
        iptvLog.setDevicetype(jsonObject.getString("devicetype"));
        iptvLog.setNettype(jsonObject.getString("nettype"));
        iptvLog.setNick(jsonObject.getString("nick"));
        iptvLog.setOstype(jsonObject.getString("ostype"));
        iptvLog.setOsversion(jsonObject.getString("osversion"));
        iptvLog.setSdk_version(jsonObject.getString("sdk_version"));
        iptvLog.setSource_channel(jsonObject.getString("source_channel"));
        iptvLog.setUser_type(jsonObject.getString("user_type"));

        if (jsonObject.containsKey("log_time")) {
            iptvLog.setLog_time(jsonObject.getLong("log_time") * 1000);
        }

        String mediaCode = null;
        if (StringUtils.isNotBlank(jsonObject.getString("channel_code"))) {
            mediaCode = jsonObject.getString("channel_code");
        } else if (StringUtils.isNotBlank(jsonObject.getString("content_code"))) {
            mediaCode = jsonObject.getString("content_code");
        } else if (StringUtils.isNotBlank(jsonObject.getString("series_code"))) {
            mediaCode = jsonObject.getString("series_code");
        } else {
            mediaCode = jsonObject.getString("media_code");
        }
//分享探针需要确保content_code各个类型的code间不重复
        if (StringUtils.isNotBlank(mediaCode)) {
            //MediaSyncJobHandler 媒资同步定时任务写入
            String mediaId = redisService.get(RedisConstant.HASH_MEDIA_CODE2ID_KEY, mediaCode);
            if (mediaId != null) {
                String mediaString = redisService.get(mediaId);
                if (StringUtils.isNotBlank(mediaString)) {
                    MediaDTO media = JSONObject.parseObject(mediaString, MediaDTO.class);
                    //mediaTypeId和MediaType的区别
                    if ("EPISODE".equalsIgnoreCase(media.getMediaTypeId())) {//如果是子集取剧头id
                        iptvLog.setMedia_id(media.getParentMediaId());
                    } else {//如果不是取当前的媒资id
                        iptvLog.setMedia_id(media.getMediaId());
                    }
                    iptvLog.setMedia_code(mediaCode);

                    iptvLog.setMedia_name(media.getName());
                    iptvLog.setMedia_type(media.getMediaType());
                    iptvLog.setMedia_type_id(media.getMediaTypeId());
                    iptvLog.setMedia_type_name(media.getMediaTypeName());
                    iptvLog.setActor(media.getActor());
                    iptvLog.setDirector(media.getDirector());
                    //媒资的唯一标识用code代替数据库自增id
                    activeMedia.add(iptvLog.getMedia_id());
                }
            }
        }

        if (StringUtils.isNotBlank(iptvLog.getUser_id())) {
            String userKey = redisService.get(RedisConstant.HASH_USER_CODE2ID_KEY, iptvLog.getUser_id());
            if (StringUtils.isNotBlank(userKey)) {
                UserDTO user = redisService.get(userKey, UserDTO.class);
                if (user != null) {
                    iptvLog.setUser_db_id(user.getUserId());
                    activeUsers.add(user.getUserId());
                }
            }
        }

        String page_url = jsonObject.getString("page_url");
        if (StringUtils.isNotBlank(page_url)) {
            try {
                page_url = URLDecoder.decode(page_url, "utf-8");
                //截取最后/后面的数据，就是栏目code
                String categoryCode = page_url.substring(page_url.lastIndexOf("/") + 1, page_url.length());
                if (StringUtils.isNotEmpty(categoryCode)) {
                    String key = iptvLog.getSys_id() + "_" + categoryCode;
                    String sceneInfo = redisService.get(RedisConstant.HASH_SCENE_ARGS_KEY, key);
                    if (StringUtils.isNotBlank(sceneInfo)) {
                        SceneDO sceneDO = JSONObject.parseObject(sceneInfo, SceneDO.class);
                        iptvLog.setScene_id(sceneDO.getId());
                        String name = sceneDO.getName();
                        int index = -1;
                        if ((index = name.indexOf("大家都在看")) > 1) {
                            iptvLog.setScene_name(name.substring(0, index + 4));
                        } else {
                            iptvLog.setScene_name(name);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("url解析失败:" + page_url);
            }
        }
        iptvLog.compose();
        Map map = JSONObject.parseObject(JSONObject.toJSONString(iptvLog), Map.class);
        jsonObject.putAll(map);
        dealEventType(jsonObject);
        dealContentRecommends(jsonObject);

        JSONObject esDoc = new JSONObject();
        for (String key : jsonObject.keySet()) {
            String value = jsonObject.getString(key);
            if (StringUtils.isNotBlank(value) && !"undefined".equals(value)) {
                esDoc.put(key, jsonObject.get(key));
            }
        }
        elasticDocs.add(esDoc);
    }

    private void saveActivateUsers(LocalDate localDate, List<Number> users) {
        if (users != null && users.size() > 0) {
            try {
                redisService.setZSet(String.format(RedisConstant.DAY_ACTIVE_USER_IDS, DateUtil.format(localDate, "yyyyMMdd")), users, 60 * 60 * 24L);
                log.info("保存活跃用户");
            } catch (Exception e) {
                log.error("保存活跃用户异常", e);
            }
        }
    }

    private void saveActivateMedia(LocalDate localDate, List<Number> medias) {
        if (medias != null && medias.size() > 0) {
            try {
                redisService.setZSet(String.format(RedisConstant.DAY_ACTIVE_MEDIA_IDS, DateUtil.format(localDate, "yyyyMMdd")), medias, 60 * 60 * 24L);
                log.info("保存活跃媒资");
            } catch (Exception e) {
                log.error("保存活跃媒资异常", e);
            }
        }
    }

    private void saveToElasticSearch(List<JSONObject> list) {
        try {
            BulkRequest request = new BulkRequest();
            list.forEach(content -> {
                String create_day = content.getString("create_day");
                if (StringUtils.isNotBlank(create_day)) {
                    log.info(content.getString("id"));
                    IndexRequest indexRequest = new IndexRequest(ElasticSearchConstant.log_index, ElasticSearchConstant.type_default, content.getString("id"));
                    indexRequest.source(content.toJSONString(), XContentType.JSON);
                    request.add(indexRequest);
                }
            });
            esService.bulk(request);
            log.info("日志存入es");
        } catch (Exception e) {
            log.error(JSONObject.toJSONString(list));
            log.error("日志存入es异常");
        }
    }

    /**
     * 搜索页
     */
    private void dealEventType(JSONObject jsonObject) {
        String event_type = jsonObject.getString("event_type");
        if (StringUtils.isNotBlank(event_type)) {
            if ("RECOMMEND_LOCATION_CLICK".equals(event_type) && "search.jsp".equals(jsonObject.getString("refer_page_id"))) {
                jsonObject.put("SEARCH_CLICK", 1);
            } else {
                jsonObject.put(event_type, 1);
            }
        }

    }

    private void dealContentRecommends(JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("content_recommends");
        if (jsonArray != null && jsonArray.size() > 0) {
            Set<String> set = new HashSet<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                String mediacode = jsonArray.getJSONObject(i).getString("media_code");
                if (StringUtils.isNotBlank(mediacode)) {
                    set.add(mediacode);
                }
            }
            if (set.size() > 0) {
                jsonObject.put("media_code", set);
                jsonObject.put("mediacode", set);
            }
        }
    }

    public static void main(String[] args) {
        String url = "pages/index/index/20200404193623741";
        String result = url.substring(url.lastIndexOf("/") + 1, url.length());
        System.out.println(result);
    }

}
