
var quartzJobApp = angular.module('quartzJobApp', [
]);


quartzJobApp.config(function ($httpProvider) {
    //http请求处理
    $httpProvider.defaults.headers.post = {'Content-Type': 'application/x-www-form-urlencoded'};
    $httpProvider.defaults.transformRequest = function (obj) {
        var str = [];
        for (var p in obj) {
            str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
        }
        return str.join("&");
    };
});

quartzJobApp.controller('quartzJobController', quartzJobController);

function quartzJobController($scope, $http) {

    $scope.repositoryId = GetQueryString("repositoryId");

    $scope.init = function () {
        $scope.getQuartzJobList();
    };


    $scope.getQuartzJobList = function(){

        var url = "/kettle/quartz/repository/getQuartzJobList";

        $http.post(url).then(function (resp) {

            if (resp.data.state == '200'){
                $scope.jobList = resp.data.data;
            }
        })
    };

    $scope.executeJob = function(quartzId){

        var url = "/kettle/quartz/repository/executeJob";
        $http.post(url,{quartzId:quartzId}).then(function (resp) {

            alert(resp.data.msgInfo);
            $scope.getQuartzJobList();
        })
    };

    $scope.stopJob = function(quartzId){

        var url = "/kettle/quartz/repository/stopJob";
        $http.post(url,{quartzId:quartzId}).then(function (resp) {

            alert(resp.data.msgInfo);
            $scope.getQuartzJobList();
        })
    };

    $scope.executeJobByHand = function(quartzId){

        var url = "/kettle/quartz/repository/executeJobByHand";
        $http.post(url,{quartzId:quartzId}).then(function (resp) {

            alert(resp.data.msgInfo);
        })
    };


    $scope.removeQuartzJob = function(quartzId){

        var url = "/kettle/quartz/repository/removeQuartzJob";
        $http.post(url,{quartzId:quartzId}).then(function (resp) {

            if (resp.data.state == '200'){
                alert(resp.data.msgInfo);
                $scope.getQuartzJobList();
            }else {
                alert(resp.data.msgInfo);
            }
        })
    };

    $scope.openTab = function (title,url,w,h,full) {

        url = url +"?repositoryId="+ $scope.repositoryId;

        if (title == null || title == '') {
            var title=false;
        };
        if (url == null || url == '') {
            var url="404.html";
        };
        if (w == null || w == '') {
            var w=($(window).width()*0.9);
        };
        if (h == null || h == '') {
            var h=($(window).height() - 50);
        };
        var index = layer.open({
            type: 2,
            area: [w+'px', h +'px'],
            fix: false, //不固定
            maxmin: true,
            shadeClose: true,
            shade:0.4,
            title: title,
            content: url
        });
        if(full){
            layer.full(index);
        }
    }


    function GetQueryString(name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) return unescape(r[2]);
        return null;
    }

    Date.prototype.format = function (fmt) {
        var o = {
            "M+": this.getMonth() + 1, //月份
            "d+": this.getDate(), //日
            "h+": this.getHours(), //小时
            "m+": this.getMinutes(), //分
            "s+": this.getSeconds(), //秒
            "q+": Math.floor((this.getMonth() + 3) / 3), //季度
            "S": this.getMilliseconds() //毫秒
        };
        if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
        for (var k in o)
            if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        return fmt;
    };
}
