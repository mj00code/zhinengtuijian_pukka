package cn.pukkasoft.datasync.dao;

import cn.pukkasoft.datasync.model.User;

import org.apache.ibatis.annotations.Param;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * UserDao
 * @author TODO:
 *
 */
@Mapper
public interface UserDao{
	
	/**
     * 新增
     * @Title: insert
	 * @param user
     * @return Integer
     * @throws
     * @Description:
     */
	Integer insert(User user);
    
	/**
     * 修改
     * @Title: update
	 * @param user
     * @return Integer
     * @throws
     * @Description:
     */
    Integer update(User user);
	
	/**
     * 修改明细（非空判断）
     * @Title: updateDetailed
	 * @param user
     * @return Integer
     * @throws
     * @Description:
     */
    Integer updateDetailed(User user);
    
	/**
     * 根据id查询
     * @Title: selectById
     * @param id
     * @return User
     * @throws
     * @Description:
     */
    User selectById(Long id);

	List<User> selectByByPlatOpreatorAndUserId(@Param("platformOperator") Integer platformOperator, @Param("userId") String userId);
}
