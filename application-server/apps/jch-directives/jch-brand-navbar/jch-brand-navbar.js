'use strict';
/*jshint esnext: true */

var moduleName = 'jch-directives';

class JchBrandNavbar {
    constructor() {
        this.restrict = 'E';
        this.templateUrl = 'components/jch-directives/jch-brand-navbar/jch-brand-navbar.html';
    }
}

class JchBrandNavbarCtrl {
    constructor($scope) {
        $scope.date = new Date();
    }

    static directiveFactory() {
        JchBrandNavbar.instance = new JchBrandNavbar();
        return JchBrandNavbar.instance;
    }
}
JchBrandNavbarCtrl.$inject = ['$scope'];


angular.module(moduleName)
    .controller('JchBrandNavbarCtrl', JchBrandNavbarCtrl)
    .directive('jchBrandNavbar', JchBrandNavbarCtrl.directiveFactory);

export default JchBrandNavbarCtrl;
