package com.ubo.iptv.mybatis.gsgd.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ubo.iptv.mybatis.gsgd.entity.GSContentCatalogDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author ottdb_gsgd
 * @since 2021-01-14
 */
public interface GSContentCatalogMapper extends BaseMapper<GSContentCatalogDO> {

    /**
     * ID作ContentID使用
     * Sequence作ContentType使用
     *
     * @param queryWrapper
     * @return
     */
    @Select("SELECT t_contentinfo.ContentID AS ID," +
            " t_contentinfo.ContentType AS Sequence," +
            " t_content_catalog.*" +
            " FROM ottdb_gsgd.t_content_catalog" +
            " LEFT JOIN ottdb_gsgd.t_contentinfo ON t_contentinfo.ID=t_content_catalog.ContentinfoID" +
            " ${ew.customSqlSegment}")
    List<GSContentCatalogDO> selectListWithContent(@Param("ew") Wrapper queryWrapper);
}
