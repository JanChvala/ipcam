'use strict';
/*jshint esnext: true */

import * as pages from './pages.module';
import * as directives from './../components/jch-directives/jch-directives';

function config($stateProvider, $urlRouterProvider, $mdThemingProvider) {
    $mdThemingProvider.theme('default')
        .primaryPalette('cyan')
        .accentPalette('amber');

    pages.configure($stateProvider, $urlRouterProvider);
}
config.$inject = ['$stateProvider', '$urlRouterProvider', '$mdThemingProvider'];

function appController($rootScope, $state, $mdSidenav) {
    $rootScope.getState = function (stateName) {
        return $state.get(stateName);
    };

    $rootScope.isIncludedActiveState = function (stateName) {
        return $state.includes(stateName);
    };

    $rootScope.getBreadCrumbs = function () {
        var parent = $state.get($state.$current.name);
        var breadCrumbs = [];
        while (parent && parent.name && parent.name !== '') {
            breadCrumbs.push(parent);
            parent = parent.parent;
        }

        return breadCrumbs.reverse();
    };

    $rootScope.getActions = function () {
        var s = $state.get($state.$current.name);
        return s.actions;
    };

    $rootScope.toggleSideNav = function (which) {
        $mdSidenav(which).toggle();
    };

    $rootScope.actionItemClick = function (item) {
        $rootScope.$broadcast('actionItemClicked', {item: item});
    };

    $rootScope.$on('$stateChangeSuccess', function (event, toState, toParams, fromState, fromParams) {// jshint ignore:line
        //$mdSidenav('left').close();
    });
}
appController.$inject = ['$rootScope', '$state', '$mdSidenav'];

function directiveResize($window) {
    console.log('resizing');
    return function (scope, element) {
        scope.$watch(function () {
                return {h: $window.innerHeight, w: $window.innerWidth};
            }, function (newValue, oldValue) {// jshint ignore:line
                scope.windowHeight = newValue.h;
                scope.windowWidth = newValue.w;

                element.style = function () {
                    return {
                        height: (newValue.h - 100) + 'px',
                        width: (newValue.w - 100) + 'px'
                    };
                };
            }, true);

        angular.element($window).bind('resize', function() {
            scope.$apply();
        });
    };
}
directiveResize.$inject = ['$window'];


angular.module('jch-app.ipcam', ['ngAnimate', 'ngCookies', 'ngTouch', 'ngSanitize', 'restangular', 'ui.router', 'ngMaterial', 'ngMdIcons', pages.name, directives.name])
    .config(config)
    .controller('AppCtrl', appController)
    .directive('resize', directiveResize);
