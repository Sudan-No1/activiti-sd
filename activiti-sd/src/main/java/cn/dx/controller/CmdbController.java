package cn.dx.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.dx.service.CmdbService;

@Controller
@RequestMapping("/cmdbController")
public class CmdbController {
	@Autowired
	private CmdbService cmdbService;
	
	@RequestMapping(value="/room/list",method=RequestMethod.POST)
	@ResponseBody
	public List<Map<String,Object>> getRoomList(
			HttpServletRequest request,
			Model model){
		List<Map<String,Object>> list = cmdbService.getRoomList();
		return list;
	}
	
	@RequestMapping(value="/room/condition/list",method=RequestMethod.POST)
	@ResponseBody
	public List<Map<String,Object>> getRoomList(@RequestParam("condition") String condition,
			HttpServletRequest request,
			Model model){
		if(condition == null || "".equals(condition)){
			return cmdbService.getRoomList();
		}
		List<Map<String,Object>> list = cmdbService.getRoomList(condition);
		return list;
	}
}
