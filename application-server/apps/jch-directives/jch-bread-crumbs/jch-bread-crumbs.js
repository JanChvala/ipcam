'use strict';
/*jshint esnext: true */

var moduleName = 'jch-directives';

class JchBreadCrumbs {
    constructor() {
        this.restrict = 'E';
        this.templateUrl = 'components/jch-directives/jch-bread-crumbs/jch-bread-crumbs.html';
    }
}

class JchBreadCrumbsCtrl {
    constructor($scope) {
        $scope.date = new Date();
    }

    static directiveFactory() {
        JchBreadCrumbs.instance = new JchBreadCrumbs();
        return JchBreadCrumbs.instance;
    }
}
JchBreadCrumbsCtrl.$inject = ['$scope'];


angular.module(moduleName)
    .controller('JchBreadCrumbsCtrl', JchBreadCrumbsCtrl)
    .directive('jchBreadCrumbs', JchBreadCrumbsCtrl.directiveFactory);

export default JchBreadCrumbsCtrl;
