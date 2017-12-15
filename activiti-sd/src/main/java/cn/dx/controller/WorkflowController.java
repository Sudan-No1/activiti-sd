package cn.dx.controller;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cn.dx.domain.PageBean;
import cn.dx.form.WorkflowBean;
import cn.dx.service.BillService;
import cn.dx.service.WorkflowService;
import cn.dx.utils.StringUtils;
import cn.dx.utils.UserUtil;

@Controller
@RequestMapping("/workflow")
public class WorkflowController{
    @Autowired
    private WorkflowService workflowService;
    
    @Autowired
    private BillService billService;
    
    /** 发布首页	*/
    @RequestMapping("/deployHome")
    public String deployHome()
    {
        return "/workflow/workflow";
    }
    
    @RequestMapping("/getDepList")
    @ResponseBody
    public PageBean<Map<String,Object>> getDepList(Integer pageNum,Integer pageSize){
    	PageBean<Map<String,Object>> depList = workflowService.findDeploymentList(pageNum,pageSize);
    	return depList;
    }
    
    /** 发布首页	*/
    @RequestMapping("/getPdList")
    @ResponseBody
    public PageBean<Map<String,Object>>  getPdList(Integer pageNum,Integer pageSize)
    {
    	PageBean<Map<String,Object>> pdList = workflowService.findProcessDefinitionList(pageNum,pageSize);
    	return pdList;
    }
    
    
    
    /** 发布流程	*/
    @RequestMapping(value="/deploy",method=RequestMethod.POST)
    public String newdeploy(MultipartFile file, 
    		RedirectAttributes redirectAttributes){
        if (file.isEmpty()){
            redirectAttributes.addFlashAttribute("message", "文件未上传");
            return "redirect:addDeploy";
        }else{
            workflowService.saveNewDeploye(file);
            return "redirect:deployHome";
        }
    }
    
    /**
     * 删除部署信息
     */
    @RequestMapping("/delDeployment")
    public String delDeployment(WorkflowBean workflowBean){
        String deploymentId = workflowBean.getDeploymentId();
        workflowService.deleteProcessDefinitionByDeploymentId(deploymentId);
        return "redirect:deployHome";
    }
    
    /**
     * 查看流程图
     * 
     */
    @RequestMapping("/viewImage")
    public void viewImage(WorkflowBean workflowBean, 
    		HttpServletResponse response)
        throws Exception{
        String deploymentId = workflowBean.getDeploymentId();
        String imageName = workflowBean.getImageName();
        InputStream in = workflowService.findImageInputStream(deploymentId, imageName);
        byte[] b = new byte[1024];
        int len = -1;
        while ((len = in.read(b, 0, 1024)) != -1){
            response.getOutputStream().write(b, 0, len);
        }
    }
    
    /**
     * 启动流程
     * 
     */
    @RequestMapping("/startProcess")
    public String startProcess(WorkflowBean workflowBean, 
    		HttpServletRequest request){
    	HttpSession session = request.getSession();
        workflowService.saveStartProcess(workflowBean,session);
        return "/bill/list";
    }
    
    /**
     * 任务管理首页显示
     * 
     */
    @RequestMapping("/listTask")
    @ResponseBody
    public PageBean<Map<String,Object>> listTask(HttpSession session,Integer pageNum,Integer pageSize){
        // 1：从Session中获取当前用户名
    	Map<String, Object> user = UserUtil.getUserFromSession(session);
        String loginname = (String)user.get("USER_LOGIN_NAME");
        String realName = (String)user.get("REAL_NAME");
        // 2：使用当前用户名查询正在执行的任务表，获取当前任务的集合List<Task>
        PageBean<Map<String,Object>> list = workflowService.findUserTaskListByName(loginname,realName,pageNum,pageSize);
        return list;
    }
    /**
     * 任务管理首页显示
     * 
     */
    @RequestMapping("/toTask")
    public String toTask(HttpSession session, Model model){
    	Map<String, Object> user = UserUtil.getUserFromSession(session);
    	String loginname = (String)user.get("USER_LOGIN_NAME");
    	if (StringUtils.isEmpty(loginname)){
    		return "redirect:login";
    	}
    	return "/workflow/tasklist";
    }
    
    /**
     * 打开任务表单
     */
    @RequestMapping("/viewTaskForm")
    public String viewTaskForm(WorkflowBean workflowBean, Model model){
        String taskId = workflowBean.getTaskId();
        String url = workflowService.findTaskFormKeyByTaskId(taskId);
        return "redirect:audit?taskId="+taskId+"&url="+url;
    }
    
