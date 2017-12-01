package cn.dx.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.dx.dao.UserDao;

@Repository
public class UserDaoImpl implements UserDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public Map<String, Object> getUserByUserName(String username) {
		return jdbcTemplate.queryForMap("select * from \"CTUser\" where \"Status\" = 'A' and \"USER_LOGIN_NAME\"=?",username);
	}


	@Override
	public List<Map<String, Object>> getClassListByGroupName(String groupName) {
		return jdbcTemplate.queryForList("select table_name,\"Mode\" from \"view_new_role_table\" where  group_name = ?",groupName);
	}

	@Override
	public List<String> findNextGroupUsersByUsernameAndBillName(String username,String billName) {
		String sql = "select user2 from view_group_relation where user1 = ? and \"ProcdefId\" = ?;"; 
		return jdbcTemplate.queryForList(sql,String.class,username,billName);
	}

	@Override
	public String findGroupLeader(String inputUser) {
		String sql = "select leader from view_user_leader where employee = ?";
		return jdbcTemplate.queryForObject(sql, String.class,inputUser);
	}

	@Override
	public List<String> findGroupByGroupName(String group) {
		String sql = " select \"User\" from view_get_user_by_group where \"Role\" = ?;";
		return jdbcTemplate.queryForList(sql,String.class,group);
	}

	@Override
	public Map<String, Object> findApprover(String unit_code) {
		String sql = "select * from ur where \"department\" = ? and \"Status\" = 'A';";
		return jdbcTemplate.queryForMap(sql,unit_code);
	}

	@Override
	public List<String> findGroupUsersByGroupname(String group) {
		String sql = "select * from ur where \"department\" = ? and \"Status\" = 'A';";
		return jdbcTemplate.queryForList(sql,String.class,group);
	}

	@Override
	public List<Map<String, Object>> getAssingeeList(String string) {
		String sql = "select * from \"UserTask\" where \"Description\" = ? and \"Status\" = 'A';";
		return jdbcTemplate.queryForList(sql,string);
	}

	@Override
	public Map<String, Object> getGroupInfo(String username) {
		String sql = "select * from view_ctgroup where loginname = ?;";
		return jdbcTemplate.queryForMap(sql,username);
	}

}
