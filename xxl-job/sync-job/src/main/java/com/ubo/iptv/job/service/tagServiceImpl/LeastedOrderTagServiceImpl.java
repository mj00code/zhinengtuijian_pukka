package com.ubo.iptv.job.service.tagServiceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ubo.iptv.job.service.TagMarkService;
import com.ubo.iptv.mybatis.gsgd.entity.GSGsydUserDO;
import com.ubo.iptv.mybatis.gsgd.entity.GsydOrderDO;
import com.ubo.iptv.mybatis.gsgd.mapper.GsydOrderMapper;
import com.ubo.iptv.mybatis.recommend.entity.TagDO;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Deprecated
public class LeastedOrderTagServiceImpl implements TagMarkService {
    @Autowired
    private GsydOrderMapper orderMapper;

    @Override
    public void mark(Map<String, List<TagDO>> map, GSGsydUserDO user, List<TagDO> userTags) {
        LambdaQueryWrapper<GsydOrderDO> wrapper = new LambdaQueryWrapper<GsydOrderDO>();
        wrapper.eq(GsydOrderDO::getUserID, user.getIPTVUserID());
        wrapper.eq(GsydOrderDO::getPayStatus, 1);
        wrapper.orderByDesc(GsydOrderDO::getCreateTime);

        GsydOrderDO order = orderMapper.selectOne(wrapper);
        if (order != null) {
            Duration duration = Duration.between(order.getCreateTime(), LocalDateTime.now());
            long day = duration.toDays();
            List<TagDO> list = map.get("最近一次订购成功距当前时间");
            for (TagDO d : list) {
                if (day >= d.getRangeFrom().longValue() && day <= d.getRangeTo().longValue()) {
                    userTags.add(d);
                }
            }
        }
    }


}
