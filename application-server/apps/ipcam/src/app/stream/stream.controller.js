'use strict';
/*jshint esnext: true */

var moduleName = 'jch-app.ipcam.pages';

function generateUUID() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
    var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}

function createConnection($scope, roomID, roomPassword) {
  console.log("##### roomID: " + roomID);
  console.log("##### roomPassword: " + roomPassword);
  var connection = new RTCMultiConnection();

  connection.session = {
    audio: false,
    video: true,
    oneway: false
  };

  connection.mediaConstraints.video = {
    mandatory: {
      minWidth: 300,
      minHeight: 200
    },
    optional: [
      {width: {min: 1900}},
      {frameRate: {min: 10, max: 60}},
      {width: {min: 1200}},
      {width: {min: 1000}},
      {width: {min: 600}},
      {width: {min: 400}}
    ]
  };

  connection.sdpConstraints.mandatory = {
    OfferToReceiveAudio: true,
    OfferToReceiveVideo: true
  };

  connection.onstream = function (stream) {
    console.log("#### onstream");
    console.log(stream);
    if (stream.type == 'local') {
      $scope.videoAvailable = true;
      //$scope.videoSrc = stream.blobURL;
      var videos = document.querySelector('#videos');
      while (videos.firstChild) videos.removeChild(videos.firstChild);

      stream.mediaElement.autoplay = true;
      stream.mediaElement.controls = false;
      stream.mediaElement.style.width = '100%';
      stream.mediaElement.style.height = '100%';
      videos.appendChild(stream.mediaElement);
      $scope.$apply();
    }
  };

  connection.onJoinWithPassword = function (remoteUserId) {
    console.log('################### onJoinWithPassword');
    var password = prompt(remoteUserId + ' is password protected. Please enter pasword:');
    connection.openOrJoin(remoteUserId, password);
  };

  connection.onInvalidPassword = function (remoteUserId, oldPassword) {
    console.log('################### onInvalidPassword');
    var password = prompt(remoteUserId + ' is password protected. Your entered wrong password (' + oldPassword + '). Please enter valid pasword:');
    connection.openOrJoin(remoteUserId, password);
  };

  connection.onPasswordMaxTriesOver = function (remoteUserId) {
    console.log('################### onPasswordMaxTriesOver');
    alert(remoteUserId + ' is password protected. Your max password tries exceeded the limit.');
  };

  connection.onNewParticipant = function(participantId, userPreferences) {
    console.log('################### onNewParticipant');
    connection.acceptParticipationRequest(participantId, userPreferences);
  };

  connection.openOrJoinOpen(roomID, roomPassword);

  return connection;
}

class CameraStreamPageCtrl {
  constructor($rootScope, $scope, $location, $stateParams) { // jshint ignore:line
    $rootScope.roomID = $stateParams.cameraHash;

    $scope.videoAvailable = false;
    window.connection = createConnection($scope, $rootScope.roomID, $stateParams.streamPassword);
  }
}
CameraStreamPageCtrl.$inject = ['$rootScope', '$scope', '$location', '$stateParams'];

angular.module(moduleName)
  .controller('CameraStreamPageCtrl', CameraStreamPageCtrl)
  .filter('trusted', ['$sce', function ($sce) {
    return function (url) {
      return $sce.trustAsResourceUrl(url);
    };
  }]);

export default CameraStreamPageCtrl;
