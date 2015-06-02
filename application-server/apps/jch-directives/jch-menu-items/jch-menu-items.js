'use strict';
/*jshint esnext: true */

var moduleName = 'jch-directives';

class JchMenuItems {
    constructor() {
        this.restrict = 'E';
        this.templateUrl = 'components/jch-directives/jch-menu-items/jch-menu-items.html';
    }
}

class JchMenuItemsCtrl {
    constructor($scope) {
        $scope.date = new Date();
    }

    static directiveFactory() {
        JchMenuItems.instance = new JchMenuItems();
        return JchMenuItems.instance;
    }
}
JchMenuItemsCtrl.$inject = ['$scope'];


angular.module(moduleName)
    .controller('JchMenuItemsCtrl', JchMenuItemsCtrl)
    .directive('jchMenuItems', JchMenuItemsCtrl.directiveFactory);

export default JchMenuItemsCtrl;
