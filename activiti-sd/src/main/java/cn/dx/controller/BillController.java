package cn.dx.controller;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cn.dx.service.BillService;
import cn.dx.utils.UserUtil;

@Controller
@RequestMapping("/billController")
public class BillController {

	@Autowired
	private BillService billService;
	
	@RequestMapping(value="/addBill",method=RequestMethod.POST)
	public String addBill(HttpServletRequest request){
		Map<String ,String[]> map=request.getParameterMap();
		Map<String, Object> user = UserUtil.getUserFromSession(request.getSession());
		String username = (String)user.get("Username");
		billService.addBill(username,map);
		String billName = map.get("billName")[0];
		return "redirect:billList?billName="+billName;
	}
	
	@RequestMapping("/{billName}/add")
	public String addBill(@PathVariable("billName") String billName,
			HttpServletRequest request,
			Model model){
		Map<String, Object> user = UserUtil.getUserFromSession(request.getSession());
		String username = (String)user.get("Username");
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String launchDate = sdf.format(date);
		String unit = "上海总部";
		String department = "网络发展部";
		String description = "2017房屋调配001";
		model.addAttribute("username", username);
		model.addAttribute("launchDate", launchDate);
		model.addAttribute("unit", unit);
		model.addAttribute("department", department);
		model.addAttribute("description", description);
		return billName+"/add";
	}
	
	
	@RequestMapping(value="/billList",method=RequestMethod.GET)
	public String billList(HttpServletRequest request,Model model){
		Map<String, Object> user = UserUtil.getUserFromSession(request.getSession());
		String username = (String)user.get("Username");
		List<Map<String,Object>> list = billService.findBillListByUser(username);
		model.addAttribute("list", list);
		return "/bill/list";
	}
	
	@RequestMapping(value="/deleteBill",method=RequestMethod.GET)
	public String deleteBill(HttpServletRequest request,
			@RequestParam("id")String id,
			@RequestParam("billName")String billName,
			Model model){
		billName = billName.replace("\"", "");
		billService.deleteBillByIdAndBillName(id,billName);
		return "redirect: billList?billName="+billName;
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
	
	@RequestMapping(value="/updateBill",method=RequestMethod.POST)
	public String updateBill(HttpServletRequest request){
		Map<String ,String[]> map=request.getParameterMap();
		String billName = map.get("BillName")[0].replace("\"", "");
		billService.updateBill(map);
		return "redirect: billList?billName="+billName;
	}
	
	
}
