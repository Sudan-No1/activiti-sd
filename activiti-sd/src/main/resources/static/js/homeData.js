//读写数据    totalPages, currentPage, pageSize

function getTopage(totalPages,currentPage, pageSize, labelCont, url, func){
    Page({
        num:totalPages,					//总页码
        startnum:currentPage,			//指定页码
        elem:$(labelCont),			//指定的元素
        callback:function(n){
            getDataChange(n);
        }
    });
}

function getData(currentPage, pageSize, labelCont, url, func){
    $.ajax({
        type: "GET",
        url: url,
        dataType: "json",
        data:{
            pageNum:currentPage,
            pageSize:pageSize
        },
        success: function (str) {
            func(str);
            getTopage(str.totalPage, currentPage, pageSize, labelCont, url, func);
        }
    })
}

//drawing页面list
function drawingData(str){
    if(str.length > 0){
        var tags1 = "";
        var item;
        for(var j = 0; j<=str.length-1; j++){
            item = str[j];
            tags1 += "<tr><td>"+item.fileName+"</td><td>"+item.version+"</td><td>"+item.date+"</td><td><a href='' download='"+item.location+"'>下载</a></td></tr>";
        }
        $("article .table tbody").empty();
        $("article .table tbody").append(tags1);
    }
}

//workflow页面list
function workflowA(str){
	var tags1 = "";
	var item;
	for(var i = 0; i<=str.list.length-1; i++){
		item = str.list[i];
		tags1 += "<tr><td>"+item.id+"</td><td>"+item.name+"</td><td>"+formatDateTime(item.getDeploymentTime)+"</td><td><a href='/workflow/delDeployment?deploymentId=" + item.id + "'>删除</a></td></tr>";
	}

	$("article #workflowDeploy .table tbody").empty();
	$("article #workflowDeploy .table tbody").append(tags1);
}

function workflowB(str){
	var tags1 = "";
	var item;
	for(var i = 0; i<=str.list.length-1; i++){
		item = str.list[i];
		tags1 += "<tr><td>"+item.deploymentId+"</td><td>"+item.id+"</td><td>"+item.name+"</td><td>"+item.key+"</td><td>"+item.resourceName+"</td><td>"+item.diagramResourceName+"</td><td>"+item.version+"</td><td><a target='_blank' href='/workflow/viewImage?deploymentId=" + item.deploymentId + "&imageName=" + item.diagramResourceName + "'>查看流程图</a></td></tr>";
	}

	$("article #workflowDefined .table tbody").empty();
	$("article #workflowDefined .table tbody").append(tags1);
}

//bill/list页面list
function billListData(str){
	var tags = "";
	var tag1 = "";
	var item;
	for(var i = 0; i<=str.list.length-1; i++){
		item = str.list[i];
  		if(item.ProcessStatus == "初始录入"){
  			tag1 = "<tr><td>" + item.Applicant +
  					"</td><td>" + item.BillDescription +
  					"</td><td>" + item.Description +
  					"</td><td>" + item.ProcessStatus +
  					"</td><td>" +
  					"<a href='/billController/findBill?id="+item.Id+"&billName="+item.IdClass+"'>编辑</a>" +
  					"<a href='/billController/deleteBill?id="+item.Id+"&billName="+item.IdClass+"'>删除</a>" +
  					"<a href='/workflow/startProcess?id="+item.Id+"&billName="+item.IdClass.value+"&outcome=提交申请'>申请房间</a>" +
  					"</td></tr>";
  		}else if(item.ProcessStatus == "审核中"){
  			tag1 = "<tr><td>" + item.Applicant +
					"</td><td>" + item.BillDescription +
					"</td><td>" + item.Description +
					"</td><td>" + item.ProcessStatus +
					"</td><td>" +
					"<a style='cursor: pointer;' data-toggle='modal' data-target='.modal' onclick='recordViewModal(" + item.Id + "," + item.IdClass.value + ")'>查看审核记录</a>" +
					"</td></tr>";
  		}
		tags += tag1;
	}
	
	$("article .table tbody").empty();
	$("article .table tbody").append(tags);
}

