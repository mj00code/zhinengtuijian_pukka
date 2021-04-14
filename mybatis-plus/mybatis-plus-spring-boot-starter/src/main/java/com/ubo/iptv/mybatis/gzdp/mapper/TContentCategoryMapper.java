package com.ubo.iptv.mybatis.gzdp.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.ubo.iptv.mybatis.gzdp.entity.TContentCategoryDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 内容与栏目关系 Mapper 接口
 * </p>
 *
 * @author gzdp
 * @since 2021-03-08
 */
public interface TContentCategoryMapper extends BaseMapper<TContentCategoryDO> {

    @Select("SELECT t_content_category.*" +
            " FROM gzdp.t_category" +
            " LEFT JOIN gzdp.t_content_category ON t_category.`code` = t_content_category.category_code" +
            " ${ew.customSqlSegment}")
    List<TContentCategoryDO> selectContentList(@Param("ew") Wrapper queryWrapper);
}
