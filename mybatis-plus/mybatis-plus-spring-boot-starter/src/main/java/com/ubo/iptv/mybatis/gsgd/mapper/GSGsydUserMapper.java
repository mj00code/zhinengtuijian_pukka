package com.ubo.iptv.mybatis.gsgd.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ubo.iptv.mybatis.gsgd.entity.GSGsydUserDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author ottdb_gsgd
 * @since 2020-11-17
 */
public interface GSGsydUserMapper extends BaseMapper<GSGsydUserDO> {

    /**
     * 用户列表
     *
     * @param queryWrapper
     * @return
     */
    @Select("SELECT" +
            " pub_area.`name` AS cityNumName," +
            " t_gsyd_user.*" +
            " FROM ottdb_gsgd.t_gsyd_user" +
            " LEFT JOIN ottdb_gsgd.pub_area ON pub_area.`code`=t_gsyd_user.CityNum" +
            " ${ew.customSqlSegment}")
    List<GSGsydUserDO> selectListWithCity(@Param("ew") Wrapper queryWrapper);
}
