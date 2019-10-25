
var logApp = angular.module('logApp', [
]);


logApp.config(function ($httpProvider) {
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

logApp.controller('logController', logController);

function logController($scope, $http) {

    $scope.repositoryId = GetQueryString("repositoryId");

    $scope.init = function () {

        $scope.getLogList(1);

    };

    $scope.getLogList = function(pageIndex){
        var url = "/kettle/log/getLogList";

        console.log($scope.name);

        if ($scope.name == undefined){
            $scope.name = "";
        }

        $http.post(url,{repositoryId:$scope.repositoryId,name:$scope.name,pageIndex:pageIndex,pageSize:10}).then(function (value) {
            if (value.data.state == "200"){
                $scope.logList = value.data.data;

                $scope.logPage = value.data.page;

                $scope.logPageList = $scope.calculateIndexes(pageIndex,$scope.logPage.totalPage,3);
            }
        })

    };

    $scope.toPgae = function (a) {
        $scope.getLogList(a);
    };

    function GetQueryString(name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) return unescape(r[2]);
        return null;
    }

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
