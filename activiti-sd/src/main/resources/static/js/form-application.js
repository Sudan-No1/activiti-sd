$(document).ready(function(){
    var data = {
        Applicant: "张兰",
        Unit: "上海总部",
        Department: "网络发展部",
        LaunchDate:"2017-11-15",
        Description:"2017XXXX294"
    };
    var tags = "";
    $.each(data, function(name,value){
        tags += "<div class='form-group'>" +
        "<label class='col-sm-2 control-label'>发起人</label>" +
        "<div class='col-sm-4'>" +
        "<p class='form-control-static'>"+ value+"</p>" +
        "</div></div>";
    });
    $(".form-horizontal").prepend(tags);
});