    /**
     * 查看历史的批注信息
     * 
     */
    @RequestMapping("/viewHisComment")
    @ResponseBody
    public List<Comment> viewHisComment(WorkflowBean workflowBean, Model model){
    	String billName = workflowBean.getBillName();
    	billName = billName.replace("\"", "");
        Long id = workflowBean.getId();
        // 1：使用请假单ID，查询请假单对象，将对象放置到栈顶，支持表单回显
        Map<String,Object> bill = billService.findBillById(billName,id);
        model.addAttribute("bill", bill);
        // 2：使用请假单ID，查询历史的批注信息
        return workflowService.findCommentByBillId(billName,id);
    }
    
    /**
     * 查看历史的批注信息
     * 
     * @param workflowBean
     * @param model
     * @return
     */
    @RequestMapping("/historyComment")
    @ResponseBody
    public List<Comment> viewHisCommentByHistoryTask(@RequestParam("businesskey")String businessKey, Model model){
    	String[] split = businessKey.split("\\.");
    	if(split.length >1){
    		String billName = split[0];
    		Long id  = Long.parseLong(split[1]);
    		return workflowService.findCommentByBillId(billName,id);
    	}
    	return null;
    }
    
    // 准备表单数据
    @RequestMapping("/audit")
    public String audit(WorkflowBean workflowBean, Model model){
        String taskId = workflowBean.getTaskId();
        String url = workflowBean.getUrl();
        /** 一：使用任务ID，查找请假单ID，从而获取请假单信息 */
        Map<String,Object> bill = workflowService.findBillByTaskId(taskId);
        model.addAttribute("taskId", taskId);
        model.addAttribute("bill", bill);
        /** 二：已知任务ID，查询ProcessDefinitionEntiy对象，从而获取当前任务完成之后的连线名称，并放置到List<String>集合中 */
        List<Map<String,Object>> outcomeList = workflowService.findOutComeListByTaskId(taskId);
        model.addAttribute("outcomeList", outcomeList);
        /** 三：查询所有历史审核人的审核信息，帮助当前人完成审核，返回List<Comment> */
        List<Comment> commentList = workflowService.findCommentByTaskId(taskId);
        model.addAttribute("commentList", commentList);
        return url;
    }
    
   
    
    /**
     * 查看当前流程图（查看当前活动节点，并使用红色的框标注）
     */
    @RequestMapping("/viewCurrentImage")
    public String viewCurrentImage(WorkflowBean workflowBean, Model model){
        String taskId = workflowBean.getTaskId();
        /** 一：查看流程图 */
        ProcessDefinition pd = workflowService.findProcessDefinitionByTaskId(taskId);
        model.addAttribute("deploymentId", pd.getDeploymentId());
        model.addAttribute("imageName", pd.getDiagramResourceName());
        /** 二：查看当前活动，获取当期活动对应的坐标x,y,width,height，将4个值存放到Map<String,Object>中 */
        Map<String, Object> map = workflowService.findCoordingByTask(taskId);
        model.addAttribute("acs", map);
        return "/workflow/image";
    }
    
    
    @RequestMapping("/chaim")
    public String chaim(WorkflowBean workflowBean,HttpServletRequest request){
    	String taskId = workflowBean.getTaskId();
    	String userId = (String)UserUtil.getUserFromSession(request.getSession()).get("USER_LOGIN_NAME");
    	workflowService.claim(taskId,userId);
    	return "redirect:listTask";
    }
    
    /**
     * 提交任务
     */
    @RequestMapping("/submitTask")
    public String submitTask(WorkflowBean workflowBean, HttpServletRequest request){
    	HttpSession session = request.getSession();
        workflowService.saveSubmitTask(workflowBean, session);
        return "/workflow/tasklist";
    }
    
    /**
     * 历史流程页面跳转
     */
    @RequestMapping("/historyTaskList")
    public String toHistoryTaskList(WorkflowBean workflowBean, HttpServletRequest request,Model model){	
    	HttpSession session = request.getSession();
    	Map<String, Object> user = UserUtil.getUserFromSession(session);
    	String realname = (String)user.get("REAL_NAME");
    	model.addAttribute("realname", realname);
    	return "/home/historyTaskList";
    }
    /**
     * 查询历史流程
     */
    @RequestMapping("/queryHistoryTaskList")
    @ResponseBody
    public PageBean<Map<String,Object>> getHistoryTaskList(HttpServletRequest request,Integer pageNum,Integer pageSize){	
    	HttpSession session = request.getSession();
    	Map<String, Object> user = UserUtil.getUserFromSession(session);
    	String username = (String)user.get("USER_LOGIN_NAME");
        String realName = (String)user.get("REAL_NAME");
    	PageBean<Map<String,Object>> hpiList = workflowService.getHistoryTaskList(username, realName,pageNum, pageSize);
    	return hpiList;
    }
    
}
