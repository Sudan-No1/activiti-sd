package cn.dx.service;

import java.util.List;
import java.util.Map;

public interface UserService {

	Map<String,Object> findUserByUsername(String username);

	List<String> findGroup(String username);

	List<Map<String, Object>> findClasList(String groupName);

}
