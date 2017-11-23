package cn.dx.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.dx.dao.RoomBillDao;

@Repository
public class RoomBillDaoImpl implements RoomBillDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public List<Map<String, Object>> findRoomBillList() {
		String sql = "select * from \"RoomBill\" where \"Status\" = 'A';";
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public void saveRoomBill(Object[] params) {
		String sql = "insert into \"RoomBill\"(\"Username\",\"Room\",\"Url\",\"BillStatus\")"
				+ " values(?,?,?,?)";
		jdbcTemplate.update(sql,params);
	}

	@Override
	public Map<String, Object> findRoomBillById(Long id) {
		String sql = "select * from \"RoomBill\" where \"Id\" = ? and \"Status\" = 'A';";
		return jdbcTemplate.queryForMap(sql,id);
	}

	@Override
	public void deleteRoomBillById(Long id) {
		String sql = "delete from  \"RoomBill\" where \"Id\" = ? and \"Status\" = 'A';";
		jdbcTemplate.update(sql,id);
	}

}
