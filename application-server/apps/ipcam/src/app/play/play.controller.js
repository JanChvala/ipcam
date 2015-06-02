'use strict';
/*jshint esnext: true */

var moduleName = 'jch-app.ipcam.pages';

function createConnection($timeout, $scope, roomID, roomPassword) {
  console.log("##### roomID: " + roomID);
  console.log("##### roomPassword: " + roomPassword);

  var connection = new RTCMultiConnection();

  connection.session = {
    audio: false,
    video: true,
    oneway: true
  };

  connection.sdpConstraints.mandatory = {
    OfferToReceiveAudio: false,
    OfferToReceiveVideo: false
  };

  connection.onstream = function (stream) {
    console.log("#### onstream");
    console.log(stream);
    if (stream.type == 'remote') {
      // for the viewer there are only remote streams
      $scope.videoAvailable = true;
      //$scope.videoSrc = stream.blobURL;
      var videos = document.querySelector('#videos');
      while (videos.firstChild) videos.removeChild(videos.firstChild);

      stream.mediaElement.autoplay = true;
      stream.mediaElement.style.width = '100%';
      stream.mediaElement.style.height = '100%';
      videos.appendChild(stream.mediaElement);
      $scope.$apply();
    }
  };

  connection.onJoinWithPassword = function (remoteUserId) {
    var msg = document.querySelector('#progressContainer');
    msg.innerHTML = "The room is protected with password.";

    console.log('################### onJoinWithPassword');
    var password = prompt(remoteUserId + ' is password protected. Please enter pasword:');
    connection.openOrJoin(remoteUserId, password);
  };

  connection.onInvalidPassword = function (remoteUserId, oldPassword) {
    var msg = document.querySelector('#progressContainer');
    msg.innerHTML = "Invalid password.";

    console.log('################### onInvalidPassword');
    var password = prompt(remoteUserId + ' is password protected. Your entered wrong password (' + oldPassword + '). Please enter valid pasword:');
    connection.openOrJoin(remoteUserId, password);
  };

  connection.onPasswordMaxTriesOver = function (remoteUserId) {
    var msg = document.querySelector('#progressContainer');
    msg.innerHTML = "You have reached the limit of tries.";

    console.log('################### onPasswordMaxTriesOver');
    alert(remoteUserId + ' is password protected. Your max password tries exceeded the limit.');
  };

  connection.onNewParticipant = function(participantId, userPreferences) {
    console.log('################### onNewParticipant');
    connection.acceptParticipationRequest(participantId, userPreferences);
  };
  var tryCount = 0;
  var joinRoom = function() {
    connection.openOrJoinJoin(roomID, roomPassword, function(){
      var msg = document.querySelector('#progressContainer');

      if(++tryCount < 30){
        console.log('Room not ready yet: ' + tryCount);
        msg.innerHTML = "Waiting for IP camera to start streaming.";
        if(tryCount > 1){
          msg.innerHTML = msg.innerHTML + ' ' + tryCount + 's'
        }
        $timeout(joinRoom, 1000);
      } else {
        var msg = document.querySelector('#progressContainer');
        msg.innerHTML = "Sorry I tried to connect the IP camera but it does not seems to be streaming.";
      }
    });
  };

  joinRoom();

  return connection;
}

class CameraPlayPageCtrl {
  constructor($rootScope, $scope, $stateParams, $http, $timeout) { // jshint ignore:line
    $rootScope.roomID = $stateParams.cameraHash;
    $scope.videoAvailable = false;

    $http.get('api/devices/' + $rootScope.roomID + "/start-stream").
      success(function (data, status, headers, config) {
        // this callback will be called asynchronously
        // when the response is available
        console.log("Starting device success:");
        console.log(data);
        console.log(status);
        console.log(headers);
        console.log(config);
      }).
      error(function (data, status, headers, config) {
        // called asynchronously if an error occurs
        // or server returns response with an error status.
        console.log("Error starting device:");
        console.log(data);
        console.log(status);
        console.log(headers);
        console.log(config);
      });

    window.connection = createConnection($timeout, $scope, $rootScope.roomID, $stateParams.streamPassword);

    $scope.endCall = function () {
      console.log('end existing call not implemented yet');
      connection.close();
      window.location.href = '/';
    };
  }
}
CameraPlayPageCtrl.$inject = ['$rootScope', '$scope', '$stateParams', '$http', '$timeout'];

angular.module(moduleName)
  .controller('CameraPlayPageCtrl', CameraPlayPageCtrl)
  .filter('trusted', ['$sce', function ($sce) {
    return function (url) {
      return $sce.trustAsResourceUrl(url);
    };
  }]);

export default CameraPlayPageCtrl;
