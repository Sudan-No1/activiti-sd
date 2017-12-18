package cn.dx.dao;

import java.util.List;
import java.util.Map;

public interface CmdbDao {

	List<Map<String, Object>> getRoomList();
	
	List<Map<String, Object>> getRoomList(String condition);

	String getSerialNumber(String year, String billName);

	Integer addCTUser(Object[] params);

	Integer updateCTUser(Object[] params);

	Integer deleteCTUser(String loginname);

}
