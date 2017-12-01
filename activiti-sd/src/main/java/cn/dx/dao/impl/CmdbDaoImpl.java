package cn.dx.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.dx.dao.CmdbDao;

@Repository
public class CmdbDaoImpl implements CmdbDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public List<Map<String, Object>> getRoomList() {
		String sql = "select * from \"Room\" where \"Status\" = 'A';";
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> getRoomList(String condition) {
		String sql = "select * from \"Room\" where (\"Number\" ~? "
				+ "or \"Number\" ~? "
				+ "or \"BuildingNumber\" ~? "
				+ "or \"FloorNmuber\" ~? "
				+ "or \"RoomNumber\" ~? "
				+ "or \"RoomLocation\" ~? "
				+ "or \"OwnershipCompany\" ~? "
				+ "or \"RentDepartment\" ~?) and \"Status\" = 'A';";
		return jdbcTemplate.queryForList(sql,new Object[]{condition,condition,
				condition,condition,condition,condition,condition,condition});
	}

	@Override
	public String getSerialNumber(String year, String billName) {
		String sql = "select \"Description\" from \"Bill\" where \"Description\" ~ ? and \"Status\" = 'A' order by \"Id\" desc limit 1";
		return jdbcTemplate.queryForObject(sql, String.class,year+billName);
	}

}
