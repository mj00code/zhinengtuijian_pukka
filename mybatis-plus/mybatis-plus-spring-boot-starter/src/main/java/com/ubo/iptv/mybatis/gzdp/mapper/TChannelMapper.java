package com.ubo.iptv.mybatis.gzdp.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ubo.iptv.mybatis.gzdp.entity.TChannelDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 频道 Mapper 接口
 * </p>
 *
 * @author gzdp
 * @since 2021-03-09
 */
public interface TChannelMapper extends BaseMapper<TChannelDO> {

    @Select("SELECT" +
//            " IF(t_media_summary.search_count_deadline>now(),t_media_summary.search_count,0) AS searchCount," +
//            " IF(t_media_summary.play_count_deadline>now(),t_media_summary.play_count,0) AS recommendCount," +
            " t_content_category.content_type contentType," +
            " t_channel.*" +
            " FROM gzdp.t_content_category" +
            " LEFT JOIN gzdp.t_channel ON t_content_category.content_code=t_channel.channel_id" +
            " LEFT JOIN gz_recommend.t_media_summary ON t_media_summary.media_code=t_channel.channel_id" +
            " ${ew.customSqlSegment}")
    List<TChannelDO> selectChannelListWithSummary(@Param("ew") Wrapper queryWrapper);

}
