package cn.pukkasoft.datasync.service.impl;

import cn.pukkasoft.datasync.dao.TProgramMapper;
import cn.pukkasoft.datasync.model.TProgram;
import cn.pukkasoft.datasync.service.ITProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author bimgr
 * @date 2020-12-02
 */
@Service
public class TProgramServiceImpl implements ITProgramService {
    @Autowired
    private TProgramMapper tProgramMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public TProgram selectTProgramById(Long id) {
        return tProgramMapper.selectTProgramById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param tProgram 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<TProgram> selectTProgramList(TProgram tProgram) {
        return tProgramMapper.selectTProgramList(tProgram);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param tProgram 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertTProgram(TProgram tProgram) {
        return tProgramMapper.insertTProgram(tProgram);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param tProgram 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateTProgram(TProgram tProgram) {
        return tProgramMapper.updateTProgram(tProgram);
    }

    /**
     * 删除【请填写功能名称】对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
//    @Override
//    public int deleteTProgramByIds(String ids) {
//        return tProgramMapper.deleteTProgramByIds(Convert.toStrArray(ids));
//    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteTProgramById(Long id) {
        return tProgramMapper.deleteTProgramById(id);
    }
}
