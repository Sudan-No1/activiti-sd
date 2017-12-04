var url = window.location.pathname;
$(document).ready(function(){
    var x = ($("#logoImg").outerHeight(true)-$(".upLoadBtn").outerHeight(true))/2;
    $(".upLoadBtn").css("margin-top",x);
    
//    初始化页面菜单状态
    $("a[href='"+ url +"']").parent("li").siblings().removeClass("active");
	$("a[href='"+ url +"']").parent("li").addClass("active");
	if($("a[href='"+ url +"']").parent("li").parent("ul").hasClass("collapse")){
		$("a[href='"+ url +"']").parent("li").parent("ul").addClass("in");
	}
    
    asideH();
    frameH();
    collapseBtn();
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
$("#roomLabel, #tasksLabel").click(function(){
    $(this).parent().siblings().children("ul").removeClass("in");
});

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

function getBuildingInfo(url){
	var oldUrl = $("#3dFrame").attr("src");
	if(typeof oldUrl === "undefined" || oldUrl != url){
		$("#3dFrame").attr("src",url);
	}
}


