'use strict';
/*jshint esnext: true */

var moduleName = 'jch-directives';

class JchFooter {
    constructor() {
        this.restrict = 'E';
        this.templateUrl = 'components/jch-directives/jch-footer/jch-footer.html';
    }
}

class JchFooterCtrl {
    constructor($scope) {
        $scope.date = new Date();
        $scope.loveUrl = 'http://www.janchvala.cz/ahoj-ja/';
        $scope.loveFromWho = '@jch';
    }

    static directiveFactory() {
        JchFooter.instance = new JchFooter();
        return JchFooter.instance;
    }
}
JchFooterCtrl.$inject = ['$scope'];


angular.module(moduleName)
    .controller('JchFooterCtrl', JchFooterCtrl)
    .directive('jchFooter', JchFooterCtrl.directiveFactory);

export default JchFooterCtrl;
