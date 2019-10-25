var indexApp = angular.module('indexApp', []);


indexApp.config(function ($httpProvider) {
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

indexApp.controller('indexController', indexController);

function indexController($scope, $http) {

    $scope.repositoryId = GetQueryString("repositoryId");

    $scope.init = function () {

    };

    $scope.loginOut = function () {
        var url = "/kettle/repository/exitRepository";

        $http.post(url, {repositoryId: $scope.repositoryId}).then(function (resp) {

            if (resp.data.state == '200') {
                $("#loading-info").show();
                location.href = 'login.html'
            }
        })
    };

    $scope.addTab = function (title, url, is_refresh) {
        url = url + "?repositoryId=" + $scope.repositoryId;

        console.log(url);

        var id = md5(url);//md5每个url

        //重复点击
        for (var i = 0; i < $('.x-iframe').length; i++) {
            if ($('.x-iframe').eq(i).attr('tab-id') == id) {
                element.tabChange('xbs_tab', id);
                if (is_refresh)
                    $('.x-iframe').eq(i).attr("src", $('.x-iframe').eq(i).attr('src'));
                return;
            }
        }
        ;

        $scope.add_lay_tab(title, url, id);
        $scope.set_data(title, url, id);
        element.tabChange('xbs_tab', id);

    };

    $scope.add_lay_tab = function (title, url, id) {
        element.tabAdd('xbs_tab', {
            title: title
            ,
            content: '<iframe tab-id="' + id + '" frameborder="0" src="' + url + '" scrolling="yes" class="x-iframe"></iframe>'
            ,
            id: id
        })
    }

    $scope.set_data = function (title, url, id) {

        if (typeof is_remember != "undefined")
            return false;

        layui.data('tab_list', {
            key: id
            , value: {title: title, url: url}
        });
    };

    function GetQueryString(name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) return unescape(r[2]);
        return null;
    }

}
