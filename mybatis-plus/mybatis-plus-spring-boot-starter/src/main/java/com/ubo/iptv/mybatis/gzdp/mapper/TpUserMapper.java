package com.ubo.iptv.mybatis.gzdp.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;

import com.ubo.iptv.mybatis.gzdp.entity.TpUserDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 用户 Mapper 接口
 * </p>
 *
 * @author gzdp
 * @since 2021-03-08
 */
public interface TpUserMapper extends BaseMapper<TpUserDO> {
    /**
     * 用户列表
     *
     * @param queryWrapper
     * @return
     */
    @Select("SELECT" +
            " tp_user.*" +
            " FROM gzdp.tp_user")
    List<TpUserDO> selectListWithCity(@Param("ew") Wrapper queryWrapper);

}
