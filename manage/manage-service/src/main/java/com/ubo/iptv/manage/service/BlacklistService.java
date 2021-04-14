package com.ubo.iptv.manage.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubo.iptv.core.CommonResponse;
import com.ubo.iptv.manage.requset.BlacklistAddDTO;
import com.ubo.iptv.manage.response.BlacklistVO;

import java.util.List;

/**
 * @Author: xuning
 * @Date: 2020-11-09
 */
public interface BlacklistService {

    /**
     * 缓存黑名单媒资
     *
     * @return
     */
    CommonResponse cacheBlacklist();

    /**
     * 黑名单列表
     *
     * @param content
     * @param page
     * @return
     */
    CommonResponse<IPage<BlacklistVO>> listBlacklist(String content, Page page);

    /**
     * 黑名单搜索媒资列表
     *
     * @param content
     * @return
     */
    CommonResponse<List<BlacklistVO>> searchMedia(String sysId,String content);

    /**
     * 加入黑名单
     *
     * @param dto
     * @return
     */
    CommonResponse addBlackList(BlacklistAddDTO dto);

    /**
     * 取消黑名单
     *
     * @param id
     * @return
     */
    CommonResponse removeBlackList(Long id);
}
