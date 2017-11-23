package cn.dx.form;

import java.io.File;

public class WorkflowBean
{
    
    private File file; // 流程定义部署文件
    private String filename;// 流程定义名称
    
    private Long id;// 申请单ID
    
    private String deploymentId;// 部署对象ID
    private String imageName; // 资源文件名称
    private String taskId; // 任务ID
    private String outcome; // 连线名称
    private String comment; // 备注
    private String billName; // 备注
    private String url; // form表单url
    private String conditionName; // 分支的条件名称
    private Object conditionValue; // 分支的条件名称
    private String applicant; // 申请人
    private String nextGroup; // 流转组
    
    public File getFile()
    {
        return file;
    }
    
    public void setFile(File file)
    {
        this.file = file;
    }
    
    public String getFilename()
    {
        return filename;
    }
    
    public void setFilename(String filename)
    {
        this.filename = filename;
    }
    
    public Long getId()
    {
        return id;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    public String getDeploymentId()
    {
        return deploymentId;
    }
    
    public void setDeploymentId(String deploymentId)
    {
        this.deploymentId = deploymentId;
    }
    
    public String getImageName()
    {
        return imageName;
    }
    
    public void setImageName(String imageName)
    {
        this.imageName = imageName;
    }
    
    public String getTaskId()
    {
        return taskId;
    }
    
    public void setTaskId(String taskId)
    {
        this.taskId = taskId;
    }
    
    public String getOutcome()
    {
        return outcome;
    }
    
    public void setOutcome(String outcome)
    {
        this.outcome = outcome;
    }
    
    public String getComment()
    {
        return comment;
    }
    
    public void setComment(String comment)
    {
        this.comment = comment;
    }

	public String getBillName() {
		return billName;
	}

	public void setBillName(String billName) {
		this.billName = billName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getConditionName() {
		return conditionName;
	}

	public void setConditionName(String conditionName) {
		this.conditionName = conditionName;
	}

	public Object getConditionValue() {
		return conditionValue;
	}

	public void setConditionValue(Object conditionValue) {
		this.conditionValue = conditionValue;
	}

	public String getApplicant() {
		return applicant;
	}

	public void setApplicant(String applicant) {
		this.applicant = applicant;
	}

	public String getNextGroup() {
		return nextGroup;
	}

	public void setNextGroup(String nextGroup) {
		this.nextGroup = nextGroup;
	}
    
}
