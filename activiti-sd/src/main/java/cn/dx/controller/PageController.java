package cn.dx.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

	@RequestMapping("/login")
	public String login(){
		return "/login";
	}
	@RequestMapping("/drawing")
	public String toDrawing(){
		return "/home/drawing";
	}
	
	@RequestMapping("/room")
	public String toRoom(){
		return "/home/room";
	}
	
	@RequestMapping("/{billName}/update")
	public String updateBill(@PathVariable("billName") String billName){
		return "/"+billName+"/update";
	}
	
    @RequestMapping("addDeploy")
    public String addDeploy()
    {
        return "/workflow/add";
    }
    
    
    
}
