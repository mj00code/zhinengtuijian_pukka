package cn.pukkasoft.datasync.dao;

import cn.pukkasoft.datasync.model.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * OrderDao
 * @author TODO:
 *
 */
@Mapper
public interface OrderDao{
	
	/**
     * 新增
     * @Title: insert
	 * @param order
     * @return Integer
     * @throws
     * @Description:
     */
	Integer insert(Order order);
    
	/**
     * 修改
     * @Title: update
	 * @param order
     * @return Integer
     * @throws
     * @Description:
     */
    Integer update(Order order);
	
	/**
     * 修改明细（非空判断）
     * @Title: updateDetailed
	 * @param order
     * @return Integer
     * @throws
     * @Description:
     */
    Integer updateDetailed(Order order);
    
	/**
     * 根据id查询
     * @Title: selectById
     * @param id
     * @return Order
     * @throws
     * @Description:
     */
    Order selectById(Long id);

	/**
	 * 删除根据id
	 * @Title: deleteById
	 * @param  id
	 * @return Integer
	 * @throws
	 * @Description:
	 */
	Integer deleteById(@Param("id") Long id);

	/**
	 * 根据id集合删除
	 *
	 * @param ids id集合
	 * @return 删除总数
	 */
	Integer deleteByIds(@Param("ids") List<Long> ids);

	List<Order> selectByPlatAndOrderNum(@Param("platForm") Integer platForm,@Param("orderNumber") String orderNumber);
}
