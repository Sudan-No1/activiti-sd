window.onload = function(){
    labelHeight();//高度自适应
    loadStyle();//加载时样式
    
};
 $(window).resize(function(){
     labelHeight();
 });

/*高度自适应*/
function labelHeight(flag){
    var wH = $(window).height();
    var hH = null;
    if(flag) {
        hH = 0;
    }else{
        hH = $("header").outerHeight(true);
    }
    var atH = $("aside .sidebarTitle").outerHeight(true);
    var asideH = $("aside").outerHeight(true);
    var articleH = $("article").outerHeight(true);
    var standardH = 567;
    if((wH-hH) > standardH){
        $("aside ul").css("height",(wH - hH - atH) + "px");
        $("aside").css("height",(wH - hH) + "px");
        $("article").css("height",(wH - hH) + "px");
    }else {
        $("aside ul").css("height",(standardH-atH) + "px");
        $("aside").css("height",(standardH) + "px");
        $("article").css("height",standardH + "px");
    }
}

/*加载时的样式*/
function loadStyle(){
    /*定义侧栏与内容区域宽度*/
    if($("main").has("aside").length == 0){
        $("article").css({'width':'100%'});
    }

    /*定义滚动条的显示隐藏*/
    var liL = $(".sidebarNav ul li").length-1;
    var liH = $(".sidebarNav ul li").outerHeight(true);
    var ulH = $(".sidebarNav ul").outerHeight(true);
    if(liL*liH > ulH){
        $(".sidebarNav ul").css("overflow-y", "scroll");
    }else {
        $(".sidebarNav ul").css("overflow-y", "inherit");
    }
}

/*侧栏菜单折叠*/
function sideMenuCollapse(o){
    /*左右x   上下y*/
    switch (o){
        case 'x':{
            if($(".sidebarNav ul").is(":hidden")){
                $(".sidebarNav")
                    .css({overflow:'inherit', display:'block'})
                    .animate({width:'16.666667%'},300);
                $("main article")
                    .animate({width:'83.333333%'},300);
                $(".collapseX .glyphicon")
                    .removeClass("glyphicon-triangle-right")
                    .addClass("glyphicon-triangle-left");
            }else{
                $(".sidebarNav")
                    .css({overflow:'hidden', display:'none'})
                    .animate({width:'0'},300);
                $("main article").animate({width:'100%'},300);
                $(".collapseX .glyphicon")
                    .removeClass("glyphicon-triangle-left")
                    .addClass("glyphicon-triangle-right");
            }
        }break;
        case 'y':{
            if($("header").is(":hidden")){
                $("header").slideDown(300,function(){
                    labelHeight();
                });
                $(".collapseY .glyphicon")
                    .removeClass("glyphicon-triangle-bottom")
                    .addClass("glyphicon-triangle-top");
            }else{
                $("header").slideUp(300,function(){
                    labelHeight(true);
                });
                $(".collapseY .glyphicon")
                    .removeClass("glyphicon-triangle-top")
                    .addClass("glyphicon-triangle-bottom");
            }
        }break;
    }

}

