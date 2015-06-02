var common = require(__dirname + '/../../common');
var express = require('express');

module.exports.register = function (app, moduleName) {
	var baseUrl = '/subdomain/' + moduleName + '/';
	var apiUrl = baseUrl + 'api/';

	var publicDir = __dirname + "/public/";
	var routesDir = __dirname + "/routes/";

	common.registerRoutes(app, apiUrl, routesDir);
	app.use(baseUrl, express.static(publicDir));
};
