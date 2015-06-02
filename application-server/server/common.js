var env = require('./env.json');
var fs = require('fs');
var path = require('path');

exports.getDirectoriesInDirectory = function (srcpath) {
    return fs.readdirSync(srcpath).filter(function (file) {
        return fs.statSync(path.join(srcpath, file)).isDirectory();
    });
};

exports.getFilesInDirectory = function (srcpath) {
    return fs.readdirSync(srcpath).filter(function (file) {
        return fs.statSync(path.join(srcpath, file)).isFile();
    });
};

exports.registerRoutes = function (app, url, routeDir) {
    var routes = exports.getFilesInDirectory(routeDir);
    for (var i = 0; i < routes.length; i++) {
        var routeName = routes[i];
        var router = require(routeDir + routeName);
        app.use(url, router);
    }
};

exports.registerApplications = function (app, appsDir) {
    var apps = exports.getDirectoriesInDirectory(appsDir);
    for (var i = 0; i < apps.length; i++) {
        var appName = apps[i];
        var appModule = require(appsDir + appName + '/module');
        appModule.register(app, appName);
    }
};

exports.config = function () {
    //var node_env = 'production';
    var node_env = 'development';
    return env[node_env];
};
