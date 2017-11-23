package cn.dx.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
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
import cn.dx.form.WorkflowBean;
import cn.dx.service.WorkflowService;
import cn.dx.utils.StringUtils;
import cn.dx.utils.UserUtil;

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
	public List<Deployment> findDeploymentList() {
		List<Deployment> list = repositoryService.createDeploymentQuery()// 创建部署对象查询
				.orderByDeploymenTime().asc()//
				.list();
		return list;
	}

	/** 查询流程定义的信息，对应表（act_re_procdef） */
	@Override
	public List<ProcessDefinition> findProcessDefinitionList() {
		List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()// 创建流程定义查询
				.orderByProcessDefinitionVersion().asc()//
				.list();
		return list;
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

	/** 更新请假状态，启动流程实例，让启动的流程实例关联业务 */
	@Override
	public void saveStartProcess(WorkflowBean workflowBean, HttpSession session) {
		String billName = workflowBean.getBillName().replace("\"", "");
		// 1：获取请假单ID，使用请假单ID，查询请假单的对象Leavebill
		Long id = workflowBean.getId();
		String applicant = workflowBean.getApplicant();
		Map<String, Object> bill = billDao.findBillById(billName, id);
		// 2：更新请假单的请假状态从0变成1（初始录入-->审核中）
		bill.put("ProcessStatus", "审核中");
		billDao.updateBillState(billName, bill);
		// 3：使用当前对象获取到流程定义的key（对象的名称就是流程定义的key）
		String key = bill.get("IdClass").toString().replace("\"", "");
		/**
		 * 4：从Session中获取当前任务的办理人，使用流程变量设置下一个任务的办理人 inputUser是流程变量的名称，
		 * 获取的办理人是流程变量的值
		 */
		Map<String, Object> variables = new HashMap<String, Object>();
		String inputUser = (String) UserUtil.getUserFromSession(session).get("Username");
		variables.put("inputUser", inputUser);// 表示惟一用户
		String leader;
		try {
			leader = userDao.findGroupLeader(inputUser);
		} catch (Exception e) {
			leader = inputUser;
		}
		variables.put("leader", leader);// 表示惟一用户
		variables.put("Applicant", applicant);// 放置申请人
		/**
		 * 5： (1)使用流程变量设置字符串（格式：Leavebill.id的形式），通过设置，让启动的流程（流程实例）关联业务
		 * (2)使用正在执行对象表中的一个字段BUSINESS_KEY（Activiti提供的一个字段），让启动的流程（流程实例）关联业务
		 */
		// 格式：Leavebill.id的形式（使用流程变量）
		String objId = key + "." + id;
		variables.put("objId", objId);
		// 6：使用流程定义的key，启动流程实例，同时设置流程变量，同时向正在执行的执行对象表中的字段BUSINESS_KEY添加业务数据，同时让流程关联业务
		runtimeService.startProcessInstanceByKey(key, objId, variables);

	}

	/** 2：使用当前用户名查询正在执行的任务表，获取当前任务的集合List<Task> */
	@Override
	public List<Task> findUserTaskListByName(String name) {
		List<Task> list = taskService.createTaskQuery()//
				.taskAssignee(name)// 指定个人任务查询
				.orderByTaskCreateTime().asc()//
				.list();
		return list;
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
		String taskId = workflowBean.getTaskId();
		String outcome = workflowBean.getOutcome();
		String message = workflowBean.getComment();
		String group = workflowBean.getNextGroup();
		String conditionName = workflowBean.getConditionName();
		Long id = workflowBean.getId();
		String billName = workflowBean.getBillName();
		Map<String, Object> currentUser = UserUtil.getUserFromSession(session);
		String username = (String) currentUser.get("Username");
		Task task = taskService.createTaskQuery()//
				.taskId(taskId)
				.taskAssignee(username).singleResult();
		//添加批注信息
		String processInstanceId = task.getProcessInstanceId();
		Authentication.setAuthenticatedUserId(username);
		taskService.addComment(taskId, processInstanceId, message);
		Map<String, Object> variables = new HashMap<String, Object>();
		//更具连线名称跳转下一任务节点
		if (outcome != null && !outcome.equals("提交")) {
			variables.put("outcome", outcome);
		}
		
		
//		String leader = (String) taskService.getVariable(taskId, "leader");
		if (group != null) {
			//TODO 根据祖名获取组成员
		/*	List<String> users = null;
			switch(group){
			case "leader0" :users = userDao.findZeroGroupUsersByGroupname(group);break;
			case "leader1" :users = userDao.findFirstGroupUsersByGroupname(group);break;
			case "leader2" :users = userDao.findSecondGroupUsersByGroupname(group);break;
			default: users = userDao.findGroupUsersByGroupname(group);
			}*/
			List<String> users = Arrays.asList("u2","u3");
			variables.put("userIds", users);
		} 
		if (conditionName != null) {
			Object conditionValue = workflowBean.getConditionValue();
			variables.put(conditionName, conditionValue);
		}
		taskService.complete(taskId, variables);

		ProcessInstance pi = runtimeService.createProcessInstanceQuery()//
				.processInstanceId(processInstanceId)// 使用流程实例ID查询
				.singleResult();
		// 流程结束了
		if (pi == null) {
			// 更新请假单表的状态从1变成2（审核中-->审核完成）
			Map<String, Object> bill = billDao.findBillById(billName, id);
			bill.put("ProcessStatus", "流程结束");
			billDao.updateBillState(billName, bill);
		}
	}
	
	
	/** 指定连线的名称完成任务 */
	public void saveSubmitTaskOld(WorkflowBean workflowBean, HttpSession session) {
		// 获取任务ID
		String taskId = workflowBean.getTaskId();
		// 获取连线的名称
		String outcome = workflowBean.getOutcome();
		// 批注信息
		String message = workflowBean.getComment();
		String group = workflowBean.getNextGroup();
		// 分支名称
		String conditionName = workflowBean.getConditionName();
		// 获取请假单ID
		Long id = workflowBean.getId();
		String billName = workflowBean.getBillName();
		
		Map<String, Object> currentUser = UserUtil.getUserFromSession(session);
		
		String username = (String) currentUser.get("Username");
		/**
		 * 1：在完成之前，添加一个批注信息，向act_hi_comment表中添加数据，用于记录对当前申请人的一些审核信息
		 */
		// 使用任务ID，查询任务对象，获取流程流程实例ID
		Task task = taskService.createTaskQuery()//
				.taskId(taskId)// 使用任务ID查询
				.taskAssignee(username).singleResult();
		// 获取流程实例ID
		String processInstanceId = task.getProcessInstanceId();
		Authentication.setAuthenticatedUserId(username);
		taskService.addComment(taskId, processInstanceId, message);
		/**
		 * 2：如果连线的名称是“提交”，那么就不需要设置，如果不是，就需要设置流程变量 在完成任务之前，设置流程变量，按照连线的名称，去完成任务
		 * 流程变量的名称：outcome 流程变量的值：连线的名称
		 */
		Map<String, Object> variables = new HashMap<String, Object>();
		if (outcome != null && !outcome.equals("提交")) {
			variables.put("outcome", outcome);
		}
		
		/*
		 * if (currentUser.getManager() != null) { variables.put("inputUser",
		 * currentUser.getManager().getName()); }
		 */
		// 3：使用任务ID，完成当前人的个人任务，同时流程变量
		// 4：当任务完成之后，需要指定下一个任务的办理人
		String leader = (String) taskService.getVariable(taskId, "leader");
		if (leader != null) {
			variables.put("userIds", leader);
			taskService.removeVariable(taskId, "leader");
		} else {
			// List<String> groupUsers =
			// userDao.findNextGroupUsersByUsernameAndBillName(username,billName);
			// String url = this.findTaskFormKeyByTaskId(taskId);
			// String[] formKey = url.split("\\|");
			// if(formKey.length == 2){
			// String group = formKey[1];
			List<String> groupUser = userDao.findGroupByGroupName(group);
			if (groupUser != null && groupUser.size() > 0) {
				StringBuilder sb = new StringBuilder();
				for (String user : groupUser) {
					sb.append(user).append(",");
				}
				variables.put("userIds", sb.toString().substring(0, sb.length() - 1));
			}
			// }
		}
		if (conditionName != null) {
			Object conditionValue = workflowBean.getConditionValue();
			variables.put(conditionName, conditionValue);
		}
		taskService.complete(taskId, variables);
		
		/**
		 * 5：在完成任务之后，判断流程是否结束 如果流程结束了，更新请假单表的状态从1变成2（审核中-->审核完成）
		 */
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()//
				.processInstanceId(processInstanceId)// 使用流程实例ID查询
				.singleResult();
		// 流程结束了
		if (pi == null) {
			// 更新请假单表的状态从1变成2（审核中-->审核完成）
			Map<String, Object> bill = billDao.findBillById(billName, id);
			bill.put("ProcessStatus", "流程结束");
			billDao.updateBillState(billName, bill);
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
}
