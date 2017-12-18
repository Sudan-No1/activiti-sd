package cn.dx.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpSession;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import cn.dx.dao.BillDao;
import cn.dx.dao.UserDao;
import cn.dx.domain.PageBean;
import cn.dx.form.WorkflowBean;
import cn.dx.service.WorkflowService;
import cn.dx.utils.StringUtils;
import cn.dx.utils.UserUtil;
import cn.dx.wsdl.service.OAService;

@Service("workflowService")
public class WorkflowServiceImpl implements WorkflowService {
	@Autowired
	private BillDao billDao;

	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private FormService formService;

	@Autowired
	private HistoryService historyService;

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private OAService oaService;
	
	

	/** 部署流程定义 */
	@Override
	public String saveNewDeploye(MultipartFile file) {
		String message = "";

		String fileName = file.getOriginalFilename();

		try {
			InputStream fileInputStream = file.getInputStream();
			Deployment deployment = null;
			String extension = FilenameUtils.getExtension(fileName);
			if (extension.equals("zip") || extension.equals("bar")) {
				ZipInputStream zip = new ZipInputStream(fileInputStream);
				deployment = repositoryService.createDeployment()// 创建部署对象
						.name(fileName)// 添加部署名称
						.addZipInputStream(zip).deploy();// 完成部署
			} else if (extension.equals("png")) {
				deployment = repositoryService.createDeployment().addInputStream(fileName, fileInputStream).deploy();
			} else if (fileName.indexOf("bpmn20.xml") != -1) {
				deployment = repositoryService.createDeployment().addInputStream(fileName, fileInputStream).deploy();
			} else if (extension.equals("bpmn")) { // bpmn扩展名特殊处理，转换为bpmn20.xml
				String baseName = FilenameUtils.getBaseName(fileName);
				deployment = repositoryService.createDeployment()
						.addInputStream(baseName + ".bpmn20.xml", fileInputStream).deploy();
			} else {
				message = "不支持的文件类型：" + extension;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return message;
	}

	/** 查询部署对象信息，对应表（act_re_deployment） */
	@Override
	public PageBean<Map<String,Object>> findDeploymentList(Integer pageNum, Integer pageSize) {
		int totalRecord  = repositoryService.createDeploymentQuery()// 创建部署对象查询
				.orderByDeploymenTime().asc()//
				.list()
				.size();
		PageBean<Map<String,Object>> pb = new PageBean<>(pageNum, pageSize, totalRecord);
		int startIndex = pb.getStartIndex();
		List<Deployment> listDeployment = repositoryService.createDeploymentQuery()// 创建部署对象查询
				.orderByDeploymenTime().asc()//
				.listPage(startIndex, pageSize);
		List<Map<String,Object>> list = new ArrayList<>();
		for (Deployment deployment : listDeployment) {
			Map<String,Object> map = new HashMap<>();
			map.put("id", deployment.getId());
			map.put("name", deployment.getName());
			map.put("getDeploymentTime",  deployment.getDeploymentTime());
			list.add(map);
		}
		pb.setList(list);
		return pb;
	}

	/** 查询流程定义的信息，对应表（act_re_procdef） */
	@Override
	public PageBean<Map<String,Object>> findProcessDefinitionList(Integer pageNum, Integer pageSize) {
		int totalRecord = repositoryService.createProcessDefinitionQuery()// 创建流程定义查询
				.orderByProcessDefinitionVersion().asc()//
				.list()
				.size();
		
		PageBean<Map<String,Object>> pb = new PageBean<>(pageNum, pageSize, totalRecord);
		int startIndex = pb.getStartIndex();
		List<ProcessDefinition> listProcessDefinition = repositoryService.createProcessDefinitionQuery()// 创建流程定义查询
				.orderByProcessDefinitionVersion().asc()//
				.listPage(startIndex, pageSize);
		List<Map<String,Object>> list = new ArrayList<>();
		for (ProcessDefinition processDefinition : listProcessDefinition) {
			Map<String,Object> map = new HashMap<>();
			map.put("id", processDefinition.getId());
			map.put("name", processDefinition.getName());
			map.put("key",  processDefinition.getKey());
			map.put("version",  processDefinition.getVersion());
			map.put("resourceName",  processDefinition.getResourceName());
			map.put("diagramResourceName",  processDefinition.getDiagramResourceName());
			map.put("deploymentId",  processDefinition.getDeploymentId());
			list.add(map);
		}
		pb.setList(list);
		return pb;
	}

	/** 使用部署对象ID和资源图片名称，获取图片的输入流 */
	@Override
	public InputStream findImageInputStream(String deploymentId, String imageName) {
		return repositoryService.getResourceAsStream(deploymentId, imageName);
	}

	/** 使用部署对象ID，删除流程定义 */
	@Override
	public void deleteProcessDefinitionByDeploymentId(String deploymentId) {
		repositoryService.deleteDeployment(deploymentId, true);
	}

	/** 
	 * 1.更新请假状态，
	 * 2.为所有节点设置办理人
	 * 3.让启动的流程实例关联业务 
	 **/
	@Override
	public void saveStartProcess(WorkflowBean workflowBean, HttpSession session) {
		Map<String, Object> variables = new HashMap<String, Object>();
		String billName = workflowBean.getBillName().replace("\"", "");
		Long id = workflowBean.getId();
    	Map<String,Object> user = UserUtil.getUserFromSession(session);
    	String applicant = (String)user.get("USER_LOGIN_NAME");
		Map<String, Object> bill = billDao.findBillById(billName, id);
		String conditionName = (String)bill.get("conditionName");
    	if(conditionName!=null){
    		if(conditionName.contains(",")){
    			String[] split = conditionName.split(",");
    			for (String string : split) {
    	    		Object conditionValue = bill.get(string);
    	    		variables.put(string, conditionValue);
				}
    		}else{
    			Object conditionValue = bill.get(conditionName);
    			variables.put(conditionName, conditionValue);
    		}
    	}
		bill.put("ProcessStatus", "审核中");
		billDao.updateBillState(billName, bill);
		String key = bill.get("IdClass").toString().replace("\"", "");
		Map<String,Object> gourpInfo =userDao.getGroupInfo(applicant);
		String unit_code = (String)gourpInfo.get("unit_code");
		Map<String,Object> map = userDao.findApprover(unit_code);
		List<Map<String,Object>> list = userDao.getAssingeeList("房屋调配流程");
		for (Map<String, Object> record : list) {
			String taskname = (String)record.get("TaskName");
			String assignee = (String)record.get("Assignee");
			variables.put(taskname, assignee);
		}
		String areaLeader = (String)map.get("areaLeader");
		String departmentLeader = (String)map.get("departmentLeader");
		variables.put("Applicant", applicant);
		variables.put("areaLeader", areaLeader);
		variables.put("departmentLeader", departmentLeader);
		// 格式：Leavebill.id的形式（使用流程变量）
		String objId = key + "." + id;
		variables.put("objId", objId);
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key, objId, variables);
		String processInstanceId = processInstance.getId();
		String taskId = taskService.createTaskQuery()
			.processInstanceId(processInstanceId)
			.singleResult()
			.getId();
		workflowBean.setTaskId(taskId);
		workflowBean.setBillName(billName);
		workflowBean.setApplicant(applicant);
		this.saveSubmitTask(workflowBean, session);
		

	}

	/** 2：使用当前用户名查询正在执行的任务表，获取当前任务的集合List<Task> */
	@Override
	public PageBean<Map<String,Object>> findUserTaskListByName(String name, String realName, Integer pageNum, Integer pageSize) {
		int totalRecord  = taskService.createTaskQuery()//
				.taskAssignee(name)// 指定个人任务查询
				.orderByTaskCreateTime().asc()//
				.list()
				.size();
		PageBean<Map<String,Object>> pb = new PageBean<>(pageNum, pageSize, totalRecord);
		int startIndex = pb.getStartIndex();
		List<Task> userTasks = taskService.createTaskQuery()//
				.taskAssignee(name)// 指定个人任务查询
				.orderByTaskCreateTime().asc()
				.listPage(startIndex, pageSize);
		List<Map<String,Object>> list = new ArrayList<>();
		for (Task task : userTasks) {
        	Map<String, Object> map = new HashMap<>();
        	map.put("taskId", task.getId());
        	map.put("taskFormKey", task.getFormKey());
        	map.put("taskName", task.getName());
        	map.put("taskCreateTime", task.getCreateTime());
        	map.put("realName",realName);
        	list.add(map);
		}
		pb.setList(list);
		return pb;
	}

	@Override
	public List<Task> findGroupTaskListByName(String name) {
		List<Task> list = taskService.createTaskQuery().taskCandidateUser(name).orderByTaskCreateTime().asc().list();
		return list;
	}

	/** 使用任务ID，获取当前任务节点中对应的Form key中的连接的值 */
	@Override
	public String findTaskFormKeyByTaskId(String taskId) {
		TaskFormData formData = formService.getTaskFormData(taskId);
		// 获取Form key的值
		String url = formData.getFormKey();
		return url;
	}

	/** 一：使用任务ID，查找请假单ID，从而获取请假单信息 */
	@Override
	public Map<String, Object> findBillByTaskId(String taskId) {
		// 1：使用任务ID，查询任务对象Task
		Task task = taskService.createTaskQuery()//
				.taskId(taskId)// 使用任务ID查询
				.singleResult();
		// 2：使用任务对象Task获取流程实例ID
		String processInstanceId = task.getProcessInstanceId();
		// 3：使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()//
				.processInstanceId(processInstanceId)// 使用流程实例ID查询
				.singleResult();
		// 4：使用流程实例对象获取BUSINESS_KEY
		String buniness_key = pi.getBusinessKey();
		// 5：获取BUSINESS_KEY对应的主键ID，使用主键ID，查询请假单对象（Leavebill.1）
		String id = "";
		String billName = "";
		if (StringUtils.isNotBlank(buniness_key)) {
			// 截取字符串，取buniness_key小数点的第2个值
			billName = buniness_key.split("\\.")[0];
			id = buniness_key.split("\\.")[1];
		}
		// 查询请假单对象
		Map<String, Object> bill = billDao.findBillById(billName, Long.parseLong(id));
		return bill;
	}

	/**
	 * 二：已知任务ID，查询ProcessDefinitionEntiy对象，从而获取当前任务完成之后的连线名称，并放置到List<String>集合中
	 */
	@Override
	public List<Map<String,Object>> findOutComeListByTaskId(String taskId) {
		// 返回存放连线的名称集合
		List<Map<String,Object>> list = new ArrayList<>();
		// 1:使用任务ID，查询任务对象
		Task task = taskService.createTaskQuery()//
				.taskId(taskId)// 使用任务ID查询
				.singleResult();
		// 2：获取流程定义ID
		String processDefinitionId = task.getProcessDefinitionId();
		// 3：查询ProcessDefinitionEntiy对象
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService
				.getProcessDefinition(processDefinitionId);
		// 使用任务对象Task获取流程实例ID
		String processInstanceId = task.getProcessInstanceId();
		// 使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()//
				.processInstanceId(processInstanceId)// 使用流程实例ID查询
				.singleResult();
		// 获取当前活动的id
		String activityId = pi.getActivityId();
		// 4：获取当前的活动
		ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);
		// 5：获取当前活动完成之后连线的名称
		List<PvmTransition> pvmList = activityImpl.getOutgoingTransitions();
		if (pvmList != null && pvmList.size() > 0) {
			for (PvmTransition pvm : pvmList) {
				Map<String,Object> map = new HashMap<>();
				String name = (String) pvm.getProperty("name");
				String id = (String) pvm.getId();
				String[] split = StringUtils.split(id, ".");
				if(split.length>1){
					String group = split[1];
					map.put("group", group);
				}else{
					map.put("group", null);
				}
				if (StringUtils.isNotBlank(name)) {
//					list.add(name);
					map.put("name", name);
				} else {
					map.put("name", "提交");
//					list.add("提交");
				}
				list.add(map);
			}
		}
		return list;
	}
/*	*//**
	 * 二：已知任务ID，查询ProcessDefinitionEntiy对象，从而获取当前任务完成之后的连线名称，并放置到List<String>集合中
	 *//*
	@Override
	public List<String> findOutComeListByTaskId(String taskId) {
		// 返回存放连线的名称集合
		
		List<String> list = new ArrayList<String>();
		// 1:使用任务ID，查询任务对象
		Task task = taskService.createTaskQuery()//
				.taskId(taskId)// 使用任务ID查询
				.singleResult();
		// 2：获取流程定义ID
		String processDefinitionId = task.getProcessDefinitionId();
		// 3：查询ProcessDefinitionEntiy对象
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService
				.getProcessDefinition(processDefinitionId);
		// 使用任务对象Task获取流程实例ID
		String processInstanceId = task.getProcessInstanceId();
		// 使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()//
				.processInstanceId(processInstanceId)// 使用流程实例ID查询
				.singleResult();
		// 获取当前活动的id
		String activityId = pi.getActivityId();
		// 4：获取当前的活动
		ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);
		// 5：获取当前活动完成之后连线的名称
		List<PvmTransition> pvmList = activityImpl.getOutgoingTransitions();
		if (pvmList != null && pvmList.size() > 0) {
			for (PvmTransition pvm : pvmList) {
				String name = (String) pvm.getProperty("name");
				String id = (String) pvm.getId();
				String[] split = StringUtils.split(id, "|");
				if(split.length>1){
					String group = split[1];
				}
				if (StringUtils.isNotBlank(name)) {
					list.add(name);
				} else {
					list.add("提交");
				}
			}
		}
		return list;
	}*/

