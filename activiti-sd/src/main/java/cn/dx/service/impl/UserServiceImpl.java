package cn.dx.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.dx.dao.UserDao;
import cn.dx.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	private final Logger log = LoggerFactory.getLogger(getClass().getName());

	@Autowired
	private UserDao userDao;
	
	@Override
	public Map<String,Object> findUserByUsername(String username) {
		Map<String, Object> map = null;
		try {
			map = userDao.getUserByUserName(username);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return map;
	}

	@Override
	public List<String> findGroup(String username) {
		List<String> list = null;
		try {
			list = userDao.findGroup(username);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<Map<String, Object>> findClasList(String groupName) {
		List<Map<String, Object>> list = null;
		try {
			list = userDao.getClassListByGroupName(groupName);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return list;
	}
}
