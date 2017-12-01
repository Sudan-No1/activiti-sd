package cn.dx.service;

import java.util.Map;

public interface UserService {

	Map<String,Object> findUserByUsername(String username);

	Map<String, Object> getGroupInfo(String username);

}
