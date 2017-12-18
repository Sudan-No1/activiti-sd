package cn.dx.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cn.dx.form.WorkflowBean;
import cn.dx.service.BillService;
import cn.dx.service.WorkflowService;

@Controller
@RequestMapping("/commonController")
public class CommonController {
	
	@Autowired
	private WorkflowService workflowService;

	@Autowired
	private BillService billService;
	
	@RequestMapping(value="/AllocateBill/update",method=RequestMethod.POST)
	public String approvalUpdateBill(
			@RequestParam("Name")String name,
			@RequestParam("Address")String address,
			@RequestParam("Area")String area,
			@RequestParam("RoomId")String roomId,
			@RequestParam("message")String message,
			@RequestParam("Purpose")String purpose,
			WorkflowBean workflowBean, 
			HttpServletRequest request){
		Long id = workflowBean.getId();
		String comment = workflowBean.getComment();
		workflowBean.setComment("【"+message+"】"+comment);
		if(roomId != null && !"".endsWith(roomId)){
			Map<String,Object> params = new HashMap<>();
			params.put("BillName","AllocateBill");		
			params.put("Id",id+"");		
			params.put("AuditName",name);		
			params.put("AuditAddress",address);	
			params.put("Purpose",purpose);	
			params.put("RoomId",Integer.parseInt(roomId));		
			params.put("Area",area);	
			params.put("AuditRemark",comment);	
			billService.updateBillByAudit(params);
		}
		HttpSession session = request.getSession();
        workflowService.saveSubmitTask(workflowBean, session);
        return "/workflow/taskList";
	}

}