	/** 指定连线的名称完成任务 */
	@Override
	public void saveSubmitTask(WorkflowBean workflowBean, HttpSession session) {
		Map<String, Object> variables = new HashMap<String, Object>();
		String billName = workflowBean.getBillName().replace("\"", "");
		Long id = workflowBean.getId();
		Map<String, Object> bill = billDao.findBillById(billName, id);
		String conditionName = (String)bill.get("conditionName");
    	if(conditionName!=null){
    		if(conditionName.contains(",")){
    			String[] split = conditionName.split(",");
    			for (String string : split) {
    	    		Object conditionValue = bill.get(string);
    	    		variables.put(string, conditionValue);
				}
    		}else{
    			Object conditionValue = bill.get(conditionName);
    			variables.put(conditionName, conditionValue);
    		}
    	}
		String taskId = workflowBean.getTaskId();
		String outcome = workflowBean.getOutcome();
		String message = workflowBean.getComment();
		if(message != null &&message.contains("【")){
			
		}else{
			if(outcome.equals("同意")){
				message = "【同意】  "+message;
			}else if(outcome.equals("不同意")){
				message = "【不同意】  "+message;
			}
		}
		Map<String, Object> currentUser = UserUtil.getUserFromSession(session);
		String username = (String) currentUser.get("USER_LOGIN_NAME");
		String realName = (String) currentUser.get("REAL_NAME");
		String createOrg = (String) currentUser.get("ORGANIZATION_NAME");
		
		Task task = taskService.createTaskQuery()
				.taskId(taskId)
				.taskAssignee(username).singleResult();
		//添加批注信息
		String processInstanceId = task.getProcessInstanceId();
		if(message != null){
			Authentication.setAuthenticatedUserId(realName);
			taskService.addComment(taskId, processInstanceId, message);
		}
		
		//更具连线名称跳转下一任务节点
		if (outcome != null && !outcome.equals("提交")) {
			variables.put("outcome", outcome);
		}
		
		taskService.complete(taskId, variables);
		
		if(!username.equals(workflowBean.getApplicant())){
			
		}
		String result = oaService.completeTask(taskId, billName, "",null,null, createOrg, username, realName);
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()//
				.processInstanceId(processInstanceId)// 使用流程实例ID查询
				.singleResult();

		// 流程结束了
		if (pi == null) {
			// 更新请假单表的状态从1变成2（审核中-->审核完成）
			bill.put("ProcessStatus", "流程结束");
			billDao.updateBillState(billName, bill);
			//更新业务数据
		}else{
			//添加统一代办
			String assignee = taskService.createTaskQuery()
					.processInstanceId(pi.getId())
					.singleResult()
					.getAssignee();
			Map<String, Object> nextUser = userDao.getUserByUserName(assignee);
			String receiveUser = (String) nextUser.get("USER_LOGIN_NAME");
			String receiveUserName = (String) nextUser.get("REAL_NAME");
			String result2 = oaService.addTask(taskId, billName,  "", username, realName, createOrg, receiveUser, receiveUserName);
			System.out.println(result);
			System.out.println(result2);
			
		}
	}
	
	

