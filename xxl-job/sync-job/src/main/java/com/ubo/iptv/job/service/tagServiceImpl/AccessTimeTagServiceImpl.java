package com.ubo.iptv.job.service.tagServiceImpl;

import com.ubo.iptv.job.service.TagMarkService;
import com.ubo.iptv.mybatis.gsgd.entity.GSGsydUserDO;
import com.ubo.iptv.mybatis.recommend.entity.TagDO;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class AccessTimeTagServiceImpl implements TagMarkService {

    @Override
    public void mark(Map<String, List<TagDO>> map, GSGsydUserDO user, List<TagDO> userTags) {
        LocalDateTime createtime = user.getCreatetime();
        if (createtime != null) {
            Duration duration = Duration.between(createtime, LocalDateTime.now());
            long day = duration.toDays();
            List<TagDO> list = map.get("入网时间");
            for (TagDO d : list) {
                if (day >= d.getRangeFrom().longValue() && day <= d.getRangeTo().longValue()) {
                    userTags.add(d);
                }
            }
        }
    }
}
