package cn.dx.service;

import java.util.List;
import java.util.Map;

public interface CmdbService {

	List<Map<String, Object>> getRoomList();

	List<Map<String, Object>> getRoomList(String condition);

	String getSerialNumber(String year, String billName);

}