	/** 获取批注信息，传递的是当前任务ID，获取历史任务ID对应的批注 */
	@Override
	public List<Comment> findCommentByTaskId(String taskId) {
		List<Comment> list = new ArrayList<Comment>();
		// 使用当前的任务ID，查询当前流程对应的历史任务ID
		// 使用当前任务ID，获取当前任务对象
		Task task = taskService.createTaskQuery()//
				.taskId(taskId)// 使用任务ID查询
				.singleResult();
		// 获取流程实例ID
		String processInstanceId = task.getProcessInstanceId();
		// //使用流程实例ID，查询历史任务，获取历史任务对应的每个任务ID
		// List<HistoricTaskInstance> htiList =
		// historyService.createHistoricTaskInstanceQuery()//历史任务表查询
		// .processInstanceId(processInstanceId)//使用流程实例ID查询
		// .list();
		// //遍历集合，获取每个任务ID
		// if(htiList!=null && htiList.size()>0){
		// for(HistoricTaskInstance hti:htiList){
		// //任务ID
		// String htaskId = hti.getId();
		// //获取批注信息
		// List<Comment> taskList =
		// taskService.getTaskComments(htaskId);//对用历史完成后的任务ID
		// list.addAll(taskList);
		// }
		// }
		list = taskService.getProcessInstanceComments(processInstanceId);
		return list;
	}

