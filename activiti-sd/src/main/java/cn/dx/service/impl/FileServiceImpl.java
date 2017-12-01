package cn.dx.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.dx.dao.FileDao;
import cn.dx.domain.PageBean;
import cn.dx.service.FileService;

@Service
public class FileServiceImpl implements FileService {

	@Autowired
	private FileDao fileDao;
	
	@Override
	public void addFileInfo(String fileName, String path) {
		try {
			int	count = fileDao.queryVersionByFileName(fileName);
			fileDao.addFileInfo(fileName,path,count+1+"");
		} catch (Exception e) {
			fileDao.addFileInfo(fileName,path,"1");
		}
	}

	@Override
	public Map<String,Object> getFilePathByFileName(String fileName, String version) {
		if("".equals(version)){
			version = fileDao.queryVersionByFileName(fileName)-1+"";
		}
		return fileDao.queryFileInfoByFileNameAndVersion(fileName,version);
	}

	@Override
	public List<Map<String, Object>> getFileInfoList() {
		return fileDao.getFileInfoList();
	}

	@Override
	public PageBean<Map<String, Object>> getFileInfoListPage(Integer pageNum, Integer pageSize) {
		int totalRecord = fileDao.getFileListSize();
		PageBean<Map<String, Object>> pb = new PageBean<>(pageNum, pageSize, totalRecord);
		int startIndex = pb.getStartIndex();
		List<Map<String, Object>> fileList = fileDao.getFileListByPage(startIndex, pageSize);
		pb.setList(fileList);
		return pb;
	}

	@Override
	public int getFileSize(String fileName) {
		if(fileName == null){
			return fileDao.getFileListSize();
		}
		return fileDao.getFileListSizeByFileName(fileName);
	}

	@Override
	public PageBean<Map<String, Object>> getFileInfoListPage(Integer pageNum, Integer pageSize, String fileName) {
		int totalRecord = fileDao.getFileListSizeByFileName(fileName);
		PageBean<Map<String, Object>> pb = new PageBean<>(pageNum, pageSize, totalRecord);
		int startIndex = pb.getStartIndex();
		List<Map<String, Object>> fileList = fileDao.getFileListByPage(startIndex, pageSize,fileName);
		pb.setList(fileList);
		return pb;
	}

}
