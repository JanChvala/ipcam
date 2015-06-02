'use strict';
/*jshint esnext: true */

var moduleName = 'jch-directives';

class JchSidebar {
    constructor() {
        this.restrict = 'E';
        this.templateUrl = 'components/jch-directives/jch-sidebar/jch-sidebar.html';
    }
}

class JchSidebarCtrl {
    constructor($scope) {
        $scope.date = new Date();
    }

    static directiveFactory() {
        JchSidebar.instance = new JchSidebar();
        return JchSidebar.instance;
    }
}
JchSidebarCtrl.$inject = ['$scope'];


angular.module(moduleName)
    .controller('JchSidebarCtrl', JchSidebarCtrl)
    .directive('jchSidebar', JchSidebarCtrl.directiveFactory);

export default JchSidebarCtrl;
