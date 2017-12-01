package cn.dx.service;

import java.util.List;
import java.util.Map;

import cn.dx.domain.PageBean;

public interface FileService {

	void addFileInfo(String fileName, String path);

	Map<String,Object> getFilePathByFileName(String fileName, String version);

	List<Map<String, Object>> getFileInfoList();

	PageBean<Map<String, Object>> getFileInfoListPage(Integer pageNum, Integer pageSize);

	int getFileSize(String fileName);

	PageBean<Map<String, Object>> getFileInfoListPage(Integer pageNum, Integer pageSize, String fileName);

}
