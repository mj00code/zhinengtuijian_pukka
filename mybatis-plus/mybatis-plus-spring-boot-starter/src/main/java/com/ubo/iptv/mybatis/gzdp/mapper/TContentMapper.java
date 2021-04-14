package com.ubo.iptv.mybatis.gzdp.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ubo.iptv.mybatis.gsgd.entity.GSContentCatalogDO;
import com.ubo.iptv.mybatis.gsgd.entity.GSContentinfoDO;
import com.ubo.iptv.mybatis.gzdp.entity.TChannelDO;
import com.ubo.iptv.mybatis.gzdp.entity.TContentDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ubo.iptv.mybatis.gzdp.entity.TScheduleDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 内容 Mapper 接口
 * </p>
 *
 * @author gzdp
 * @since 2021-03-08
 */
public interface TContentMapper extends BaseMapper<TContentDO> {

    @Select("SELECT" +
            " IF(t_media_summary.search_count_deadline>now(),t_media_summary.search_count,0) AS searchCount," +
            " IF(t_media_summary.play_count_deadline>now(),t_media_summary.play_count,0) AS recommendCount," +
            " t1.*" +
            " FROM gzdp.t_content t1" +
            " LEFT JOIN gz_recommend.t_media_summary ON t_media_summary.media_id=t1.id" +
            " LEFT JOIN gzdp.t_content t2 on t1.id = t2.id" +
            " ${ew.customSqlSegment}")
    List<TContentDO> selectListWithSummary(@Param("ew") Wrapper queryWrapper);

}
