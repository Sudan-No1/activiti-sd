	/*时间*/
	function formatDateTime(inputTime) {    
        var date = new Date(inputTime);  
        var y = date.getFullYear();    
        var m = date.getMonth() + 1;    
        m = m < 10 ? ('0' + m) : m;    
        var d = date.getDate();    
        d = d < 10 ? ('0' + d) : d;    
        var h = date.getHours();  
        h = h < 10 ? ('0' + h) : h;  
        var minute = date.getMinutes();  
        var second = date.getSeconds();  
        minute = minute < 10 ? ('0' + minute) : minute;    
        second = second < 10 ? ('0' + second) : second;   
        return y + '-' + m + '-' + d+' '+h+':'+minute+':'+second;    
    }; 

    /*分页*/
    function pageList(pUrl, pContainer, pNum,appendId,func){
    	
        $.ajax({
            type: "GET",
            url: pUrl,
            dataType: "json",
            success:function(str){
                $.jqPaginator(pContainer, {             //pContainer 分页容器
                    totalPages: parseInt(str.length/pNum)+1,     //数据总页数
                    visiblePages: 10,                   //设置最多显示的页码数
                    currentPage: 1,                     //设置当前页码
                    pageSize: pNum,                     //pNum 设置每页的条目数
                    first:'<li><a href="javaScript:;">|<<</a></li>',
                    prev:'<li><a href="javaScript:;"><</a></li>',
                    next:'<li><a href="javaScript:;">></a></li>',
                    last:'<li><a href="javaScript:;">>>|</a></li>',
                    page: '<li><a href="javascript:;">{{page}}</a></li>',
                    onPageChange: function(num, type){  //num当前页
                        if(str.length > 0){
                            if(num == parseInt(str.length/pNum)+1){//最后一页显示列表
                                var tags1 = "";
                                for(var j = (num-1)*pNum; j<=str.length-1; j++){
                                    //pageTags(str, j);
                                    tags1 += func(str, j);
                                }
                                $(appendId).empty().append(tags1);
                            }else{//除最后一页显示列表
                                tags1 = "";
                                for(var q = (num-1)*pNum; q<=num*pNum-1; q++){
                                    tags1 += func(str, q);
                                }
                                $(appendId).empty().append(tags1);
                            }
                        }else{
                            alert("很抱歉，没有数据")
                        }
                    }
                });
            }
        });
    }

    /*drawing数据列表*/
    function drawingFunc(str, n){
        var item = str[n];
        var tag = "";
        tag = "<tr><td>"+item.fileName+"</td><td>"+item.version+"</td><td>"+item.date+"</td><td><a href='' download='"+item.location+"'>下载</a></td></tr>";
        return tag;
    }
    
    /*historyTask数据列表*/
    function historyTaskPageList(str , j){
    	var item = str[j];
        var billName = item.businessKey.split(".")[0];
        var billDescription = null;
        if(billName == 'AllocateBill'){
        	billDescription = '房屋调配';
        }
        return "<tr><td>"+billDescription+
        "</td><td>"+currentUser+
        "</td><td>"+formatDateTime(item.startTime)+
    	"</td><td><a style='margin-right:5px' class='dx-btn-link'"+
    	"onclick=\"viewForminfo(\'"+item.businessKey+"\')\""+
		"data-toggle='modal'"+
		"data-target='#viewForminfo'>查看表单信息</a>"+
	    "<a class='dx-btn-link'"+
		"onclick=\"viewExamineRecord(\'"+item.businessKey+"\')\""+
		"data-toggle='modal'"+
		"data-target='#viewExamineRecord'>查看审批记录</a>"+
        "</td></tr>";
    }
    
    /*billList 数据列表
    function billList(str, n){
        var item = str[n];
        var tag = "";
        <tr th:each="bill : ${list}"><td th:text="${bill.Applicant }"></td><td th:text="${bill.BillDescription }"></td><td th:text="${bill.Description }"></td><td th:text="${bill.ProcessStatus }"></td><td th:if="${bill.ProcessStatus=='初始录入' }"><a th:href="@{/billController/findBill(id=${bill.Id},billName=${bill.IdClass})}">编辑</a><a th:href="@{/billController/deleteBill(id=${bill.Id},billName=${bill.IdClass})}">删除</a><a th:href="@{/workflow/startProcess(id=${bill.Id},billName=${bill.IdClass})}">申请房间</a></td><td th:if="${bill.ProcessStatus=='审核中' }"><a href="javaScript:;" data-toggle="modal" data-target="#auditRecordModal" th:onclick="'auditRecordModal(\''+${bill.Id}+'\',\''+${bill.IdClass}+'\')'">查看审核记录</a><!--  添加data--></td></tr>
        if(item.ProcessStatus=='初始录入'){
        	alert(2 )
        	  tag = '<tr>' +
      		'<td>' + item.Applicant + '</td>' +
      		'<td>' + item.BillDescription + '</td>' +
      		'<td>' + item.Description + '</td>' +
      		'<td>' + item.ProcessStatus + '</td>' +
      		'<td>' +
      			'<a href="/billController/findBill?id='+bill.Id+'&billName='+item.IdClass+'">编辑</a>' +
      			'<a href="/billController/deleteBill(id=${bill.Id},billName=${bill.IdClass})}">删除</a>' +
      			'<a href="/workflow/startProcess(id=${bill.Id},billName=${bill.IdClass})}">申请房间</a></td></tr>';
        }else if(item.ProcessStatus=='审核中'){
        	 tag = '<tr>' +
    		'<td>' + item.Applicant + '</td>' +
    		'<td>' + item.BillDescription + '</td>' +
    		'<td>' + item.Description + '</td>' +
    		'<td>' + item.ProcessStatus + '</td>' +
    		'<td>' +
			'<a href="javaScript:;" data-toggle="modal" data-target="#auditRecordModal"' +
				' th:onclick="'auditRecordModal(\''+${bill.Id}+'\',\''+${bill.IdClass}+'\')'">查看审核记录' +
			'</a>' +
		'</td></tr>';
        }
       return tag;
    }*/
    
    /*弹窗-提交表单*/    
