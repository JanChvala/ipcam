'use strict';
/*jshint esnext: true */

var moduleName = 'jch-app.ipcam.pages';

function DialogController($scope, $mdDialog) {
  $scope.hide = function () {
    $mdDialog.hide();
  };

  $scope.cancel = function () {
    $mdDialog.cancel();
  };

  $scope.answer = function (answer) {
    console.log("calling: " + answer);
    $mdDialog.hide(answer);
  };
}


class MainPageCtrl {
  constructor($rootScope, $scope, $mdDialog, $window) { // jshint ignore:line
    $scope.makeCall = function (ev) {
      console.log('opening call dialog');
      $mdDialog.show({
        controller: DialogController,
        templateUrl: 'dialog.call.tmpl.html',
        targetEvent: ev
      })
        .then(function (answer) {
          console.log($scope);
          var callerID = $rootScope.callerID;
          $rootScope.callerID = '';
          $window.location.href = "assets/ipcamera.html?room=" + callerID;
        }, function () {});
    };
  }


}
MainPageCtrl.$inject = ['$rootScope', '$scope', '$mdDialog', '$window'];

angular.module(moduleName)
  .controller('MainPageCtrl', MainPageCtrl)
  .filter('trusted', ['$sce', function ($sce) {
    return function (url) {
      return $sce.trustAsResourceUrl(url);
    };
  }]);

export default MainPageCtrl;
