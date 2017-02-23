(function () {
    angular.module("OfficeManagementSystem")
        .controller("LoginController", ["$scope", "$http", "$cookies", "$resource", "$routeParams", "$httpParamSerializer",
            function ($scope, $http, $cookies, $resource, $routeParams, $httpParamSerializer) {
                $scope.personCredentials = {
                    grant_type: "password",
                    username: "",
                    password: "",
                    client_id: "client",
                    scope: "read write"
                };

                $scope.showRecoverInput = false;

                var registrationToken = $routeParams.registrationToken;

                if (!!registrationToken) {
                    $http.get("/api/v1/registration/" + registrationToken)
                        .then(function (callback) {
                            $scope.personCredentials.username = callback.data.email;
                        }, function () {
                            console.log("Registration error")
                        })
                }

                $scope.recoverEmail = "";


                $scope.encoded = btoa("client");

                $scope.sendPersonCredentials = function () {
                    var req = {
                        method: 'POST',
                        url: "http://localhost:8080/oauth/token",
                        headers: {
                            "Authorization": "Basic " + $scope.encoded + "Og==",
                            "Content-type": "application/x-www-form-urlencoded; charset=utf-8"
                        },
                        data: $httpParamSerializer($scope.personCredentials)
                    };
                    $http(req).then(function (callback) {
                        console.log(callback);
                        $http.defaults.headers.common.Authorization =
                            'Bearer ' + callback.data.access_token;
                        $cookies.put("access_token", callback.data.access_token);
                        window.location.href = "/demo";
                    }, function(callback) {
                        console.log(callback);
                    });
                };

                $scope.$on('oauth:login', function (event, token) {
                    $http.defaults.headers.common.Authorization = 'Bearer ' + token.access_token;
                });

                // $scope.foo = {id: 0, name: "sample foo"};
                // $scope.foos = $resource(
                //     "http://localhost:8080/spring-security-oauth-resource/foos/:fooId",
                //     {fooId: '@id'});
                // $scope.getFoo = function () {
                //     $scope.foo = $scope.foos.get({fooId: $scope.foo.id});
                // };

            }])
})();