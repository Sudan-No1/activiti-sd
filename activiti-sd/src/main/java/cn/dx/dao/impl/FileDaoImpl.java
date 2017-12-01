package cn.dx.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.dx.dao.FileDao;

@Repository
public class FileDaoImpl implements FileDao {

	@Autowired
	private JdbcTemplate jdbcTempalte;
	
	@Override
	public int queryVersionByFileName(String fileName) {
		String sql = "select count(1) from \"Attachment\" where \"FileName\" = ? and \"Status\" = 'A'";
		return jdbcTempalte.queryForObject(sql, Integer.class, fileName);
	}

	@Override
	public void addFileInfo(String fileName, String path, String version) {
		String sql = "insert into \"Attachment\"(\"Description\",\"FileName\",\"Version\",\"Date\",\"Path\")"
				+ " values(?,?,?,now(),?)";
		Object[] obj = new Object[4];
		obj[0] = fileName+"_"+version;
		obj[1] = fileName;
		obj[2] = version;
		obj[3] = path;
		jdbcTempalte.update(sql,obj);
	}

	@Override
	public Map<String, Object> queryFileInfoByFileNameAndVersion(String fileName, String version) {
		String sql = "select * from \"Attachment\" where \"FileName\" = ? and \"Version\" = ? and \"Status\" = 'A'";
		return jdbcTempalte.queryForMap(sql,fileName,version);
	}

	@Override
	public List<Map<String, Object>> getFileInfoList() {
		String sql = "select * from \"Attachment\" where \"Status\" = 'A'";
		return jdbcTempalte.queryForList(sql);
	}

	@Override
	public int getFileListSize() {
		String sql = "select count(1) from \"Attachment\" where \"Status\" = 'A';";
		return jdbcTempalte.queryForObject(sql, Integer.class);
	}

	@Override
	public List<Map<String, Object>> getFileListByPage(int startIndex, Integer pageSize) {
		String sql = "select * from \"Attachment\" where \"Status\" = 'A' offset ? limit ? ;";
		return jdbcTempalte.queryForList(sql,startIndex,pageSize);
	}

	@Override
	public int getFileListSizeByFileName(String fileName) {
		String sql = "select count(1) from \"Attachment\" where \"Status\" = 'A' and \"FileName\" ~ ?;";
		return jdbcTempalte.queryForObject(sql, Integer.class,fileName);
	}

	@Override
	public List<Map<String, Object>> getFileListByPage(int startIndex, Integer pageSize, String fileName) {
		String sql = "select * from \"Attachment\" where \"Status\" = 'A' and \"FileName\" ~ ? offset ? limit ? ;";
		return jdbcTempalte.queryForList(sql,fileName,startIndex,pageSize);
	}

}
