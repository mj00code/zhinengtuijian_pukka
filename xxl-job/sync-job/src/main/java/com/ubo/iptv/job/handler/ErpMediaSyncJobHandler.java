package com.ubo.iptv.job.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ubo.iptv.core.constant.RedisConstant;
import com.ubo.iptv.core.enums.SysEnum;
import com.ubo.iptv.mybatis.gsgd.entity.GSChannelDO;
import com.ubo.iptv.mybatis.gsgd.entity.GSContentinfoDO;
import com.ubo.iptv.mybatis.gsgd.entity.GSProgramDO;
import com.ubo.iptv.mybatis.gsgd.mapper.GSChannelMapper;
import com.ubo.iptv.mybatis.gsgd.mapper.GSContentinfoMapper;
import com.ubo.iptv.mybatis.gsgd.mapper.GSProgramMapper;
import com.ubo.iptv.mybatis.recommend.entity.SceneDO;
import com.ubo.iptv.mybatis.recommend.mapper.SceneMapper;
import com.ubo.iptv.redis.RedisService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.util.ShardingUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: xuning
 * @Date: 2020-12-08
 */
@Service
@JobHandler(value = "erpMediaSyncJobHandler")
@Slf4j
public class ErpMediaSyncJobHandler extends IJobHandler {

    @Value("${iptv.portal.host}")
    private String host;
    @Value("${iptv.portal.portalcode}")
    private String portalcode;
    @Value("${iptv.portal.sptoken}")
    private String sptoken;

    @Autowired
    private SceneMapper sceneMapper;
    @Autowired
    private GSContentinfoMapper gsContentinfoMapper;
    @Autowired
    private GSProgramMapper gsProgramMapper;
    @Autowired
    private GSChannelMapper gsChannelMapper;

    @Autowired
    private CloseableHttpClient httpClient;
    @Autowired
    private RedisService redisService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        long begin = System.currentTimeMillis();
        // 分片参数
        ShardingUtil.ShardingVO shardingVO = ShardingUtil.getShardingVo();
        XxlJobLogger.log("分片参数: 当前分片序号={}, 总分片数={}", shardingVO.getIndex(), shardingVO.getTotal());

        syncErpMedia(shardingVO);

