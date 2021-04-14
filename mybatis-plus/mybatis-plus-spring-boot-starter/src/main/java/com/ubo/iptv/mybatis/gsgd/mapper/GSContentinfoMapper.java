package com.ubo.iptv.mybatis.gsgd.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ubo.iptv.mybatis.gsgd.entity.GSContentinfoDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author ottdb_gsgd
 * @since 2020-11-10
 */
public interface GSContentinfoMapper extends BaseMapper<GSContentinfoDO> {

    /**
     * 带统计信息的媒资列表
     *
     * @param queryWrapper
     * @return
     */
    @Select("SELECT" +
            " IF(t_media_summary.search_count_deadline>now(),t_media_summary.search_count,0) AS HitCount," +
            " IF(t_media_summary.play_count_deadline>now(),t_media_summary.play_count,0) AS RecommendCount," +
            " CASE t_contentinfo.ContentType" +
            " WHEN 1 THEN t_program.iptvId" +
            " WHEN 2 THEN t_program.iptvId" +
            " WHEN 4 THEN t_channel.iptvId" +
            " ELSE t_contentinfo.ContentCode" +
            " END AS ContentCode," +
            " t_contentinfo.*" +
            " FROM ottdb_gsgd.t_contentinfo" +
            " LEFT JOIN gz_recommend.t_media_summary ON t_media_summary.media_id=t_contentinfo.ID" +
            " LEFT JOIN ottdb_gsgd.t_program ON t_program.ID=t_contentinfo.ContentID" +
            " LEFT JOIN ottdb_gsgd.t_channel ON t_channel.ID=t_contentinfo.ContentID" +
            " ${ew.customSqlSegment}")
    List<GSContentinfoDO> selectListWithSummary(@Param("ew") Wrapper queryWrapper);

    /**
     * 搜索媒资
     *
     * @param spId
     * @param content
     * @return
     */
    @Select("<script>(SELECT id, SPID, ContentType, ContentCode, `Name`, CPName" +
            " FROM ottdb_gsgd.t_contentinfo " +
            " WHERE <choose><when test='spId!=null'>t_contentinfo.SPID=#{spId}</when><otherwise>t_contentinfo.SPID IN (1005,1009)</otherwise></choose>" +
            " AND ContentType IN (3,5,6) AND `Name` LIKE '${content}%'" +
            " LIMIT 10) UNION" +
            " (SELECT id, SPID, ContentType, ContentCode, `Name`, CPName" +
            " FROM ottdb_gsgd.t_contentinfo " +
            " WHERE <choose><when test='spId!=null'>t_contentinfo.SPID=#{spId}</when><otherwise>t_contentinfo.SPID IN (1005,1009)</otherwise></choose>" +
            " AND ContentType IN (3,5,6) AND ContentCode LIKE '${content}%'" +
            " LIMIT 10) UNION ALL" +
            " (SELECT t_contentinfo.id, t_contentinfo.SPID, t_contentinfo.ContentType, t_program.iptvId, t_contentinfo.`Name`, t_contentinfo.CPName" +
            " FROM ottdb_gsgd.t_program" +
            " LEFT JOIN ottdb_gsgd.t_contentinfo ON t_contentinfo.ContentID=t_program.ID" +
            " WHERE <choose><when test='spId!=null'>t_contentinfo.SPID=#{spId}</when><otherwise>t_contentinfo.SPID IN (1005,1009)</otherwise></choose>" +
            " AND t_contentinfo.ContentType=1 AND t_contentinfo.`Name` LIKE '${content}%'" +
            " LIMIT 10) UNION" +
            " (SELECT t_contentinfo.id, t_contentinfo.SPID, t_contentinfo.ContentType, t_program.iptvId, t_contentinfo.`Name`, t_contentinfo.CPName" +
            " FROM ottdb_gsgd.t_program" +
            " LEFT JOIN ottdb_gsgd.t_contentinfo ON t_contentinfo.ContentID=t_program.ID" +
            " WHERE <choose><when test='spId!=null'>t_contentinfo.SPID=#{spId}</when><otherwise>t_contentinfo.SPID IN (1005,1009)</otherwise></choose>" +
            " AND t_contentinfo.ContentType=1 AND t_program.iptvId LIKE '${content}%'" +
            " LIMIT 10) UNION ALL" +
            " (SELECT t_contentinfo.id, t_contentinfo.SPID, t_contentinfo.ContentType, t_channel.iptvId, t_channel.`Name`, t_contentinfo.CPName" +
            " FROM ottdb_gsgd.t_channel" +
            " LEFT JOIN ottdb_gsgd.t_contentinfo ON t_contentinfo.ContentID=t_channel.ID" +
            " WHERE <choose><when test='spId!=null'>t_contentinfo.SPID=#{spId}</when><otherwise>t_contentinfo.SPID IN (1005,1009)</otherwise></choose>" +
            " AND t_contentinfo.ContentType=4 AND t_contentinfo.`Name` LIKE '${content}%'" +
            " LIMIT 10) UNION" +
            " (SELECT t_contentinfo.id, t_contentinfo.SPID, t_contentinfo.ContentType, t_channel.iptvId, t_channel.`Name`, t_contentinfo.CPName" +
            " FROM ottdb_gsgd.t_channel" +
            " LEFT JOIN ottdb_gsgd.t_contentinfo ON t_contentinfo.ContentID=t_channel.ID" +
            " WHERE <choose><when test='spId!=null'>t_contentinfo.SPID=#{spId}</when><otherwise>t_contentinfo.SPID IN (1005,1009)</otherwise></choose>" +
            " AND t_contentinfo.ContentType=4 AND t_channel.iptvId LIKE '${content}%'" +
            " LIMIT 10) LIMIT 10</script>")
    List<GSContentinfoDO> searchMedia(@Param("spId") Integer spId, @Param("content") String content);
}
