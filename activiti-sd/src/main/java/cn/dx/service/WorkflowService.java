package cn.dx.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.web.multipart.MultipartFile;

import cn.dx.domain.PageBean;
import cn.dx.form.WorkflowBean;

public interface WorkflowService
{
    
	PageBean<Map<String,Object>> findDeploymentList(Integer pageNum, Integer pageSize);
    
    PageBean<Map<String,Object>> findProcessDefinitionList(Integer pageNum, Integer pageSize);
    
    InputStream findImageInputStream(String deploymentId, String imageName);
    
    void deleteProcessDefinitionByDeploymentId(String deploymentId);
    
    PageBean<Map<String, Object>> findUserTaskListByName(String name, String realName, Integer pageNum, Integer pageSize);
    
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
    
    void saveStartProcess(WorkflowBean workflowBean, HttpSession session);

	void claim(String taskId, String userId);

	PageBean<Map<String, Object>> getHistoryTaskList(String username, String realName, Integer pageNum, Integer pageSize);

}
