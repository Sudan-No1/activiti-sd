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
		return jdbcTemplate.queryForMap("select * from \"User\" where \"Status\" = 'A' and \"Username\"=?",username);
	}

	@Override
	public List<String> findGroup(String username) {
		return jdbcTemplate.queryForList("select _new_get_role_list_by_user(?)",new Object[]{username},String.class);
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

}
