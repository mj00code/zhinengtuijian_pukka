package cn.pukkasoft.datasync.service.impl;

import cn.pukkasoft.datasync.advice.ApiResponse;
import cn.pukkasoft.datasync.dao.TContentPicUrlMapper;
import cn.pukkasoft.datasync.dao.TProgramMapper;
import cn.pukkasoft.datasync.model.ContentInfoQuery;
import cn.pukkasoft.datasync.model.ContentPageResult;
import cn.pukkasoft.datasync.model.Contentinfo;
import cn.pukkasoft.datasync.model.TContentPicUrl;
import cn.pukkasoft.datasync.service.ITContentPicUrlService;
import cn.pukkasoft.datasync.util.HttpUtils;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 媒资图片关系Service业务层处理
 *
 * @author
 * @date 2020-11-22
 */
@Service
public class TContentPicUrlServiceImpl implements ITContentPicUrlService {

    private static final Log log = LogFactory.getLog(TContentPicUrlServiceImpl.class);

    private static final Gson gson = new Gson();

    @Value("${canal.waitTime}")
    private String waitTime;

    private static Gson getGson() {
        return gson;
    }

    @Autowired
    private TContentPicUrlMapper tContentPicUrlMapper;

    @Autowired
    private TProgramMapper programMapper;

    @Autowired
    private ITContentPicUrlService contentPicUrlService;

    @Value("${contentInfo.url}")
    private String url;

    @Value("${contentInfo.path}")
    private String path;

    @Value("${contentInfo.portalCode}")
    private String portalCode;

    @Value("${contentInfo.spToken}")
    private String spToken;

    /**
     * 查询媒资图片关系
     *
     * @param id 媒资图片关系ID
     * @return 媒资图片关系
     */
    @Override
    public TContentPicUrl selectTContentPicUrlById(Long id) {
        return tContentPicUrlMapper.selectTContentPicUrlById(id);
    }

    /**
     * 查询媒资图片关系列表
     *
     * @param tContentPicUrl 媒资图片关系
     * @return 媒资图片关系
     */
    @Override
    public List<TContentPicUrl> selectTContentPicUrlList(TContentPicUrl tContentPicUrl) {
        return tContentPicUrlMapper.selectTContentPicUrlList(tContentPicUrl);
    }

    /**
     * 新增媒资图片关系
     *
     * @param tContentPicUrl 媒资图片关系
     * @return 结果
     */
    @Override
    @Transactional
    public int insertTContentPicUrl(TContentPicUrl tContentPicUrl) {
        tContentPicUrl.setCreateTime(new Date());
        return tContentPicUrlMapper.insertTContentPicUrl(tContentPicUrl);
    }

    /**
     * 修改媒资图片关系
     *
     * @param tContentPicUrl 媒资图片关系
     * @return 结果
     */
    @Override
    @Transactional
    public int updateTContentPicUrl(TContentPicUrl tContentPicUrl) {
        tContentPicUrl.setUpdateTime(new Date());
        return tContentPicUrlMapper.updateTContentPicUrl(tContentPicUrl);
    }

