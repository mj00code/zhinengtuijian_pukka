package cn.pukkasoft.datasync.service.impl;

import cn.pukkasoft.datasync.advice.PlatFormEnum;
import cn.pukkasoft.datasync.advice.TimeUtils;
import cn.pukkasoft.datasync.dao.UserDao;
import cn.pukkasoft.datasync.model.User;
import cn.pukkasoft.datasync.service.UserService;
import cn.pukkasoft.datasync.vo.GSUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.Date;
import java.util.List;


/**
 * GSUserServiceImpl
 * @author majia:
 */
@Service("userService")
public class GSUserServiceImpl implements UserService{
	
	@Autowired
	private UserDao userDao;



	/**
     * 新增
     * @Title: insert
	 * @param GSUserVo
     * @return Optional<Integer>
     * @throws
     * @Description:
     */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public Integer insert(GSUserVo GSUserVo) throws ParseException {
		User user = setFields(GSUserVo);
		user.setCreatetime(TimeUtils.parseDate(new Date()));
		user.setUpdatetime(TimeUtils.parseDate(new Date()));
		return userDao.insert(user);
	}

	private User setFields(GSUserVo GSUserVo) throws ParseException {
		User user = new User();
		user.setActivetime(TimeUtils.parseDate(GSUserVo.getDateCreated()));
		user.setAreaNum(GSUserVo.getAreaNum());
		user.setChannelNum(GSUserVo.getChannelNum());
		user.setCityNum(GSUserVo.getCityNum());
//		user.setCityNumName(AreaCodeEnum.getByCode(GSUserVo.getCityNum()).getName());
		user.setCorrelateId(GSUserVo.getCorrelateId());

		user.setDeviceNum(GSUserVo.getDeviceNum());
		user.setIptvuserId(GSUserVo.getUserId());
		user.setJuXiangNum(GSUserVo.getJuXiangNum());
		user.setOpnum(GSUserVo.getOpNum());
		user.setPeopleNum(GSUserVo.getPeopleNum());
		user.setPlatform(PlatFormEnum.getByCode(GSUserVo.getPlatformOperator()).intEnumValue);
		user.setServerId(GSUserVo.getServerId());
		user.setSn(GSUserVo.getSn());
		user.setSpnum(GSUserVo.getSpNum());
		user.setStatus(GSUserVo.getUserStatus());
		user.setTimestamp(GSUserVo.getTimestamp());

		user.setUserGroupId(GSUserVo.getUserGroupId());
		user.setUserGroupName(GSUserVo.getUserGroupName());
		return user;
	}

	/**
     * 修改
     * @Title: update
	 * @param GSUserVo
     * @return Optional<Integer>
     * @throws
     * @Description:
     */
	@Transactional(rollbackFor = Exception.class)
	@Override
    public Integer updateDetailed(GSUserVo GSUserVo) throws ParseException {
		User user = setFields(GSUserVo);
		user.setUpdatetime(TimeUtils.parseDate(new Date()));
		return userDao.updateDetailed(user);
	}
	


    @Override
    public boolean selectByPlatOpreatorAndUserId(Integer platformOperator, String userId) {
		List<User> users = userDao.selectByByPlatOpreatorAndUserId(platformOperator, userId);

		return users.size()>0?true:false;

	}


}



