'use strict';
/*jshint esnext: true */

var moduleName = 'jch-directives';

class JchAppLoader {
    constructor() {
        this.restrict = 'E';
        this.transclude = true;
        this.templateUrl = 'components/jch-directives/jch-app-loader/jch-app-loader.html';
    }
}

class JchAppLoaderCtrl {
    constructor($scope) {
        $scope.appLoaded = true;
    }

    static directiveFactory() {
        JchAppLoader.instance = new JchAppLoader();
        return JchAppLoader.instance;
    }
}
JchAppLoaderCtrl.$inject = ['$scope'];


angular.module(moduleName)
    .controller('JchAppLoaderCtrl', JchAppLoaderCtrl)
    .directive('jchAppLoader', JchAppLoaderCtrl.directiveFactory);

export default JchAppLoaderCtrl;
