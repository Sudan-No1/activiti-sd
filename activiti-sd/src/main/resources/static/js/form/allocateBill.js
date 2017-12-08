//表单添加页面--获取房屋信息
getRoomName();
function getRoomName(){
    $.ajax({
        type:"POST",
        url:"/cmdbController/room/list",
        data:"",
        dataType:"json",
        success: function(str){
        fn(str);
    }
    });
}
$("#roomNameBtn").click(function(){
    //获取参数
    var data = $("#roomNameSearch").val();
    getRoomDataByCondition(data);
});

function  getRoomDataByCondition(data){
$.ajax({
    type:"POST",
    url:"/cmdbController/room/condition/list",
    data:{"condition":data},
    dataType:"json",
    success: function(str){
        fn(str);
    }
});
}

function fn(str){
    $.jqPaginator("#modalListPages", {//pContainer 分页容器
        totalPages: parseInt(str.length/10)+1,     //数据总页数
        visiblePages: 6,    //设置最多显示的页码数
        currentPage: 1,      //设置当前页码
        pageSize: 10,        //设置每页的条目数
        first:'<li><a href="javaScript:;">|<<</a></li>',
        prev:'<li><a href="javaScript:;"><</a></li>',
        next:'<li><a href="javaScript:;">></a></li>',
        last:'<li><a href="javaScript:;">>>|</a></li>',
        page: '<li><a href="javascript:;">{{page}}</a></li>',
        onPageChange: function(num, type){
            if(str.length > 0){
                if(num == parseInt(str.length/10)+1){
                    var tags1 = "";
                    for(var j = (num-1)*10; j<=str.length-1; j++){
                        var item = str[j];
                        tags1 += '<tr><td><input type="radio" name="roomInfo" value="'+j+'" /></td>'
                        		+'<td>'+item.BuildingNumber+'</td>'
                        		+'<td>'+item.RoomNumber+'</td>'
                        		+'<td>'+item.RoomLocation+'</td>'
                        		+'<td>'+item.FloorNmuber+'</td></tr>';
                    }
                    $('.modal .table tbody').empty().append(tags1);
                }else{
                    tags1 = "";
                    for(var q = (num-1)*10; q<=num*10-1; q++){
                        item = str[q];
                        tags1 += '<tr><td><input type="radio" name="roomInfo" value="'+q+'" /></td>'
                        +'<td>'+item.BuildingNumber+'</td>'
                		+'<td>'+item.RoomNumber+'</td>'
                		+'<td>'+item.RoomLocation+'</td>'
                		+'<td>'+item.FloorNmuber+'</td></tr>';
                    }
                    $('.modal .table tbody').empty().append(tags1);
                }
            }else{
                alert("很抱歉，没有数据")
            }
            if(str.length/10 <=1){
                $("#modalListPages").hide();
            }else{
            	$("#modalListPages").show();
            }
        }
    });

    $("#getDataBtn").click(function(){
    	var current =$(".modal .table tbody td input:checked").val();
        $("#applicationForm1 input[name='Name']").val(str[current].Description);
        $("#applicationForm1 input[name='Area']").val(str[current].Area);
        $("#applicationForm1 input[name='RoomId']").val(str[current].Id);
        $("input[name='UseUint']").val(str[current].UseUint);
        $("input[name='OwnershipCompany']").val(str[current].OwnershipCompany);
        $("input[name='CurrentPurpose']").val(str[current].BuildingPurpose);
        $("#applicationForm1 input[name='Address']").val(str[current].RoomLocation);
        $("#applicationForm1 input[name='Purpose']").removeAttr("checked");
        $("#applicationForm1 input[value='"+str[current].BuildingPurpose+"']").attr("checked","checked");

    });

}

