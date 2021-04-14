package cn.pukkasoft.datasync.service.impl;

import cn.pukkasoft.datasync.advice.PlatFormEnum;
import cn.pukkasoft.datasync.advice.TimeUtils;
import cn.pukkasoft.datasync.dao.OrderDao;
import cn.pukkasoft.datasync.dao.UserDao;
import cn.pukkasoft.datasync.model.Order;
import cn.pukkasoft.datasync.model.User;
import cn.pukkasoft.datasync.service.OrderService;
import cn.pukkasoft.datasync.vo.GSOrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;


/**
 * OrderServiceImpl
 * @author majia:
 */
@Service("orderService")
public class GSOrderServiceImpl
    implements OrderService{
	
	@Autowired
	private OrderDao orderDao;
	@Autowired
	private UserDao userDao;

	/**
     * 新增
     * @Title: insert
	 * @param GSOrderVo
     * @return Optional<Integer>
     * @throws
     * @Description:
     */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public Integer insert(GSOrderVo GSOrderVo) throws ParseException {
		Order order=setFields(GSOrderVo);
		order.setCreateTime(TimeUtils.parseDate(new Date()));
		order.setUpdateTime(TimeUtils.parseDate(new Date()));
		return orderDao.insert(order);
	}

	private Order setFields(GSOrderVo GSOrderVo) throws ParseException {
		Order order = new Order();
		order.setBillingCode(GSOrderVo.getBillingCode());
		order.setChargingDuration(GSOrderVo.getChargingDuration());
		order.setChargingId(GSOrderVo.getChargingId());
		order.setChargingType(GSOrderVo.getChargingType());
		List<User> users = userDao.selectByByPlatOpreatorAndUserId(PlatFormEnum.getByCode(GSOrderVo.getPlatformOperator()).intEnumValue, GSOrderVo.getUserId());
		if(users.size()>0){
			User user = users.get(0);
			order.setCityNum(user.getCityNum());
			order.setCityNumName(user.getCityNumName());
		}
		order.setContentCode(GSOrderVo.getContentCode());
		order.setContentName(GSOrderVo.getContentName());
		order.setContentProvider(GSOrderVo.getContentProvider());
		order.setContentId(GSOrderVo.getContentId());

		order.setDiscount(GSOrderVo.getDiscount());
		order.setFee(GSOrderVo.getFee());
		order.setFreeMonth(GSOrderVo.getFreeMonth());
		order.setIsForceActivation(GSOrderVo.getIsForceActivation());
		order.setIsLog(GSOrderVo.getIsLog());
		order.setIspaymentHod(GSOrderVo.getIsPaymentHod());
		order.setIsPlan(GSOrderVo.getIsPlan());
		order.setMonth(GSOrderVo.getMonth());
		order.setOrderId(GSOrderVo.getOrderId());
		order.setOrderNumber(GSOrderVo.getOrderNumber());
		order.setOrderTime(TimeUtils.parseDate(GSOrderVo.getOrderTime()));
		order.setPayCode(GSOrderVo.getPayCode());
		order.setPayDesc(GSOrderVo.getPayDesc());
		order.setPayStatus(GSOrderVo.getPayStatus());
		order.setPayType(GSOrderVo.getPayType());
		order.setPlatform(PlatFormEnum.getByCode(GSOrderVo.getPlatformOperator()).intEnumValue);
		order.setPrice(GSOrderVo.getPrice());
		order.setProductCode(GSOrderVo.getProductCode());
		order.setProductId(GSOrderVo.getProductId());
		order.setProductName(GSOrderVo.getProductName());
		order.setRemark(GSOrderVo.getRemark());
		order.setReportTime(TimeUtils.parseDate(GSOrderVo.getReportTime()));
		order.setRequestIp(GSOrderVo.getRequestIp());
		order.setServiceCancelTime(TimeUtils.parseDate(GSOrderVo.getServiceCancelTime()));
		order.setServiceCode(GSOrderVo.getServiceCode());
		order.setServiceId(GSOrderVo.getServiceId());
		order.setServiceName(GSOrderVo.getServiceName());
		order.setServiceType(GSOrderVo.getServiceType());
		order.setSpid(GSOrderVo.getSpId());
		order.setSpname(GSOrderVo.getSpName());
		order.setSpresellerId(GSOrderVo.getSpResellerId());
		order.setSpresellerName(GSOrderVo.getSpResellerName());
		order.setStatus(GSOrderVo.getStatus());
		order.setTmtype(GSOrderVo.getTmType());
		order.setUnOrderTime(TimeUtils.parseDate(GSOrderVo.getUnOrderTime()));
		order.setUserId(GSOrderVo.getUserId());
		order.setUserName(GSOrderVo.getUserName());
		if(!(null==GSOrderVo.getValidEnd()||"".equals(GSOrderVo.getValidEnd()))){
			order.setValidEnd(TimeUtils.parseDate(GSOrderVo.getValidEnd()));
		}else{
			if(GSOrderVo.getStatus().equals("1")){
				order.setValidEnd("2099-12-31 23:59:59");
			}else{
				order.setValidEnd("");
			}
		}
		order.setValidStart(TimeUtils.parseDate(GSOrderVo.getValidStart()));
		return order;
	}

	/**
     * 修改
     * @Title: update
	 * @param GSOrderVo
     * @return Optional<Integer>
     * @throws
     * @Description:
     */
	@Transactional(rollbackFor = Exception.class)
	@Override
    public Integer updateDetailed(GSOrderVo GSOrderVo) throws ParseException {
		Order order=setFields(GSOrderVo);
		order.setUpdateTime(TimeUtils.parseDate(new Date()));
		return orderDao.updateDetailed(order);
	}
	

    
	/**
     * 根据id查询
     * @Title: selectById
     * @param id
     * @return Optional<GSOrderVo>
     * @throws
     * @Description:
     */
	@Override
    public Optional<GSOrderVo> selectById(Long id){
		return null;
	}

    @Override
    public List<Order>  selectByOrderNumber(Integer platForm,String orderNumber) {
		return  orderDao.selectByPlatAndOrderNum(platForm, orderNumber);
    }


}



