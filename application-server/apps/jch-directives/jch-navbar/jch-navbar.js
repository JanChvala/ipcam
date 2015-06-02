'use strict';
/*jshint esnext: true */

var moduleName = 'jch-directives';

class JchNavbar {
    constructor() {
        this.restrict = 'E';
        this.templateUrl = 'components/jch-directives/jch-navbar/jch-navbar.html';
    }
}

class JchNavbarCtrl {
    constructor($scope) {
        $scope.date = new Date();
    }

    static directiveFactory() {
        JchNavbar.instance = new JchNavbar();
        return JchNavbar.instance;
    }
}
JchNavbarCtrl.$inject = ['$scope'];


angular.module(moduleName)
    .controller('JchNavbarCtrl', JchNavbarCtrl)
    .directive('jchNavbar', JchNavbarCtrl.directiveFactory);

export default JchNavbarCtrl;
