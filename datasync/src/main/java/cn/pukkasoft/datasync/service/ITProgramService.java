package cn.pukkasoft.datasync.service;

import cn.pukkasoft.datasync.model.TProgram;

import java.util.List;

/**
 * 【请填写功能名称】Service接口
 *
 * @author bimgr
 * @date 2020-12-02
 */
public interface ITProgramService {
    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    public TProgram selectTProgramById(Long id);

    /**
     * 查询【请填写功能名称】列表
     *
     * @param tProgram 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<TProgram> selectTProgramList(TProgram tProgram);

    /**
     * 新增【请填写功能名称】
     *
     * @param tProgram 【请填写功能名称】
     * @return 结果
     */
    public int insertTProgram(TProgram tProgram);

    /**
     * 修改【请填写功能名称】
     *
     * @param tProgram 【请填写功能名称】
     * @return 结果
     */
    public int updateTProgram(TProgram tProgram);

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
//    public int deleteTProgramByIds(String ids);

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteTProgramById(Long id);
}
