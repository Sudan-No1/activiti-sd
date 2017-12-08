package cn.dx.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import cn.dx.domain.PageBean;
import cn.dx.service.FileService;

@Controller
@RequestMapping("/fileController")
public class FileController {

	@Autowired
	private FileService fileService;

	@RequestMapping("/search")
	@ResponseBody
	public PageBean<Map<String, Object>> getFileList(Model model, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
		PageBean<Map<String, Object>> list = fileService.getFileInfoListPage(pageNum, pageSize);
		model.addAttribute("fileList", list);
		return list;
	}
	
	@RequestMapping("/fileList")
	@ResponseBody
	public PageBean<Map<String, Object>> getFileListByFileName(Model model, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
			@RequestParam(value = "fileName",defaultValue= "") String fileName) {
		PageBean<Map<String, Object>> list = null;
		if("".equals(fileName)){
			list = fileService.getFileInfoListPage(pageNum, pageSize);
		}else{
			list = fileService.getFileInfoListPage(pageNum, pageSize,fileName);
		}
		model.addAttribute("fileList", list);
		return list;
	}
	
	
	@RequestMapping("/file")
	public String toFile(Map<String, Object> map,
			@RequestParam(value = "fileName",defaultValue= "") String fileName){
		int count = fileService.getFileSize(fileName);
		map.put("total", count);
		map.put("fileName", fileName);
		return "/index";
	}

	@RequestMapping("/toDownload")
	public String toDownloadPage() {
		return "/download";
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public String uploadFile(@RequestParam(value = "file") MultipartFile file, HttpServletRequest request)
			throws Exception {
		if (file.isEmpty()) {
			return "redirect:/file";
		}
		String fileName = file.getOriginalFilename();
		String suffixName = fileName.substring(fileName.lastIndexOf("."));
		String filePath = "C:/Sudan_ 管/upload";
		String path = filePath + UUID.randomUUID() + suffixName;
		fileService.addFileInfo(fileName, path);
		File dest = new File(path);
		if (!dest.getParentFile().exists()) {
			dest.getParentFile().mkdirs();
		}
		try {
			file.transferTo(dest);
			return "redirect:file";
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "redirect:file";
	}

	@RequestMapping(value = "/download", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<InputStreamResource> downloadFile(@PathParam(value = "fileName") String fileName,
			@PathParam(value = "version") String version, HttpServletRequest request) throws Exception {
		Map<String, Object> fileInfo = fileService.getFilePathByFileName(fileName, version);
		String filePath = (String) fileInfo.get("Path");
		FileSystemResource file = new FileSystemResource(filePath);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(fileName, "UTF-8") + "\"");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");
		return ResponseEntity.ok().headers(headers).contentLength(file.contentLength())
				.contentType(MediaType.parseMediaType("application/octet-stream"))
				.body(new InputStreamResource(file.getInputStream()));

	}
	
	@RequestMapping(value = "/downloadAttachment", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<InputStreamResource> downloadFileByPath(@RequestParam(value = "Attachment") String fileName,
			@RequestParam(value = "Path") String path,HttpServletRequest request) throws Exception {
		FileSystemResource file = new FileSystemResource(path);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(fileName, "UTF-8") + "\"");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");
		return ResponseEntity.ok().headers(headers).contentLength(file.contentLength())
				.contentType(MediaType.parseMediaType("application/octet-stream"))
				.body(new InputStreamResource(file.getInputStream()));

	}
}
