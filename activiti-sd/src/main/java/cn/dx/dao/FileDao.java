package cn.dx.dao;

import java.util.List;
import java.util.Map;

public interface FileDao {

	int queryVersionByFileName(String fileName);

	void addFileInfo(String fileName, String path, String version);

	Map<String, Object> queryFileInfoByFileNameAndVersion(String fileName, String version);

	List<Map<String, Object>> getFileInfoList();

	int getFileListSize();

	List<Map<String, Object>> getFileListByPage(int startIndex, Integer pageSize);

	int getFileListSizeByFileName(String fileName);

	List<Map<String, Object>> getFileListByPage(int startIndex, Integer pageSize, String fileName);

}
