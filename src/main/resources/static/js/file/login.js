var loginApp = angular.module('loginApp', []);


loginApp.config(function ($httpProvider) {
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

loginApp.controller('loginController', loginController);

function loginController($scope, $http) {

    $scope.queryModel = {
        id: "",
        user: "",
        password: ""
    };


    $scope.init = function () {

        $scope.getRepositoryList();

    };

    $scope.getRepositoryList = function () {

        var url = "/kettle/repository/getRepositoryList";

        $http.post(url).then(function (resp) {

            if (resp.data.state == '200') {
                $scope.repositoryList = resp.data.data;
            }

        })
    };

    $scope.login = function () {
        var url = "/kettle/repository/loginRepository";

        console.log($scope.queryModel);

        $http.post(url, {
            repositoryId: $scope.queryModel.id,
            user: $scope.queryModel.user,
            password: $scope.queryModel.password
        }).then(function (resp) {

            if (resp.data.state == '200') {
                $("#loading-info").show();
                location.href = 'index.html?repositoryId=' + $scope.queryModel.id;
            }
        })
    };

}