function viewForminfo(key){
    if($("#viewForminfo .modal-content").has(".modal-body").length){
        $("#viewForminfo .modal-body").empty();
    }else {
        $("#viewForminfo .modal-content").append("<div class='modal-body'></div>");
    }
    $.ajax({
        type:"POST",
        url:"/billController/queryBill",
        data:{"businessKey":key},
        dataType:"json",
        success: function(str){
        	console.log(str);
            tags = "<form class='form-horizontal'><div class='row'>" +
                    "<div class='form-group col-sm-6'><label class='col-sm-4 control-label'>发起人</label><div class='col-sm-8'><p class='form-control-static'>"+ str.Applicant +"</p></div></div>" +
                    "<div class='form-group col-sm-6'><label class='col-sm-4 control-label'>发起单位</label><div class='col-sm-8'><p class='form-control-static'>"+ str.Department +"</p></div></div>" +
                    "</div><div class='row'><div class='form-group col-sm-6'><label class='col-sm-4 control-label'>发起部门</label><div class='col-sm-8'><p class='form-control-static'>"+ str.Unit +"</p></div></div>" +
                    "<div class='form-group col-sm-6'><label class='col-sm-4 control-label'>发起日期</label><div class='col-sm-8'><p class='form-control-static'>"+ str.LaunchDate +"</p></div></div>" +
                    "</div><div class='row'><div class='form-group col-sm-6'><label class='col-sm-4 control-label'>编号</label><div class='col-sm-8'><p class='form-control-static'>"+ str.Description+"</p></div></div>" +
                    "<div class='form-group col-sm-6'><label class='col-sm-4 control-label'>联系人</label><div class='col-sm-8'><p class='form-control-static'>"+ str.Contact+"</p></div></div>" +
                    "</div><div class='row'><div class='form-group col-sm-6'><label class='col-sm-4 control-label'>联系方式</label><div class='col-sm-8'><p class='form-control-static'>"+ str.ContactNo+"</p></div></div>" +
                    "<div class='form-group col-sm-6'><label class='col-sm-4 control-label'>房屋用途</label><div class='col-sm-8'><p class='form-control-static'>"+ str.Purpose+"</p></div></div>" +
                    "</div><div class='row'><div class='form-group col-sm-6'><label class='col-sm-4 control-label'>房屋名称</label><div class='col-sm-8'><p class='form-control-static'>"+ str.Name+"</p></div></div>" +
                    "<div class='form-group col-sm-6'><label class='col-sm-4 control-label'>房屋地址</label><div class='col-sm-8'><p class='form-control-static'>"+ str.Address+"</p></div></div>" +
                    "</div><div class='row'><div class='form-group col-sm-6'><label class='col-sm-4 control-label'>需求情况说明</label><div class='col-sm-8'><p class='form-control-static'>"+ str.DemandRemark+"</p></div></div>" +
                    "<div class='form-group col-sm-6'><label class='col-sm-4 control-label'>房屋名称(实际)</label><div class='col-sm-8'><p class='form-control-static'>"+ str.AuditName+"</p></div></div>" +
                    "</div><div class='row'><div class='form-group col-sm-6'><label class='col-sm-4 control-label'>房屋地址(实际)</label><div class='col-sm-8'><p class='form-control-static'>"+ str.AuditAddress+"</p></div></div>" +
                    "<div class='form-group col-sm-6'><label class='col-sm-4 control-label'>房屋面积</label><div class='col-sm-8'><p class='form-control-static'>"+ str.Area+"&ensp;平方米</p></div></div>" +
                    "</div><div class='row'><div class='form-group col-sm-6'><label class='col-sm-4 control-label'>审核情况说明</label><div class='col-sm-8'><p class='form-control-static'>"+ str.AuditRemark+"</p></div></div>" +
                    "</div></form>";
            $("#viewForminfo .modal-body").append(tags);
        }
    })
}


/*审批记录*/
function viewExamineRecord(key){
    if($("#viewExamineRecord .modal-content").has(".modal-body").length){
        $("#viewExamineRecord .modal-body").empty();
    }else {
        $("#viewExamineRecord .modal-content").append("<div class='modal-body'></div>");
    }
    $.ajax({
        type:"GET",
        url:'/workflow/historyComment?businesskey='+key,
        dataType:"json",
        success: function(str){
            var tags = '<table class="table table-hover"><thead><tr>' +
                    '<th width="20%">审核人</th>' +
                    '<th width="30%">审核时间</th>' +
                    '<th>审核意见</th></tr></thead><tbody>';
            $.each(str, function(index, item) {
                tags += '<tr><td>' + item.userId +
                        '</td><td>' + formatDateTime(item.time) +
                        '</td><td>' +  item.fullMessage +
                        '</td></tr>';
            });
            $("#viewExamineRecord .modal-body").append(tags);
            tags +='</tbody></table>';
        }
    })
}


function historyTaskPageList(user){
    $.ajax({
        type:"GET",
        url:"/workflow/queryHistoryTaskList",
        dataType:"json",
        success:function(str){
        	console.log(str)
            $.jqPaginator("#processPages-3", {// 分页容器
                totalPages: parseInt(str.length/10)+1,     //数据总页数
                visiblePages: 10,    //设置最多显示的页码数
                currentPage: 1,      //设置当前页码
                pageSize: 10,        //设置每页的条目数
                first:'<li><a href=" ">|<<</ a></li>',
                prev:'<li><a href="javaScript:;"><</ a></li>',
                next:'<li><a href="javaScript:;">></ a></li>',
                last:'<li><a href="javaScript:;">>>|</ a></li>',
                page: '<li><a href="javascript:;">{{page}}</ a></li>',
                onPageChange: function(num, type){
                    if(str.length > 0){
                        if(num == parseInt(str.length/10)+1){
                            var tags1 = "";
                            for(var j = (num-1)*10; j<=str.length-1; j++){
                                var item = str[j];
                                var billName = item.businessKey.split(".")[0];
                                var billDescription = null;
                                if(billName == 'AllocateBill'){
                                	billDescription = '房屋调配';
                                }
                                tags1 += "<tr><td>"+billDescription+
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
                            $('#taskSubPage-3 .table tbody').empty().append(tags1);
                        }else{
                            tags1 = "";
                            for(var q = (num-1)*10; q<=num*10-1; q++){
                                item = str[q];
                                var billName = item.businessKey.split(".")[0];
                                var billDescription = null;
                                if(billName == 'AllocateBill'){
                                	billDescription = '房屋调配';
                                }
                                tags1 += "<tr><td>"+billDescription+
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
                            $('#taskSubPage-3 .table tbody').empty().append(tags1);
                        }
                    }
                }
            });
        }
    });
}

