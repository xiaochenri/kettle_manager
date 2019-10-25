
var addApp = angular.module('addApp', [
]);


addApp.config(function ($httpProvider) {
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

addApp.controller('addController', addController);

function addController($scope, $http) {

    $scope.repositoryId = GetQueryString("repositoryId");

    /**查询参数*/
    $scope.queryModel = {
        path:"",
        jobType:"",
        jobName:"",
        cron:"",
        repositoryId:""
    };

    $scope.init = function () {
        $scope.getRepositoryDirList();
    };

    $scope.getRepositoryDirList = function(){

        var url = "/kettle/repository/getRepositoryDirList";

        $http.post(url,{repositoryId:$scope.repositoryId}).then(function (value) {
            if (value.data.state == "200"){
                $scope.dirList = value.data.data;
            }
        })
    };

    $scope.getJobList = function(){
        console.log($scope.queryModel);

        var url = "";
        if ($scope.queryModel.jobType == "1"){
            url = "/kettle/repository/getRepositoryJobs";
        } else if ($scope.queryModel.jobType == "2"){
            url = "/kettle/repository/getRepositoryTrans";
        }

        $http.post(url,{repositoryId:$scope.repositoryId,path:$scope.queryModel.path}).then(function (value) {
            if (value.data.state == "200"){
                $scope.jobList = value.data.data;
                console.log($scope.jobList);
            }
        });
    };

    $scope.addQuartzJob = function(){
        
        if (!$scope.queryModel.jobType){
            alert("请选择任务类型");
            return;
        }
        if (!$scope.queryModel.path){
            alert("请选择任务路径");
            return;
        }
        if (!$scope.queryModel.jobName){
            alert("请选择任务名称");
            return;
        }
        if (!$scope.queryModel.cron){
            alert("请输入任务定时触发参数");
            return;
        }
        
        var url = "/kettle/quartz/repository/addQuartzJob";
        $scope.queryModel.repositoryId = $scope.repositoryId;
        $http.post(url,$scope.queryModel).then(function (value) {
            if (value.data.state == "200"){
                alert(value.data.msgInfo);
            }
        })

    };

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

    <!--1 4 3-->
    $scope.calculateIndexes = function (current, length, displayLength) {
        var indexes = [];
        var start = Math.round(current - displayLength / 2);
        var end = Math.round(current + displayLength / 2);
        if (length <= displayLength) {
            start = 1;
            end = length;
        } else {
            if (start <= 1) {
                start = 1;
                end = start + displayLength - 1;
            }
            if (end > length - 1) {
                end = length;
                start = end - displayLength + 1;
            }
        }
        for (var i = start; i <= end; i++) {
            indexes.push(i);
        }
        return indexes;
    };

}