//    dialogFormFun("请选择要上传的文件","#",dialogDrawUpload())
    function dialogFormFun(title,url,fun){
    	//title：弹框标题  url：表单提交地址
    	//fun: 返回拼接HTML语句
    	var str = '<form class="form-horizontal" method="get" action="'+url+'"  enctype="multipart/form-data">' +
    			'<div class="modal-header">' +
    			'<a class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></a>' +
    			'<h4 class="modal-title">' + title + '</h4></div>'+
    			fun() +	//内容部分
    			'</form>';
    	if($(".modal-content").has("form").length == 0){
    		$(".modal .modal-content").append(str);
    	}else{
    		$(".modal .modal-content").empty();
    		$(".modal .modal-content").append(str);
    	}
    }
    
    /*弹窗-查看视图/数据*/    
    //dialogViewFun("请选择要上传的文件","#")
    function dialogViewFun(title, url, fun){//title：弹框标题  url：表单提交地址
    	var str = '<div class="modal-header">' +
    	'<a class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></a>' +
    	'<h4 class="modal-title">' + title + '</h4></div>'+ fun(url); 	//内容部分
    	
    	if($(".modal-content").has(".modal-header").length == 0){
    		$(".modal .modal-content").append(str);
    	}else{
    		$(".modal .modal-content").empty();
    		$(".modal .modal-content").append(str);
    	}
    }
    
    function dialogDrawUpload(){
    	return '<div class="modal-body">' +
			'<input type="file" id="upload-file" /></div>' +
			'<div class="modal-footer">' +
			'<a class="btn btn-default" data-dismiss="modal">取消</a>' +
			'<a class="btn btn-primary">确定</a></div>';
    }
    
    function dialogFlowUpload(){
    	return '<div class="modal-body">' +
        	'<div class="form-group">' +
	    	'<label class="col-sm-2 control-label">名称：</label>' +
	    	'<div class="col-sm-4">' +
 			'<input class="form-control" type="text" name="deployName"></div></div>' +
 			'<div class="form-group">' +
 			'<label class="col-sm-2 control-label">文件上传：</label>' +
 			'<div class="col-sm-4">' +
	        '<input type="file" name="file"/></div></div>' +
	        '</div>' +
			'<div class="modal-footer">' +
		    '<a class="btn btn-default" data-dismiss="modal">取消</a>' +
		    '<input type="submit" class="btn btn-primary" value="确定" />' +
		    '</div>';
    }
    
    function dialogViewImg(url){
    	 $.ajax({
    	        type:"GET",
    	        url:url,
    	        data:"",
    	        dataType:"json",
    	        success: function(str){
    	        var tags = '<div class="modal-body">' +
    	    	'<div>'+
    	    	'<img style="position: absolute;top: 0px;left: 0px;" src="/workflow/viewImage?deploymentId='+str.deploymentId +'&imageName='+str.imageName+'>'+
    	    	'<div style="position: absolute;border:1px solid red;top:'+str.acs.y+'px;left: ' + str.acs.x + 'px;width: ' + str.acs.width + 'px;height:' +  str.acs.height + 'px;"></div>'+
    	    	'</div>'+
    				'</div>';
    	    }
    	    });
    	return tags;
    }
    
    
    
    
    
    
    
    
    