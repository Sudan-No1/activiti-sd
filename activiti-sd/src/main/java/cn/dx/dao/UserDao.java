package cn.dx.dao;

import java.util.List;
import java.util.Map;

public interface UserDao {

	Map<String, Object> getUserByUserName(String username);

	List<Map<String, Object>> getClassListByGroupName(String groupName);

	List<String> findNextGroupUsersByUsernameAndBillName(String username, String billName);

	String findGroupLeader(String inputUser);

	List<String> findGroupByGroupName(String group);

	Map<String, Object> findApprover(String unit_code);

	List<String> findGroupUsersByGroupname(String group);

	List<Map<String, Object>> getAssingeeList(String string);

	Map<String, Object> getGroupInfo(String username);

}
