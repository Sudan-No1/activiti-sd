package cn.dx.controller;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import cn.dx.domain.PageBean;
import cn.dx.form.WorkflowBean;
import cn.dx.service.BillService;
import cn.dx.service.CmdbService;
import cn.dx.service.UserService;
import cn.dx.service.WorkflowService;
import cn.dx.utils.UserUtil;

@Controller
@RequestMapping("/billController")
public class BillController {

	@Autowired
	private BillService billService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CmdbService cmdbService;
	
	@Autowired
	private WorkflowService workflowService;
	
	@RequestMapping(value="/addBill",method=RequestMethod.POST)
	public String addBill(@RequestParam(value = "file") MultipartFile file,HttpServletRequest request){
		String path = null;
		String fileName = null;
		if (!file.isEmpty()) {
			fileName = file.getOriginalFilename();
			String suffixName = fileName.substring(fileName.lastIndexOf("."));
			String filePath = "C:/Sudan_资管/upload/";
			path = filePath + UUID.randomUUID() + suffixName;
			File dest = new File(path);
			if (!dest.getParentFile().exists()) {
				dest.getParentFile().mkdirs();
			}
			try {
				file.transferTo(dest);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		HttpSession session = request.getSession();
		Map<String ,String[]> map=request.getParameterMap();
		map.put("Attachment", new String[]{fileName});
		map.put("Path", new String[]{path});
		Map<String, Object> user = UserUtil.getUserFromSession(request.getSession());
		String username = (String)user.get("USER_LOGIN_NAME");
		billService.addBill(username,map);
		String billName = map.get("billName")[0];
		String description = map.get("Description")[0];
		Long id = billService.findBillIdByDescription(billName, description);
		WorkflowBean workflowBean = new WorkflowBean();
		workflowBean.setBillName(billName);
		workflowBean.setId(id);
		workflowBean.setOutcome("提交申请");;
		workflowService.saveStartProcess(workflowBean,session);
		return "/bill/list";
	}
	
	@RequestMapping(value="/queryBill",method=RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> queryBill(@RequestParam("businessKey")String businessKey,HttpServletRequest request){
		String[] split = businessKey.split("\\.");
    	if(split.length >1){
    		String billName = split[0];
    		Long id  = Long.parseLong(split[1]);
    		return billService.findBillById(billName,id);
    	}
    	return null;
	}
	
	@RequestMapping("/{billName}/add")
	public String addBill(@PathVariable("billName") String billName,
			@RequestParam("billDescription")String billDescription,
			HttpServletRequest request,
			Model model){
		Map<String, Object> user = UserUtil.getUserFromSession(request.getSession());
		String realName = (String)user.get("REAL_NAME");
		String username = (String)user.get("USER_LOGIN_NAME");
		Map<String,Object> map = userService.getGroupInfo(username);
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy");
		String year = sdf2.format(date);
		String launchDate = sdf.format(date);
		String unit = (String) map.get("unit");
		String department = (String) map.get("department");
		String description = null;
		try {
			String currentNumber = cmdbService.getSerialNumber(year,billDescription);
			int num = Integer.parseInt(currentNumber.substring(currentNumber.length()-3))+1;
			if(num<10){
				description = year+billDescription+"00"+num;
			}else if(num<100){
				description = year+billDescription+"0"+num;
			}else{
				description = year+billDescription+num;
			}
		} catch (Exception e) {
			description = year+billDescription+"001";
		}
		model.addAttribute("username", realName);
		model.addAttribute("launchDate", launchDate);
		model.addAttribute("unit", unit);
		model.addAttribute("department", department);
		model.addAttribute("description", description);
		return "/"+billName+"/add";
	}
	
	@RequestMapping(value="/updateBill",method=RequestMethod.POST)
	public String updateBill(@RequestParam(value = "file") MultipartFile file,HttpServletRequest request){
		String path = null;
		String fileName = null;
		if (!file.isEmpty()) {
			fileName = file.getOriginalFilename();
			String suffixName = fileName.substring(fileName.lastIndexOf("."));
			String filePath = "C:/Sudan_资管/upload/";
			path = filePath + UUID.randomUUID() + suffixName;
			File dest = new File(path);
			if (!dest.getParentFile().exists()) {
				dest.getParentFile().mkdirs();
			}
			try {
				file.transferTo(dest);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Map<String ,String[]> map=request.getParameterMap();
		if(fileName != null){
			map.put("Attachment", new String[]{fileName});
			map.put("Path", new String[]{path});
		}else{
			map.remove("Attachment");
			map.remove("Path");
		}
		String billName = map.get("BillName")[0].replace("\"", "");
		billService.updateBill(map);
		return "redirect: toBill";
	}
	
	@RequestMapping(value="/billList",method=RequestMethod.GET)
	@ResponseBody
	public PageBean<Map<String,Object>> billList(HttpServletRequest request,Integer pageNum,Integer pageSize){
		Map<String, Object> user = UserUtil.getUserFromSession(request.getSession());
		String username = (String)user.get("USER_LOGIN_NAME");
		PageBean<Map<String,Object>> list = billService.findBillListByUser(username,pageNum,pageSize);
		return list;
	}
	
	@RequestMapping(value="/toBill",method=RequestMethod.GET)
	public String toBill(HttpServletRequest request){
		return "/bill/list";
	}
	
	@RequestMapping(value="/deleteBill",method=RequestMethod.GET)
	public String deleteBill(HttpServletRequest request,
			@RequestParam("id")String id,
			@RequestParam("billName")String billName,
			Model model){
		billName = billName.replace("\"", "");
		billService.deleteBillByIdAndBillName(id,billName);
		return "redirect: toBill";

	}
	
	@RequestMapping(value="/findBill",method=RequestMethod.GET)
	public String findBill(HttpServletRequest request,
			@RequestParam("id")String id,
			@RequestParam("billName")String billName,
			Model model){
		billName = billName.replace("\"", "");
		Map<String,Object> bill = billService.findBillByIdAndBillName(id,billName);
		model.addAttribute("bill", bill);
		return "/"+billName+"/update";
	}
}
