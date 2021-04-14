package com.ubo.iptv.manage.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ubo.iptv.manage.entity.TagRate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 菜单表 Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2018-12-24
 */
@Mapper
public interface TagSummaryMapper extends BaseMapper {

    @Select("SELECT\n" +
            "v.date AS date,\n" +
            "v.allCount AS allCount,\n" +
            "v.tag_id AS tagId,\n" +
            "v.tag_name AS tagName,\n" +
            "IFNULL(c.count,0) AS count,\n" +
            "( c.count / v.allCount ) * 100 AS rate \n" +
            "FROM\n" +
            "(\n" +
            "SELECT\n" +
            "a.date,\n" +
            "a.allCount,\n" +
            "b.tag_id ,\n" +
            "b.tag_name\n" +
            "FROM\n" +
            "(\n" +
            "SELECT\n" +
            "date,\n" +
            "sum( count ) AS allCount \n" +
            "FROM\n" +
            "gz_recommend.t_tag_daily_summary \n" +
            "${ew.customSqlSegment}\n" +
            "GROUP BY\n" +
            "date \n" +
            "ORDER BY\n" +
            "date DESC \n" +
            "LIMIT 7 \n" +
            ") a,\n" +
            "( SELECT DISTINCT tag_id,tag_name FROM gz_recommend.t_tag_daily_summary ) b \n" +
            ") v\n" +
            "LEFT JOIN gz_recommend.t_tag_daily_summary c ON c.date = v.date \n" +
            "AND c.tag_id = v.tag_id \n" +
            "${ew1.customSqlSegment}" )
    List<TagRate> tagRate(@Param("ew") Wrapper wrapper,@Param("ew1") Wrapper wrapper1);

    @Select("SELECT\n" +
            "v.date AS date,\n" +
            "v.allCount AS allCount,\n" +
            "d.count AS count,\n" +
            "( d.count / v.allCount ) * 100 AS rate,\n" +
            "d.tag_id AS tagId,\n" +
            "d.tag_name AS tagName \n" +
            "FROM\n" +
            "(\n" +
            "SELECT\n" +
            "date,\n" +
            "sum( count ) AS allCount \n" +
            "FROM\n" +
            "gz_recommend.t_tag_daily_summary \n" +
            "${ew.customSqlSegment}\n" +
            "GROUP BY\n" +
            "date\n" +
            "ORDER BY\n" +
            "date DESC \n" +
            "LIMIT 1 \n" +
            ") v\n" +
            "inner JOIN gz_recommend.t_tag_daily_summary d ON d.date = v.date \n" +
            "${ew.customSqlSegment}" )
    List<TagRate> tadayTagRate(@Param("ew") Wrapper wrapper);

}
