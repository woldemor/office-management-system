(function () {
    angular.module("OfficeManagementSystem")
        .controller("NewRequestController", ["$scope", "$http","$routeParams",
            function ($scope, $http, $routeParams) {

                $scope.pageTitle = "";
                $scope.update = false;

                if (!$routeParams.requestId){
                    $scope.pageTitle = "New request";
                    $scope.requestCredentials = {
                        priority:'2'
                    };
                    $scope.requestCredentials.description = null;
                    $scope.requestCredentials.name = null;
                    $scope.requestCredentials.estimate = null;
                } else {
                    $scope.pageTitle = "Update request";
                    $scope.update = true;
                    function formatDate(dateVal) {
                        var newDate = new Date(dateVal);
                        var sMonth = padValue(newDate.getMonth() + 1);
                        var sDay = padValue(newDate.getDate());
                        var sYear = newDate.getFullYear();
                        var sHour = newDate.getHours();
                        var sMinute = padValue(newDate.getMinutes());
                        var sAMPM = "AM";
                        var iHourCheck = parseInt(sHour);

                        if (iHourCheck > 12) {
                            sAMPM = "PM";
                            sHour = iHourCheck - 12;
                        }
                        else if (iHourCheck === 0) {
                            sHour = "12";
                        }
                        sHour = padValue(sHour);
                        return sMonth + "/" + sDay + "/" + sYear + " " + sHour + ":" + sMinute + " " + sAMPM;
                    }

                    function padValue(value) {
                        return (value < 10) ? "0" + value : value;
                    }


                    $scope.getRequestCredential = function () {
                        $http.get("/api/request/" + $routeParams.requestId )
                            .then(function (callback) {
                                $scope.requestCredentials = callback.data;
                                if ($scope.requestCredentials.estimate!=null){
                                    $scope.requestCredentials.estimate = new Date($scope.requestCredentials.estimate);
                                }
                                // if ($scope.requestCredentials.manager == null){
                                //     $("#input-manager").parents(".col-md-offset-3").fadeOut();
                                //     $scope.requestCredentials.manager = null;
                                // }
                                // else {
                                //     $scope.selectedManager = $scope.requestCredentials.manager;
                                // }
                                // if ($scope.requestCredentials.requestGroup == null) {
                                //     $("#input-request-group").parents(".col-md-offset-3").fadeOut();
                                // }
                                // else {
                                //     $scope.requestCredentials.requestGroup = $scope.requestCredentials.requestGroup.name;
                                // }
                                if ($scope.requestCredentials.manager!=null) {
                                    $scope.requestCredentials.manager = $scope.requestCredentials.manager.id;
                                }
                                if ($scope.requestCredentials.parent!=null) {
                                    $scope.requestCredentials.parent = $scope.requestCredentials.parent.id;
                                }
                                if ($scope.requestCredentials.requestGroup!=null) {
                                    $scope.requestCredentials.requestGroup = $scope.requestCredentials.requestGroup.id;
                                }
                                $scope.requestCredentials.priority = $scope.requestCredentials.priority.id;
                                $scope.requestCredentials.status = $scope.requestCredentials.status.id;
                                $scope.requestCredentials.employee = $scope.requestCredentials.employee.id;
                                console.dir($scope.requestCredentials);
                            }, function (callback) {
                                console.log(callback)
                            })
                    };


                    $scope.getRequestCredential();
                }

                $scope.wrongRequestNameMessage = "Name must contain at least 3 letters";
                $scope.requestNameRegExp = /^(([a-zA-Z\d]+)\s?\1?){3,}$/;

                $scope.updateRequestCredentials = function () {
                    /*if ($scope.calendarClick==true) {
                        $scope.requestCredentials.estimate = new Date($('#datetimepicker1').data('date')).getTime();
                    } else if ($scope.estimateTime!=undefined && $scope.calendarClick==false){
                        $scope.requestCredentials.estimate = new Date($scope.estimateTime).getTime();
                    } else if ($scope.estimateTime==undefined){
                        $scope.requestCredentials.estimate = null;
                    }*/
                    $http.put("/api/request/" + $routeParams.requestId + "/update/", $scope.requestCredentials)
                        .then(function (callback) {
                            window.location = "/requestListByEmployee";
                        }, function (callback) {
                            console.log("Updating request Failure!");
                            console.log($scope.requestCredentials)
                        })
                };
                
                $scope.sendRequestCredentials = function () {
                    //$scope.requestCredentials.estimate = new Date($('#datetimepicker1').data('date')).getTime();
                    $http.post("/api/request/addRequest", $scope.requestCredentials)
                        .then(function (callback) {
                            $scope.name = callback.data.name;
                            window.location = "/requestListByEmployee";
                        }, function (callback) {
                            console.log("Creating request Failure!")
                        })
                }




            }])
})();