	/** 使用请假单ID，查询历史批注信息 */
	@Override
	public List<Comment> findCommentByBillId(String billName, Long id) {
		// 使用请假单ID，查询请假单对象
		Map<String, Object> bill = billDao.findBillById(billName, id);
		// 获取对象的名称
		String objectName = bill.get("IdClass").toString().replace("\"", "");
		// 组织流程表中的字段中的值
		String objId = objectName + "." + id;

		/** 1:使用历史的流程实例查询，返回历史的流程实例对象，获取流程实例ID */
		// HistoricProcessInstance hpi =
		// historyService.createHistoricProcessInstanceQuery()//对应历史的流程实例表
		// .processInstanceBusinessKey(objId)//使用BusinessKey字段查询
		// .singleResult();
		// //流程实例ID
		// String processInstanceId = hpi.getId();
		/** 2:使用历史的流程变量查询，返回历史的流程变量的对象，获取流程实例ID */
		HistoricVariableInstance hvi = historyService.createHistoricVariableInstanceQuery()// 对应历史的流程变量表
				.variableValueEquals("objId", objId)// 使用流程变量的名称和流程变量的值查询
				.singleResult();
		// 流程实例ID
		String processInstanceId = hvi.getProcessInstanceId();
		List<Comment> list = taskService.getProcessInstanceComments(processInstanceId);
		return list;
	}

