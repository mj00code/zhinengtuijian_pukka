package com.ubo.iptv.job.service;

import com.ubo.iptv.mybatis.gsgd.entity.GSGsydUserDO;
import com.ubo.iptv.mybatis.recommend.entity.TagDO;

import java.util.List;
import java.util.Map;

public interface TagMarkService {

    public void mark(Map<String, List<TagDO>> map, GSGsydUserDO user, List<TagDO> userTags);
}