        XxlJobLogger.log("任务完成：共耗时 {}秒", (System.currentTimeMillis() - begin) / 1000);
        return SUCCESS;
    }

    private void syncErpMedia(ShardingUtil.ShardingVO shardingVO) {
        List<SceneDO> sceneDOList = sceneMapper.selectList(new LambdaQueryWrapper<SceneDO>()
                .isNotNull(SceneDO::getCategoryCode)
                .apply(shardingVO.getTotal() > 1, "MOD(id,{0})={1}", shardingVO.getTotal(), shardingVO.getIndex()));
        sceneDOList.forEach(sceneDO -> {
            //TODO  此处需要转换为category_id
            Set<String> programCodes = getProgramCode(sceneDO.getCategoryCode());
            XxlJobLogger.log("场景{}：排除媒资code={}", sceneDO.getId(), programCodes);
            Map<Integer, String> mediaIds = new HashMap<>(programCodes.size());
            programCodes.forEach(programCode -> {
                Integer mediaId = getMediaId(sceneDO.getSysId(), programCode);
                if (mediaId != null) {
                    mediaIds.put(mediaId, "");
                }
            });
            if (!mediaIds.isEmpty()) {
                redisService.set(String.format(RedisConstant.ERP_PROVIDED, sceneDO.getId()), JSONObject.toJSONString(mediaIds), 864000L);
            }
            XxlJobLogger.log("场景{}：排除媒资id={}", sceneDO.getId(), mediaIds.keySet());
        });
    }

    /**
     * 获取ProgramCode
     *
     * @param categoryId
     * @return
     */
    private Set<String> getProgramCode(String categoryId) {
        Set<String> set = new HashSet<>();

        String uri = String.format("http://%s/IPTVPortalInterface/content/gettagslist.do", host);
        int pageindex = 0;
        int pagetotal = 1;
        while (pageindex < pagetotal) {
            HttpPost httpPost = new HttpPost(uri);
            JSONObject post = new JSONObject();
            post.put("format", "json");
            post.put("portalcode", portalcode);
            post.put("sptoken", sptoken);
            post.put("type", "1");
            post.put("parentid", categoryId);
            post.put("orderinfo", "1");
            post.put("pageindex", String.valueOf(++pageindex));
            post.put("pagesize", "20");
            httpPost.setEntity(new StringEntity(post.toJSONString(), Consts.UTF_8));
            JSONObject response = request(httpPost);
            if (response != null && "0".equals(response.getString("resultcode"))) {
                pagetotal = response.getInteger("pagetotal");
                JSONArray results = response.getJSONArray("result");
                for (int i = 0; i < results.size(); i++) {
                    JSONObject result = results.getJSONObject(i);
                    Integer id = result.getInteger("id");
                    String description = result.getString("description");
                    Map<String, String> parameter = getUrlParameter(description);
                    if (parameter.containsKey("programCode")) {
                        set.add(parameter.get("programCode"));
                    } else {
                        Set<String> subSet = getProgramCode(id.toString());
                        if (!subSet.isEmpty()) {
                            set.addAll(subSet);
                        }
                    }
                }
            }
        }
        XxlJobLogger.log("栏目{}：排除媒资code={}", categoryId, set);
        return set;
    }

    /**
     * 获取MediaId
     *
     * @param programCode
     * @return
     */
    private Integer getMediaId(String sysId, String programCode) {
        HttpPost httpPost = new HttpPost(String.format("http://%s/IPTVPortalInterface/content/getcontentlist.do", host));
        JSONObject post = new JSONObject();
        post.put("format", "json");
        post.put("portalcode", portalcode);
        post.put("sptoken", sptoken);
        post.put("tmtype", "1");
        post.put("contentid", programCode);
        post.put("orderinfo", "1");
        post.put("pageindex", "1");
        post.put("pagesize", "20");
        httpPost.setEntity(new StringEntity(post.toJSONString(), Consts.UTF_8));
        JSONObject response = request(httpPost);
        if (response != null && "0".equals(response.getString("resultcode"))) {
            JSONArray results = response.getJSONArray("result");
            for (int i = 0; i < results.size(); i++) {
                JSONObject result = results.getJSONObject(i);
                String contentcode = result.getString("contentcode");
                Integer contenttype = result.getInteger("contenttype");
                if (contenttype == 1 || contenttype == 2) {
                    GSProgramDO programDO = gsProgramMapper.selectOne(new LambdaQueryWrapper<GSProgramDO>().eq(GSProgramDO::getIptvId, contentcode).last("LIMIT 1"));
                    if (programDO != null) {
                        GSContentinfoDO contentinfoDO = gsContentinfoMapper.selectOne(new LambdaQueryWrapper<GSContentinfoDO>()
                                .eq(GSContentinfoDO::getSpid, SysEnum.getSpIdBySysId(sysId))
                                .eq(GSContentinfoDO::getContentID, programDO.getId()).last("LIMIT 1"));
                        if (contentinfoDO != null) {
                            return contentinfoDO.getId();
                        }
                    }
                } else if (contenttype == 4) {
                    GSChannelDO channelDO = gsChannelMapper.selectOne(new LambdaQueryWrapper<GSChannelDO>().eq(GSChannelDO::getIptvId, contentcode).last("LIMIT 1"));
                    if (channelDO != null) {
                        GSContentinfoDO contentinfoDO = gsContentinfoMapper.selectOne(new LambdaQueryWrapper<GSContentinfoDO>()
                                .eq(GSContentinfoDO::getSpid, SysEnum.getSpIdBySysId(sysId))
                                .eq(GSContentinfoDO::getContentID, channelDO.getId()).last("LIMIT 1"));
                        if (contentinfoDO != null) {
                            return contentinfoDO.getId();
                        }
                    }
                } else {
                    GSContentinfoDO contentinfoDO = gsContentinfoMapper.selectOne(new LambdaQueryWrapper<GSContentinfoDO>()
                            .eq(GSContentinfoDO::getSpid, SysEnum.getSpIdBySysId(sysId))
                            .eq(GSContentinfoDO::getContentCode, contentcode).last("LIMIT 1"));
                    if (contentinfoDO != null) {
                        return contentinfoDO.getId();
                    }
                }
            }
        }
        return null;
    }

    private static long timeMillis = System.currentTimeMillis();

    /**
     * 发送请求
     *
     * @param request
     * @return
     */
    private JSONObject request(HttpUriRequest request) {
        // 限制请求频率
        long interval = 5000 * ShardingUtil.getShardingVo().getTotal();
        long time = System.currentTimeMillis() - timeMillis;
        if (time < interval) {
            try {
                Thread.sleep(interval - time);
            } catch (InterruptedException e) {
                log.error("sleep error", e);
            }
        }
        log.info("request: {}-{}", request.getMethod(), request.getURI());
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            StatusLine statusLine = response.getStatusLine();
            HttpEntity entity = response.getEntity();
            // 状态码200
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                if (entity != null) {
                    String entityContent = EntityUtils.toString(entity, Consts.UTF_8);
                    log.info("response: {}", entityContent);
                    return JSONObject.parseObject(entityContent);
                }
            }
            // 错误信息
            log.error("response: {}", response);
        } catch (IOException e) {
            log.error("request error", e);
        } finally {
            timeMillis = System.currentTimeMillis();
        }
        return null;
    }


    private static Pattern pattern = Pattern.compile("(\\?|&+)(.+?)=([^&]*)");

    /**
     * 获取URL参数
     *
     * @param url
     * @return
     */
    private static Map<String, String> getUrlParameter(String url) {
        Map<String, String> paramMap = new HashMap<>();
        if (StringUtils.isNotBlank(url)) {
            //匹配参数名和参数值的正则表达式
            Matcher m = pattern.matcher(url);
            while (m.find()) {
                //获取参数名
                String paramName = m.group(2).trim();
                //获取参数值
                String paramVal = m.group(3).trim();
                paramMap.put(paramName, paramVal);
            }
        }
        return paramMap;
    }
}
