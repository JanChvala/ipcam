'use strict';
/*jshint esnext: true */

import * as config from './config';

var moduleName = 'jch-app.ipcam.pages';
angular.module(moduleName, ['jch-app.ipcam.config']);

import * as main from './main/main.controller'; // jshint ignore:line

export default moduleName;
export const name = moduleName;


function createState(url, name, templateUrl, controller, pageTitle) {
  var result = {};
  result.url = url;
  result.name = name;
  result.templateUrl = 'app/' + templateUrl;
  result.controller = controller;
  result.pageTitle = pageTitle;
  result.showInSideNav = true;
  return result;
}

function createAction(id, name, icon) {
  var result = {};
  result.id = id;
  result.name = name;
  result.icon = icon;
  return result;
}

export function configure($stateProvider, $urlRouterProvider) {
  var home = createState('/', 'home', 'main/main.html', 'MainPageCtrl', 'Android IP camera');

  $stateProvider
    .state(home);

  $urlRouterProvider.otherwise('/');
}
