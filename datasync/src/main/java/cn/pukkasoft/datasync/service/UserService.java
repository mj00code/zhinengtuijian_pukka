package cn.pukkasoft.datasync.service;

import cn.pukkasoft.datasync.vo.GSUserVo;

import java.text.ParseException;
import java.util.Optional;

/**
 * UserService
 * @author TODO:
 */
public interface UserService{

	/**
     * 新增
     * @Title: insert
	 * @param GSUserVo
     * @return Optional<Integer>
     * @throws
     * @Description:
     */
	Integer insert(GSUserVo GSUserVo) throws ParseException;
    
	/**
     * 修改
     * @Title: update
	 * @param GSUserVo
     * @return Optional<Integer>
     * @throws
     * @Description:
     */
    Integer updateDetailed(GSUserVo GSUserVo) throws ParseException;



    boolean selectByPlatOpreatorAndUserId(Integer platformOperator, String userId);
}



