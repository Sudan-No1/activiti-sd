package cn.dx.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cn.dx.service.UserService;
import cn.dx.utils.UserUtil;

@Controller
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(@RequestParam("username") String username, 
			@RequestParam("password") String password,
			HttpServletRequest request) {
//		String username = request.getHeader("iv-user");
		Map<String,Object> user = userService.findUserByUsername(username);
		UserUtil.saveUserToSession(request.getSession(), user);
		return "redirect:/workflow/toTask";
	}
}
