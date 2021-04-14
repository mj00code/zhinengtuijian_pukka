package com.ubo.iptv.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ubo.iptv.mybatis.recommend.entity.TagDO;
import com.ubo.iptv.mybatis.recommend.entity.UserTagDO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MyUserTagMapper extends BaseMapper<UserTagDO> {

    @Delete("TRUNCATE gz_recommend.t_user_tag")
    void truncate();

    @Insert({"<script>" +
            "insert into gz_recommend.t_user_tag(user_id,classify,type,divide,tag_id,tag_name) values " +
            "<foreach collection='list' item='item' index='index' separator=','>" +
            "   (#{item.userId},#{item.classify},#{item.type},#{item.divide},#{item.tagId},#{item.tagName})" +
            "</foreach>" +
            "</script>"})
    public void bacthInsert(@Param("list") List<UserTagDO> list);

    @Insert("<script>" +
            "INSERT INTO gz_recommend.t_user_tag(user_id, classify, type, divide, tag_id, tag_name)" +
            " SELECT t.ID, t_tag.classify, t_tag.type, t_tag.divide, t.tagId, t_tag.`name`" +
            " FROM (SELECT t_gsyd_user.ID," +
            " CASE" +
            "<foreach collection='list' item='item' index='index'>" +
            "<choose>" +
            "<when test='index != list.size()-1'> WHEN LastOrderDays &gt;= #{item.rangeFrom} AND LastOrderDays &lt;= #{item.rangeTo} THEN #{item.id}</when>" +
            "<otherwise> ELSE #{item.id}</otherwise>" +
            "</choose>" +
            "</foreach>" +
            " END AS tagId" +
            " FROM ottdb_gsgd.t_gsyd_user" +
            " INNER JOIN ( SELECT UserID, DATEDIFF(now(), MAX(CreateTime)) AS LastOrderDays FROM ottdb_gsgd.t_gsyd_order WHERE `Status` = 1 GROUP BY UserID ) lod ON lod.UserID = t_gsyd_user.IPTVUserID" +
            " WHERE t_gsyd_user.`Status` = 5) t" +
            " LEFT JOIN gz_recommend.t_tag ON t_tag.id = t.tagId" +
            "</script>")
    int insertLastOrderTag(@Param("list") List<TagDO> list);

    @Insert("<script>" +
            "INSERT INTO gz_recommend.t_user_tag(user_id, classify, type, divide, tag_id, tag_name)" +
            " SELECT t.ID, t_tag.classify, t_tag.type, t_tag.divide, t.tagId, t_tag.`name`" +
            " FROM (SELECT t_gsyd_user.ID," +
            " CASE" +
            "<foreach collection='list' item='item' index='index'>" +
            "<choose>" +
            "<when test='index != list.size()-1'> WHEN IFNULL(OrderCount,0) &gt;= #{item.rangeFrom} AND IFNULL(OrderCount,0) &lt;= #{item.rangeTo} THEN #{item.id}</when>" +
            "<otherwise> ELSE #{item.id}</otherwise>" +
            "</choose>" +
            "</foreach>" +
            " END AS tagId" +
            " FROM ottdb_gsgd.t_gsyd_user" +
            " LEFT JOIN ( SELECT UserID, COUNT(*) AS OrderCount FROM ottdb_gsgd.t_gsyd_order WHERE `Status` = 1 AND CreateTime > DATE_SUB(NOW(),INTERVAL 30 DAY) GROUP BY UserID ) oc ON oc.UserID = t_gsyd_user.IPTVUserID" +
            " WHERE t_gsyd_user.`Status` = 5) t" +
            " LEFT JOIN gz_recommend.t_tag ON t_tag.id = t.tagId" +
            "</script>")
    int insertOrderCountTag(@Param("list") List<TagDO> list);

    @Insert("<script>" +
            "INSERT INTO gz_recommend.t_user_tag(user_id, classify, type, divide, tag_id, tag_name)" +
            " SELECT t.ID, t_tag.classify, t_tag.type, t_tag.divide, t.tagId, t_tag.`name`" +
            " FROM (SELECT t_gsyd_user.ID," +
            " CASE" +
            "<foreach collection='list' item='item' index='index'>" +
            "<choose>" +
            "<when test='index != list.size()-1'> WHEN IFNULL(TotalPrice,0) &gt;= #{item.rangeFrom} AND IFNULL(TotalPrice,0) &lt;= #{item.rangeTo} THEN #{item.id}</when>" +
            "<otherwise> ELSE #{item.id}</otherwise>" +
            "</choose>" +
            "</foreach>" +
            " END AS tagId" +
            " FROM ottdb_gsgd.t_gsyd_user" +
            " LEFT JOIN ( SELECT UserID, SUM(Price) AS TotalPrice FROM ottdb_gsgd.t_gsyd_order WHERE `Status` = 1 AND Price > 0 AND CreateTime > DATE_SUB(NOW(),INTERVAL 30 DAY) GROUP BY UserID ) tp ON tp.UserID = t_gsyd_user.IPTVUserID" +
            " WHERE t_gsyd_user.`Status` = 5) t" +
            " LEFT JOIN gz_recommend.t_tag ON t_tag.id = t.tagId" +
            "</script>")
    int insertPayTag(@Param("list") List<TagDO> list);

    @Delete("delete from gz_recommend.t_user_tag where user_id>=#{startId} and user_id<=#{endId}")
    public void deleteRangeIds(@Param("startId") long startId, @Param("endId") long endId);

    @Insert("INSERT INTO gz_recommend.t_tag_daily_summary ( tag_id, tag_name, count, date ) \n" +
            "SELECT\n" +
            "tag_id,\n" +
            "tag_name,\n" +
            "count( 1 ) AS count,\n" +
            "date(DATE_ADD(NOW(),INTERVAL -1 day)) AS date \n" +
            "FROM\n" +
            "gz_recommend.t_user_tag \n" +
            "GROUP BY\n" +
            "tag_id")
    int insertTagDailySummary();


    @Delete("delete from gz_recommend.t_tag_daily_summary where date=date(DATE_ADD(NOW(),INTERVAL -1 day))")
    int deleteTagDailySummary();
}
