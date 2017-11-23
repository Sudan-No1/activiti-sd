package cn.dx.dao;

import java.util.List;
import java.util.Map;

public interface UserDao {

	Map<String, Object> getUserByUserName(String username);

	List<String> findGroup(String username);

	List<Map<String, Object>> getClassListByGroupName(String groupName);

	List<String> findNextGroupUsersByUsernameAndBillName(String username, String billName);

	String findGroupLeader(String inputUser);

	List<String> findGroupByGroupName(String group);

}
