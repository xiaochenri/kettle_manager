
var kettleJobApp = angular.module('kettleJobApp', [
]);


kettleJobApp.config(function ($httpProvider) {
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

kettleJobApp.controller('kettleJobController', kettleJobController);

function kettleJobController($scope, $http) {

    /**查询参数*/
    $scope.queryModel = {
        dir:"/",
        type:"trans"
    };

    $scope.repositoryId = GetQueryString("repositoryId");

    $scope.init = function () {
        $scope.getRepositoryDirList();
        $scope.getJobList();

        // setInterval(function () {
        //     $scope.getJobList();
        // }, 10000);
    };

    $scope.show = function(){
        console.log($scope.queryModel);
    };

    $scope.getRepositoryDirList = function(){

        var url = "/kettle/repository/getRepositoryDirList";

        $http.post(url,{repositoryId:$scope.repositoryId}).then(function (value) {
            if (value.data.state == "200"){
                $scope.dirList = value.data.data;
                console.log($scope.dirList);
            }
        })
    };

    $scope.getJobList = function(){
        var url = "";
        if ($scope.queryModel.type == "job"){
            url = "/kettle/repository/getRepositoryJobs";
        } else if ($scope.queryModel.type == "trans"){
            url = "/kettle/repository/getRepositoryTrans";
        }
        $("#loading-info").show();
        $http.post(url,{repositoryId:$scope.repositoryId,path:$scope.queryModel.dir}).then(function (value) {
            if (value.data.state == "200"){
                $("#loading-info").hide();
                $scope.jobList = value.data.data;
            }
        });
    };

    $scope.executeRepositoryJob = function(path,jobName){
        var url = "/kettle/repository/executeRepositoryJob";
        $("#loading-info").show();
        $http.post(url,{repositoryId:$scope.repositoryId,path:path,jobName:jobName}).then(function (resp) {
            $("#loading-info").hide();
            alert(resp.data.msgInfo);
            $scope.getJobList();
        })
    };

    $scope.stopRepositoryJob = function(path,jobName){
        var url = "/kettle/repository/stopRepositoryJob";
        $("#loading-info").show();
        $http.post(url,{repositoryId:$scope.repositoryId,path:path,jobName:jobName}).then(function (resp) {
            $("#loading-info").hide();
            alert(resp.data.msgInfo);
            $scope.getJobList();
        })
    };

    $scope.executeRepositoryTrans = function(path,transName){
        var url = "/kettle/repository/executeRepositoryTrans";
        $("#loading-info").show();
        $http.post(url,{repositoryId:$scope.repositoryId,path:path,transName:transName}).then(function (resp) {
            $("#loading-info").hide();
            alert(resp.data.msgInfo);
            $scope.getJobList();
        })
    };

    $scope.stopRepositoryTrans = function(path,transName){
        var url = "/kettle/repository/stopRepositoryTrans";
        $("#loading-info").show();
        $http.post(url,{repositoryId:$scope.repositoryId,path:path,transName:transName}).then(function (resp) {
            $("#loading-info").hide();
            alert(resp.data.msgInfo);
            $scope.getJobList();
        })
    };

    $scope.pauseRepositoryTrans = function(path,transName){
        var url = "/kettle/repository/pauseRepositoryTrans";
        $("#loading-info").show();
        $http.post(url,{repositoryId:$scope.repositoryId,path:path,transName:transName}).then(function (resp) {
            $("#loading-info").hide();
            alert(resp.data.msgInfo);
            $scope.getJobList();
        })
    };

    $scope.resumeRepositoryTrans = function(path,transName){
        var url = "/kettle/repository/resumeRepositoryTrans";
        $("#loading-info").show();
        $http.post(url,{repositoryId:$scope.repositoryId,path:path,transName:transName}).then(function (resp) {
            $("#loading-info").hide();
            alert(resp.data.msgInfo);
            $scope.getJobList();
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