    /**
     * 删除媒资图片关系信息
     *
     * @param id 媒资图片关系ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteTContentPicUrlById(Long id) {
        return tContentPicUrlMapper.deleteTContentPicUrlById(id);
    }

    @Override
    @Transactional
    public ApiResponse notifyContentStatus(Integer contentType, String contentCode, Integer contentId) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            if (StringUtils.isNotEmpty(contentCode)) {
                ContentInfoQuery info = new ContentInfoQuery();
                if (contentType == 2 || contentType == 3) {
                    info.setContentcode(contentCode);
                }
                if (contentType == 1 || contentType == 5) {
                    if (contentId != null) {
                        String iptvId = programMapper.getProgramById(contentId);
                        if (StringUtils.isNotEmpty(iptvId)) {
                            contentCode = iptvId;
                            info.setContentcode(contentCode);
                        }
                    } else {
                        info.setContentcode("");
                    }
                }
                TContentPicUrl contentPicUrl = new TContentPicUrl();
                contentPicUrl.setContentCode(contentCode);
                int count = contentPicUrlService.getContentPicCount(contentCode);
                info.setFormat("json");
                info.setPortalcode(portalCode);
                info.setSptoken(spToken);
                info.setPageindex(1);
                info.setPagesize(1);
                info.setTmtype(1);
                String param = getGson().toJson(info);
                log.info("====The request parameter is====portalCode=" + portalCode + ";spToken=" + spToken + ";contentcode=" + contentCode);
                if (count == 0 && StringUtils.isNotEmpty(info.getContentcode())) {
                    log.info("======Add media picture=======");
                    //新增
                    Contentinfo contentinfo = getContentInfo(param, url + path);
                    if (contentinfo != null && contentinfo.getId() != null) {
                        //图片地址
                        String imgUrl = contentinfo.getMidimg();
                        //图片存在
                        if (StringUtils.isNotEmpty(imgUrl)) {
                            int contentType2 = contentinfo.getContenttype();
                            String contentCode2 = contentinfo.getContentcode();
                            if (StringUtils.isEmpty(contentCode2)) {
                                contentCode2 = contentCode;
                            }
                            String name = contentinfo.getName();
                            //新增记录
                            TContentPicUrl contentPicUrl2 = new TContentPicUrl();
                            contentPicUrl2.setContentType(contentType2);
                            contentPicUrl2.setContentCode(contentCode2);
                            contentPicUrl2.setMediaName(name);
                            contentPicUrl2.setPicUrl(imgUrl);
                            contentPicUrl2.setCreateTime(new Date());
                            contentPicUrl2.setCornerPicurl(contentinfo.getCornerpicurl());
                            contentPicUrl2.setSpid(contentinfo.getSpid());
                            contentPicUrl2.setSpName(contentinfo.getSpname());
                            contentPicUrl2.setCpid(contentinfo.getCpid());
                            contentPicUrl2.setCpName(contentinfo.getCpname());
                            insertTContentPicUrl(contentPicUrl2);
                        } else {
                            //图片不存在，判断角标是否存在
                            if (StringUtils.isNotEmpty(contentinfo.getCornerpicurl())) {
                                int contentType2 = contentinfo.getContenttype();
                                String contentCode2 = contentinfo.getContentcode();
                                if (StringUtils.isEmpty(contentCode2)) {
                                    contentCode2 = contentCode;
                                }
                                String name = contentinfo.getName();
                                TContentPicUrl contentPicUrl3 = new TContentPicUrl();
                                contentPicUrl3.setContentType(contentType2);
                                contentPicUrl3.setContentCode(contentCode2);
                                contentPicUrl3.setMediaName(name);
                                contentPicUrl3.setPicUrl(imgUrl);
                                contentPicUrl3.setCreateTime(new Date());
                                contentPicUrl3.setCornerPicurl(contentinfo.getCornerpicurl());
                                contentPicUrl3.setSpid(contentinfo.getSpid());
                                contentPicUrl3.setSpName(contentinfo.getSpname());
                                contentPicUrl3.setCpid(contentinfo.getCpid());
                                contentPicUrl3.setCpName(contentinfo.getCpname());
                                insertTContentPicUrl(contentPicUrl3);
                            }
                        }
                    }
                } else if (count > 0 && StringUtils.isNotEmpty(info.getContentcode())) {
                    log.info("======Edit media picture=======");
                    //修改
                    Contentinfo contentinfo = getContentInfo(param, url + path);
                    if (contentinfo != null && contentinfo.getId() != null) {
                        String contentCode2 = contentinfo.getContentcode();
                        if (StringUtils.isEmpty(contentCode2)) {
                            contentCode2 = contentCode;
                        }
                        int contentType2 = contentinfo.getContenttype();
                        String imgUrl = contentinfo.getMidimg();
                        String name = contentinfo.getName();
                        //判断图片地址是否存在且是否需要修改
                        TContentPicUrl contentPicUrl2 = new TContentPicUrl();
                        contentPicUrl2.setContentCode(contentCode);
                        List<TContentPicUrl> contentPicUrlList = selectTContentPicUrlList(contentPicUrl2);
                        if (contentPicUrlList.size() == 1 && contentPicUrlList != null) {
                            TContentPicUrl urlResult = contentPicUrlList.get(0);
                            //新旧图片不同，则需要修改图片地址
                            String oldUrl = urlResult.getPicUrl();
                            if (oldUrl == null) {
                                oldUrl = "";
                            }
                            String oldCornUrl = urlResult.getCornerPicurl();
                            if (oldCornUrl == null) {
                                oldCornUrl = "";
                            }
                            String newCornUrl = contentinfo.getCornerpicurl();
                            if (newCornUrl == null) {
                                newCornUrl = "";
                            }
                            if (imgUrl == null) {
                                imgUrl = "";
                            }
                            //海报不相等，则修改
                            if (!imgUrl.equals(oldUrl)) {
                                TContentPicUrl contentPicUrlVo = new TContentPicUrl();
                                contentPicUrlVo.setId(urlResult.getId());
                                contentPicUrlVo.setContentType(contentType2);
                                contentPicUrlVo.setContentCode(contentCode2);
                                contentPicUrlVo.setMediaName(name);
                                contentPicUrlVo.setPicUrl(imgUrl);
                                contentPicUrlVo.setUpdateTime(new Date());
                                contentPicUrlVo.setCornerPicurl(contentinfo.getCornerpicurl());
                                contentPicUrlVo.setSpid(contentinfo.getSpid());
                                contentPicUrlVo.setSpName(contentinfo.getSpname());
                                contentPicUrlVo.setCpid(contentinfo.getCpid());
                                contentPicUrlVo.setCpName(contentinfo.getCpname());
                                updateTContentPicUrl(contentPicUrlVo);
                            } else if (!newCornUrl.equals(oldCornUrl)) {
                                //角标不相等
                                TContentPicUrl contentPicUrlVo2 = new TContentPicUrl();
                                contentPicUrlVo2.setId(urlResult.getId());
                                contentPicUrlVo2.setContentType(contentType2);
                                contentPicUrlVo2.setContentCode(contentCode2);
                                contentPicUrlVo2.setMediaName(name);
                                contentPicUrlVo2.setUpdateTime(new Date());
                                contentPicUrlVo2.setCornerPicurl(contentinfo.getCornerpicurl());
                                contentPicUrlVo2.setSpid(contentinfo.getSpid());
                                contentPicUrlVo2.setSpName(contentinfo.getSpname());
                                contentPicUrlVo2.setCpid(contentinfo.getCpid());
                                contentPicUrlVo2.setCpName(contentinfo.getCpname());
                                updateTContentPicUrl(contentPicUrlVo2);
                            }
                        }
                    }
                }
                apiResponse.setCode(1);
                apiResponse.setErrorMessage("SUCCESS");
            }
        } catch (Exception e) {
            log.error(e);
            apiResponse.setCode(0);
            apiResponse.setErrorMessage("FAIL");
        }
        return apiResponse;
    }

    @Override
    public int getContentPicCount(String contentCode) {
        return tContentPicUrlMapper.getContentPicCount(contentCode);
    }

    protected Contentinfo getContentInfo(String param, String path) throws Exception {
        //控制访问速率
        Thread.sleep(Long.parseLong(waitTime));
        HttpResponse response = HttpUtils.doPost2(path, param);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            String result = EntityUtils.toString(entity, "UTF-8");
            log.info("====Http response data====" + result);
            ContentPageResult contentInfoResult = getGson().fromJson(result, ContentPageResult.class);
            List<Contentinfo> lists = contentInfoResult.getResult();
            Contentinfo contentinfo = new Contentinfo();
            if (lists.size() > 0 && lists != null) {
                contentinfo = lists.get(0);
            }
            return contentinfo;
        } else {
            return null;
        }
    }
}