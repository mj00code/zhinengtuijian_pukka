package cn.pukkasoft.datasync.service;

import cn.pukkasoft.datasync.model.Order;
import cn.pukkasoft.datasync.vo.GSOrderVo;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

/**
 * OrderService
 * @author TODO:
 */
public interface OrderService{

	/**
     * 新增
     * @Title: insert
	 * @param GSOrderVo
     * @return Optional<Integer>
     * @throws
     * @Description:
     */
	Integer insert(GSOrderVo GSOrderVo) throws ParseException;
    
	/**
     * 修改
     * @Title: update
	 * @param GSOrderVo
     * @return Optional<Integer>
     * @throws
     * @Description:
     */
    Integer updateDetailed(GSOrderVo GSOrderVo) throws ParseException;
	

    
	/**
     * 根据id查询
     * @Title: selectById
     * @param id
     * @return Optional<GSOrderVo>
     * @throws
     * @Description:
     */
    Optional<GSOrderVo> selectById(Long id);


	List<Order>  selectByOrderNumber(Integer platForm, String orderNumber);
}



