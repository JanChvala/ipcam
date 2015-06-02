'use strict';
/*jshint esnext: true */

var moduleName = 'jch-directives';

class JchToolbarActions {
    constructor() {
        this.restrict = 'E';
        this.templateUrl = 'components/jch-directives/jch-toolbar-actions/jch-toolbar-actions.html';
    }
}

class JchToolbarActionsCtrl {
    constructor($scope) {
        $scope.date = new Date();
    }

    static directiveFactory() {
        JchToolbarActions.instance = new JchToolbarActions();
        return JchToolbarActions.instance;
    }
}
JchToolbarActionsCtrl.$inject = ['$scope'];


angular.module(moduleName)
    .controller('JchToolbarActionsCtrl', JchToolbarActionsCtrl)
    .directive('jchToolbarActions', JchToolbarActionsCtrl.directiveFactory);

export default JchToolbarActionsCtrl;
