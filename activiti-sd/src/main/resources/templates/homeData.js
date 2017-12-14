

/*
//读写数据

getdata(10, 1, '#page1', '#', drawingdata);//html页面调用
//getdata(pageNum: 总页数 ，num:当前页码 ，url:接口路径 ， labelCont:页码容器标签）

function getTopage(pageNum, pageCurrent, labelCont, url, func){
    Page({
        num:pageNum,					//总页码
        startnum:pageCurrent,			//指定页码
        elem:$(labelCont),		    //指定的页码容器标签
        callback:function(num){	    //回调函数  num:当前页数
            getdata(pageNum, num, labelCont, url, func);
        }
    });
}

function getdata(pageNum ,num, labelCont, url, func){
//pageNum: 总页数 ，num:当前页码 ，url:接口路径 ， labelCont:页码容器标签
    $.ajax({
        type: "POST",
        url: url,
        dataType: "json",
        data:{
            page: num
        },
        error:function(data){
            console.log("error:"+data);
        },
        success: function (str) {
            func(str);
            getTopage(pageNum, num, labelCont, url, func);
        }
    })
}

function drawingdata(str){
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

*/


//显示弹窗
//modalShow(false, false, modalBodyFunc);
function modalShow(titleVal, footerVal, func){
    var modalContent;
    var modalTitle;
    var modalFooter;
    if(titleVal){
        modalTitle= '<h4 class="modal-title">' + titleVal + '</h4>';
    }else {
        modalTitle= '';
    }

    if(footerVal){
        modalFooter = '<div class="modal-footer">' +
            '<a class="btn btn-default" data-dismiss="modal">取消</a>' +
            '<a class="btn btn-primary">确定</a>' +
            '</div>';
    }else {
        modalFooter = '';
    }

    var modalHeader = '<div class="modal-header">' +
        '<a class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></a>' +
        modalTitle +
        '</div>';
    var modalBody = func();
    modalContent = modalHeader + modalBody + modalFooter;
    $(".modal .modal-content").append(modalContent);
    if($(".modal .modal-content").has(".modal-body").length == 0){
        $(".modal .modal-content").append(modalContent);
    }else{
        $(".modal .modal-content").empty();
        $(".modal .modal-content").append(modalContent);
    }
}





