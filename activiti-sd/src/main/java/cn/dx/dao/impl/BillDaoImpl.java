package cn.dx.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.dx.dao.BillDao;

@Repository
public class BillDaoImpl implements BillDao{

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public Map<String, Object> findBillById(String billName, Long id) {
		String sql = "select * from \""+billName+"\" where \"Id\" = ?";
		return jdbcTemplate.queryForMap(sql, id);
	}

	@Override
	public void updateBillState(String billName,Map<String, Object> bill) {
		int id = (int)bill.get("Id");
		String status = (String)bill.get("ProcessStatus");
		String sql = "update \""+billName+"\" set \"ProcessStatus\" = ? where \"Id\" = ? ;";
		jdbcTemplate.update(sql, status, id);
	}

	@Override
	public void addBill(String billName, Map<String, Object> params) {
		Collection<String> keys = params.keySet();
		StringBuilder sql = new StringBuilder();
		StringBuilder values = new StringBuilder(" values(");
		sql.append("insert into \"").append(billName).append("\"(");
		for (String key : keys) {
			sql.append("\"")
				.append(key)
				.append("\",");
			values.append("'")
				.append(params.get(key))
				.append("',");
		}
		values.append(");");
		String finalSql = sql.append(")").append(values).toString().replace(",)", ")");
		jdbcTemplate.update(finalSql);
	}

	@Override
	public List<Map<String, Object>> findBillListByUser(String username) {
		String sql = "select * from \"Bill\" where \"loginname\" = ? and \"Status\" = 'A' and \"ProcessStatus\" != '流程结束';";
		return jdbcTemplate.queryForList(sql, username);
	}

	@Override
	public void deleteBillByIdAndBillName(String id, String billName) {
		String sql = "update \""+billName+"\" set \"Status\" = 'N' where \"Id\" = ?;";
		jdbcTemplate.update(sql, Integer.parseInt(id));
		
	}

	@Override
	public Map<String, Object> findBillByIdAndBillName(String id, String billName) {
		String sql = "select * from \""+billName+"\" where \"Id\" = ? and \"Status\" = 'A';";
		return jdbcTemplate.queryForMap(sql,Integer.parseInt(id));
	}

	@Override
	public void update(String billName, Map<String, Object> params) {
		Collection<String> keys = params.keySet();
		String id =  (String)params.get("Id");
		params.remove("Id");
		StringBuilder sql = new StringBuilder();
		sql.append("update \"").append(billName).append("\" set ");
		List<Object> list = new ArrayList<>();
		for (String key : keys) {
			sql.append("\"")
				.append(key)
				.append("\" = ?,");
			list.add(params.get(key));
		}
		list.add(Integer.parseInt(id));
		String finalsql = sql.append(" where \"Id\" = ?").toString().replace(", where", " where");
		jdbcTemplate.update(finalsql,list.toArray());
		
	}

	@Override
	public Long findBillIdByDescription(String billName, String description) {
		String sql = "select \"Id\" from \""+billName+"\" where \"Description\" = ? and \"Status\" = 'A';";
		return jdbcTemplate.queryForObject(sql, Long.class,description);
	}

	@Override
	public int getTotalBillListByUser(String username) {
		String sql = "select count(1) from \"Bill\" where \"loginname\" = ? and \"Status\" = 'A' and \"ProcessStatus\" != '流程结束';";
		return jdbcTemplate.queryForObject(sql, Integer.class,username);
	}

	@Override
	public List<Map<String, Object>> findPageBillListByUser(String username, int startIndex, Integer pageSize) {
		String sql = "select * from \"Bill\" where \"loginname\" = ? and \"Status\" = 'A' and \"ProcessStatus\" != '流程结束' offset ? limit ?;";
		return jdbcTemplate.queryForList(sql,new Object[]{username,startIndex,pageSize});
	}

}
