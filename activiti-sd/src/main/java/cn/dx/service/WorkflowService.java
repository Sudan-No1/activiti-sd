package cn.dx.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.web.multipart.MultipartFile;

import cn.dx.form.WorkflowBean;

public interface WorkflowService
{
    
    List<Deployment> findDeploymentList();
    
    List<ProcessDefinition> findProcessDefinitionList();
    
    InputStream findImageInputStream(String deploymentId, String imageName);
    
    void deleteProcessDefinitionByDeploymentId(String deploymentId);
    
    List<Task> findUserTaskListByName(String name);
    
    List<Task> findGroupTaskListByName(String name);

    String findTaskFormKeyByTaskId(String taskId);
    
    Map<String,Object> findBillByTaskId(String taskId);
    
    List<Map<String,Object>> findOutComeListByTaskId(String taskId);
//    List<String> findOutComeListByTaskId(String taskId);
    
    void saveSubmitTask(WorkflowBean workflowBean, HttpSession session);
    
    List<Comment> findCommentByTaskId(String taskId);
    
    List<Comment> findCommentByBillId(String billName, Long id);
    
    ProcessDefinition findProcessDefinitionByTaskId(String taskId);
    
    Map<String, Object> findCoordingByTask(String taskId);
    
    String saveNewDeploye(MultipartFile file);
    
    void saveStartProcess(WorkflowBean workflowBean, Map<String, Object> user);

	void claim(String taskId, String userId);

	List<HistoricProcessInstance> getHistoryTaskList(String username);

}