	/** 1：获取任务ID，获取任务对象，使用任务对象获取流程定义ID，查询流程定义对象 */
	@Override
	public ProcessDefinition findProcessDefinitionByTaskId(String taskId) {
		// 使用任务ID，查询任务对象
		Task task = taskService.createTaskQuery()//
				.taskId(taskId)// 使用任务ID查询
				.singleResult();
		// 获取流程定义ID
		String processDefinitionId = task.getProcessDefinitionId();
		// 查询流程定义的对象
		ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()// 创建流程定义查询对象，对应表act_re_procdef
				.processDefinitionId(processDefinitionId)// 使用流程定义ID查询
				.singleResult();
		return pd;
	}

	/**
	 * 二：查看当前活动，获取当期活动对应的坐标x,y,width,height，将4个值存放到Map<String,Object>中
	 * map集合的key：表示坐标x,y,width,height map集合的value：表示坐标对应的值
	 */
	@Override
	public Map<String, Object> findCoordingByTask(String taskId) {
		// 存放坐标
		Map<String, Object> map = new HashMap<String, Object>();
		// 使用任务ID，查询任务对象
		Task task = taskService.createTaskQuery()//
				.taskId(taskId)// 使用任务ID查询
				.singleResult();
		// 获取流程定义的ID
		String processDefinitionId = task.getProcessDefinitionId();
		// 获取流程定义的实体对象（对应.bpmn文件中的数据）
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService
				.getProcessDefinition(processDefinitionId);
		// 流程实例ID
		String processInstanceId = task.getProcessInstanceId();
		// 使用流程实例ID，查询正在执行的执行对象表，获取当前活动对应的流程实例对象
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()// 创建流程实例查询
				.processInstanceId(processInstanceId)// 使用流程实例ID查询
				.singleResult();
		// 获取当前活动的ID
		String activityId = pi.getActivityId();
		// 获取当前活动对象
		ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);// 活动ID
		// 获取坐标
		map.put("x", activityImpl.getX());
		map.put("y", activityImpl.getY());
		map.put("width", activityImpl.getWidth());
		map.put("height", activityImpl.getHeight());
		return map;
	}

	@Override
	public void claim(String taskId, String userId) {
		taskService.claim(taskId, userId);

	}

	@Override
	public PageBean<Map<String, Object>> getHistoryTaskList(String username,  String realName, Integer pageNum, Integer pageSize) {
		int totalRecord  = historyService.createHistoricProcessInstanceQuery()
				.variableValueEquals("Applicant", username)
				.finished()
				.list()
				.size();
		PageBean<Map<String,Object>> pb = new PageBean<>(pageNum, pageSize, totalRecord);
		int startIndex = pb.getStartIndex();
		List<HistoricProcessInstance> listPage = historyService.createHistoricProcessInstanceQuery()
		.variableValueEquals("Applicant", username)
		.finished()
		.orderByProcessInstanceEndTime()
		.desc()
		.listPage(startIndex, pageSize);
		List<Map<String,Object>> list = new ArrayList<>();
		for (HistoricProcessInstance hpi : listPage) {
			Map<String,Object> map = new HashMap<>();
			map.put("businessKey", hpi.getBusinessKey());
			map.put("startTime", hpi.getStartTime());
			map.put("currentUser", realName);
			list.add(map);
		}
		pb.setList(list);
		return pb;
	}
}
