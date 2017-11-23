$(document).ready(function(){
    var x = ($("#logoImg").outerHeight(true)-$(".upLoadBtn").outerHeight(true))/2;
    $(".upLoadBtn").css("margin-top",x);
    asideH();
    frameH();
    collapseBtn();
    drawList();
    processList1();
    processList2();
});

$(window).resize(function(){    //更改窗口大小
    asideH();
    frameH();
});

function asideH(){              //侧栏高度
    var wh = $(window).height();
    var headerH = $(".header").outerHeight(true);
    $("aside>ul").css("height", (wh - headerH) + "px");
    var asideH = $("aside>ul").height();
  //侧栏折叠按钮定位
    var btnH = $(".foldBtnLeft").height();
    $(".foldBtnLeft,.foldBtnRight").css("top", (wh - btnH)/2 + "px");
}

function frameH(){              //iframe高度
    var wh = $(window).height();
    var h1 = $("article").outerHeight(true) - $("article section div.panel").height();
    var iFrameH = wh - h1;
    $("article section div.iFrame").css("height", iFrameH + "px");
}

function collapseBtn(){
    $(".foldBtnLeft").click(function(){
        $("aside").css("display","none");
        $("article").removeClass("col-lg-9 col-sm-9 col-md-9")
            .addClass("col-lg-12 col-sm-12 col-md-12");
        $(".foldBtnRight").show();
    });
    $(".foldBtnRight").click(function(){
        $("aside").css("display","block");
        $("article").removeClass("col-lg-12 col-sm-12 col-md-12")
            .addClass("col-lg-9 col-sm-9 col-md-9");
        $(".foldBtnRight").hide();
    })
}



/*导航 下拉菜单*/
$("#flowLabel, #tasksLabel").click(function(){
    $(this).parent().siblings().children("ul").removeClass("in");
});

/*分页*/
function drawList(){
    $.ajax({
        type:"POST",
        url:"ex1.json",
        dataType:"json",
        success:function(str){
            $.jqPaginator("#drawingPages", {//pContainer 分页容器
                totalPages: parseInt(str.length/10)+1,     //数据总页数
                visiblePages: 10,    //设置最多显示的页码数
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
                                tags1 += "<tr><td>"+item.fileName+"</td><td>"+item.version+"</td><td>"+item.date+"</td><td><a href='' download='"+item.location+"'>下载</a></td></tr>";
                            }
                            $('#drawingList .table tbody').empty().append(tags1);
                        }else{
                            tags1 = "";
                            for(var q = (num-1)*10; q<=num*10-1; q++){
                                item = str[q];
                                tags1 += "<tr><td>"+item.fileName+"</td><td>"+item.version+"</td><td>"+item.date+"</td><td><a href='' download='"+item.location+"'>下载</a></td></tr>";
                            }
                            $('#drawingList .table tbody').empty().append(tags1);
                        }
                    }else{
                        alert("很抱歉，没有数据")
                    }
                }
            });
        }
    });
}

function processList1(){
    $.ajax({
        type:"POST",
        url:"ex1.json",
        dataType:"json",
        success:function(str){
            $.jqPaginator("#processPages-1", {// 分页容器
                totalPages: parseInt(str.length/10)+1,     //数据总页数
                visiblePages: 10,    //设置最多显示的页码数
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
                                tags1 += "<tr><td>"+item.fileName+"</td><td>"+item.version+"</td><td>"+item.date+"</td><td><a href='' download='"+item.location+"'>下载</a></td></tr>";
                            }
                            $('#processList-1 .table tbody').empty().append(tags1);
                        }else{
                            tags1 = "";
                            for(var q = (num-1)*10; q<=num*10-1; q++){
                                item = str[q];
                                tags1 += "<tr><td>"+item.fileName+"</td><td>"+item.version+"</td><td>"+item.date+"</td><td><a href='' download='"+item.location+"'>下载</a></td></tr>";
                            }
                            $('#processList-1 .table tbody').empty().append(tags1);
                        }
                    }else{
                        alert("很抱歉，没有数据")
                    }
                }
            });
        }
    });
}

function processList2(){
    $.ajax({
        type:"POST",
        url:"ex1.json",
        dataType:"json",
        success:function(str){
            $.jqPaginator("#processPages-2", {// 分页容器
                totalPages: parseInt(str.length/10)+1,     //数据总页数
                visiblePages: 10,    //设置最多显示的页码数
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
                                tags1 += "<tr><td>"+item.fileName+"</td><td>"+item.version+"</td><td>"+item.date+"</td><td><a href='' download='"+item.location+"'>下载</a></td></tr>";
                            }
                            $('#processList-2 .table tbody').empty().append(tags1);
                        }else{
                            tags1 = "";
                            for(var q = (num-1)*10; q<=num*10-1; q++){
                                item = str[q];
                                tags1 += "<tr><td>"+item.fileName+"</td><td>"+item.version+"</td><td>"+item.date+"</td><td><a href='' download='"+item.location+"'>下载</a></td></tr>";
                            }
                            $('#processList-2 .table tbody').empty().append(tags1);
                        }
                    }else{
                        alert("很抱歉，没有数据")
                    }
                }
            });
        }
    });
}

//任务栏折叠
function collapseTasksList(){
    $("article section .panel .panel-heading").dblclick(function(){
        $(this).siblings(".panel-body").stop(true).slideToggle();
    })
}

//侧栏折叠
function collapseBtn(){
    $(".foldBtnLeft").click(function(){
        //$("aside").css("display","none");
        $("aside").css("display","none");
        $("article").removeClass("col-lg-9 col-sm-9 col-md-9")
            .addClass("col-lg-12 col-sm-12 col-md-12");
        $(".foldBtnRight").show();
    });
    $(".foldBtnRight").click(function(){
        $("aside").css("display","block");
        $("article").removeClass("col-lg-12 col-sm-12 col-md-12")
            .addClass("col-lg-9 col-sm-9 col-md-9");
        $(".foldBtnRight").hide();
    })
}

















