//读写数据    totalPages, currentPage, pageSize

function getTopage(totalPages,currentPage, pageSize, labelCont, url, func){
    Page({
        num:totalPages,					//总页码
        startnum:currentPage,				//指定页码
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

	$("#workflowDeploy .table tbody").empty();
	$("#workflowDeploy .table tbody").append(tags1);
}

function workflowB(str){
	var tags1 = "";
	var item;
	console.log(str.list[0]);
	for(var i = 0; i<=str.list.length-1; i++){
		item = str.list[i];
		tags1 += "<tr><td>"+item.deploymentId+"</td><td>"+item.id+"</td><td>"+item.name+"</td><td>"+item.key+"</td><td>"+item.resourceName+"</td><td>"+item.diagramResourceName+"</td><td>"+item.version+"</td><td><a class='btn btn-link' target='_blank' href='/workflow/viewImage?deploymentId=" + item.deploymentId + "&imageName=" + item.diagramResourceName + "'>查看流程图</a></td></tr>";
	}

	$("#workflowDefined .table tbody").empty();
	$("#workflowDefined .table tbody").append(tags1);
}


//显示弹窗
//modalShow(false, false, form, modalBodyFunc);
function modalShow(titleVal, footerVal, form, func){
    var modalTitle;
    var modalFooter;
    var modalContent = func();
    console.log(modalContent);
    if($(".modal .modal-content").has(".modal-header").length == 0){
        $(".modal .modal-content").append(modalContent);
    }else{
        $(".modal .modal-content").empty();
        $(".modal .modal-content").append(modalContent);
    }
    
    if(titleVal){
        modalTitle= '<h4 class="modal-title">' + titleVal + '</h4>';
    }else {
        modalTitle= '';
    }
    $(".modal .modal-content .modal-header").append(modalTitle);
    
    if(footerVal){
    	if(form == "form"){
    		modalFooter = '<a class="btn btn-default" data-dismiss="modal">取消</a>' +
            			'<input type="submit" class="btn btn-primary" value="确定"/>';
    	}else{
    		if(form == "view"){
    			modalFooter = '<a class="btn btn-default" data-dismiss="modal">取消</a>' +
                			'<a class="btn btn-primary">确定</a>';
    		}
    	}
    }else {
        modalFooter = '';
    }
    $(".modal .modal-content .modal-footer").append(modalFooter);
}





