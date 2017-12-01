package cn.dx.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.dx.dao.CmdbDao;
import cn.dx.service.CmdbService;

@Service
public class CmdbServiceImpl implements CmdbService {

	@Autowired
	private CmdbDao cmdbDao;
	
	@Override
	public List<Map<String, Object>> getRoomList() {
		return cmdbDao.getRoomList();
	}

	@Override
	public List<Map<String, Object>> getRoomList(String condition) {
		return cmdbDao.getRoomList(condition);
	}

	@Override
	public String getSerialNumber(String year, String billName) {
		return cmdbDao.getSerialNumber(year,billName);
	}

}