//taskList页面list
function taskListData(str){
	var tags = "";
	var taskType = "";
	var item;
	console.log(str);
	for(var i = 0; i<=str.list.length-1; i++){
		item = str.list[i];
		if(item.taskFormKey.split("/")[1] == "AllocateBill"){
			taskType = "房调流程";
		}
		tags += "<tr><td>" + item.taskId +
		"</td><td>"+ taskType +
		"</td><td>" + item.taskName +
		"</td><td>" + formatDateTime(item.taskCreateTime) +
		"</td><td>" + item.realName +
		"</td><td>" +
		"<a href='/workflow/viewTaskForm?taskId=" + item.taskId + "'>办理任务</a>" +
		"<a target='_blank' href='/workflow/viewCurrentImage?taskId=" + item.taskId + "'>查看流程图</a>" +
		"</td></tr>";
	}
	
	$("article .table tbody").empty();
	$("article .table tbody").append(tags);
}

//historyTaskList页面list
function historyTaskListData(str){
	var tags = "";
	var billDescription = "";
	var item;
	console.log(str);
	for(var i = 0; i<=str.list.length-1; i++){
		item = str.list[i];
		if(item.businessKey.split("/")[1] == "AllocateBill"){
			billDescription = "房屋调配";
		}
		tags += "<tr><td>" + billDescription + 
		"</td><td>" + item.currentUser + 
		"</td><td>" + formatDateTime(item.startTime) +
		"</td><td>" +
		"<a data-toggle='modal' data-target='.modal' onclick='viewForminfo(" + item.businessKey + ")'>查看表单信息</a>" +
		"<a data-toggle='modal' data-target='.modal' onclick='viewExamineRecord(" + item.businessKey + ")'>查看审批记录</a>" +
		"</td></tr>";
	}
	
	$("article .table tbody").empty();
	$("article .table tbody").append(tags);
}


//显示弹窗
//modalShow(false, false, form, modalBodyFunc);
function modalShow(titleVal, footerVal, type, func){
    var modalTitle;
    var modalFooter;
    var modalContent = "";
    if($(".modal .modal-content").has(".modal-header").length == 0){
    	func();
    }else{
        $(".modal .modal-content").empty();
        func();
    }
    
    if(titleVal){
        modalTitle= '<h4 class="modal-title">' + titleVal + '</h4>';
    }else {
        modalTitle= '';
    }
    $(".modal .modal-content .modal-header").append(modalTitle);
    
    if(footerVal){
    	if(type == "form"){
    		modalFooter = '<div class="modal-footer">' +
                		'<a class="btn btn-default" data-dismiss="modal">取消</a>' +
            			'<input type="submit" class="btn btn-primary" value="确定"/></div>';
    	}else{
    		if(type == "view"){
    			modalFooter = '<div class="modal-footer">' +
    						'<a class="btn btn-default" data-dismiss="modal">取消</a>' +
                			'<a class="btn btn-primary">确定</a></div>';
    		}
    	}
    }else {
        modalFooter = '';
    }
    $(".modal .modal-content form").append(modalFooter);
}

function recordView(id, billName){
	var contentStr = '<div class="modal-header">' +
					'<a class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></a>' +
					'</div><div class="modal-body"></div>';
	$(".modal .modal-content").append(contentStr);
	$.ajax({
		type : "GET",
		url : "/workflow/viewHisComment?Id=" + id + "&billName=" + billName,
		dataType : "json",
		error:function(data){
            console.log("没有数据");
            var tags = "<div>抱歉，暂时没有记录</div>"
            $(".modal .modal-content .modal-body").append(tags);
        },
		success : function(str) {
			console.log(str);
			var tags = '<table class="table table-hover">' +
						'<thead><tr>' +
						'<th width="25%">任务ID</th>' +
						'<th width="25%">审核时间</th>' +
						'<th width="50%">审核意见</th>' +
						'</tr></thead>' +
						'<tbody>';
			var item = '';
			for(var i = 0; i<=str.length-1; i++){
				item = str[i];
				console.log(item);
				tags += '<tr><td>' + item.userId + 
						'</td><td>'+ formatDateTime(item.time) + 
						'</td><td>' + item.fullMessage + 
						'</td></tr>';
				}

			tags += '</tbody>' +
					'</table>';
			
			$(".modal .modal-content .modal-body").append(tags);
		}
	});
}


