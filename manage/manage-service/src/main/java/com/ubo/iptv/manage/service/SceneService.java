package com.ubo.iptv.manage.service;

import com.ubo.iptv.core.CommonResponse;
import com.ubo.iptv.manage.requset.CacheSwitchDTO;

/**
 * @Author: xuning
 * @Date: 2020-11-07
 */
public interface SceneService {

    /**
     * 缓存场景信息
     *
     * @return
     */
    CommonResponse cacheScene();

    /**
     * 缓存场景开关信息
     *
     * @return
     */
    CommonResponse cacheSwitch(CacheSwitchDTO dto);
}
