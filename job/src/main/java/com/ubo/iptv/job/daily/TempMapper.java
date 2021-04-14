package com.ubo.iptv.job.daily;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ubo.iptv.mybatis.recommend.entity.UserDailySummaryDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author: xuning
 * @Date: 2020-11-05
 */
public interface TempMapper extends BaseMapper {

    /**
     * top5媒资类型偏好
     *
     * @param sysId
     * @param userId
     * @return
     */
    @Select("SELECT" +
            " media_type AS mediaType," +
            " SUM(SQRT(click_count+play_count*3)/(DATEDIFF(CURRENT_DATE,`day`)+1)) AS score" +
            " FROM gz_recommend.t_user_daily_summary" +
            " WHERE sys_id=#{sysId}" +
            " AND user_id=#{userId}" +
            " group by media_type" +
            " ORDER BY score DESC" +
            " LIMIT 0,5")
    List<UserDailySummaryDO> selectTopMediaType(@Param("sysId") String sysId, @Param("userId") Integer userId);

    /**
     * top3媒资题材偏好
     *
     * @param sysId
     * @param userId
     * @param mediaType
     * @return
     */
    @Select("SELECT" +
            " media_kind_id AS mediaTypeKind," +
            " SUM(SQRT(click_count+play_count*3)/(DATEDIFF(CURRENT_DATE,`day`)+1)) AS score" +
            " FROM gz_recommend.t_user_daily_summary" +
            " WHERE sys_id=#{sysId}" +
            " AND user_id=#{userId}" +
            " AND media_type=#{mediaType}" +
            " group by media_kind_id" +
            " ORDER BY score DESC" +
            " LIMIT 0,3")
    List<UserDailySummaryDO> selectTopMediaKind(@Param("sysId") String sysId, @Param("userId") Integer userId, @Param("mediaType") Integer mediaType);
}
