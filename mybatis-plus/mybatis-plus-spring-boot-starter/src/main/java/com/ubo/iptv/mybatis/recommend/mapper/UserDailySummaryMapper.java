package com.ubo.iptv.mybatis.recommend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ubo.iptv.mybatis.recommend.entity.UserDailySummaryDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 用户每日小结 Mapper 接口
 * </p>
 *
 * @author gz_recommend
 * @since 2020-11-05
 */
public interface UserDailySummaryMapper extends BaseMapper<UserDailySummaryDO> {

    /**
     * top媒资类型偏好
     *
     * @param userId
     * @param size
     * @return
     */
    @Select("SELECT media_type AS mediaType," +
            " SUM(SQRT(click_count+play_count*3)/(DATEDIFF(CURRENT_DATE,`day`)+1)) AS score" +
            " FROM gz_recommend.t_user_daily_summary" +
            " WHERE user_id=#{userId}" +
            " GROUP BY media_type" +
            " ORDER BY score DESC" +
            " LIMIT 0,#{size}")
    List<UserDailySummaryDO> selectMediaTypeTop(@Param("userId") Integer userId, @Param("size") Integer size);

    /**
     * top媒资题材偏好
     *
     * @param userId
     * @param mediaType
     * @param size
     * @return
     */
    @Select("SELECT media_kind_id AS mediaKindId," +
            " SUM(SQRT(click_count+play_count*3)/(DATEDIFF(CURRENT_DATE,`day`)+1)) AS score" +
            " FROM gz_recommend.t_user_daily_summary" +
            " WHERE user_id=#{userId}" +
            " AND media_type=#{mediaType}" +
            " GROUP BY media_kind_id" +
            " ORDER BY score DESC" +
            " LIMIT 0,#{size}")
    List<UserDailySummaryDO> selectMediaKindTop(@Param("userId") Integer userId, @Param("mediaType") Integer mediaType, @Param("size") Integer size);
}
