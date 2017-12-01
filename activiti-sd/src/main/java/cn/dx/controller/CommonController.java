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
			WorkflowBean workflowBean, 
			HttpServletRequest request){
		Long id = workflowBean.getId();
		String comment = workflowBean.getComment();
		Map<String,String[]> params = new HashMap<>();
		params.put("BillName", new String[]{"AllocateBill"});		
		params.put("Id", new String[]{id+""});		
		params.put("AuditName", new String[]{name});		
		params.put("AuditAddress", new String[]{address});		
		params.put("RoomId", new String[]{roomId});		
		params.put("Area", new String[]{area});	
		params.put("AuditRemark", new String[]{comment});	
		billService.updateBill(params);
		HttpSession session = request.getSession();
        workflowService.saveSubmitTask(workflowBean, session);
        return "redirect:/workflow/listTask";
	}

}
