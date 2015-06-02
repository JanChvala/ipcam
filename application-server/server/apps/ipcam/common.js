var common = require(__dirname + '/../../common');
var express = require('express');

function registerModule(app, moduleName) {
    var baseUrl = '/subdomain/' + moduleName + '/';
    var apiUrl = baseUrl + 'api/';

    var publicDir = __dirname + "/public/";
    var routesDir = __dirname + "/routes/";

    common.registerRoutes(app, apiUrl, routesDir);
    app.use(baseUrl, express.static(publicDir));
}

function getRandomInt(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

function generateUID(len) {
    var buf = []
        , chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
        , charlen = chars.length;

    for (var i = 0; i < len; ++i) {
        buf.push(chars[getRandomInt(0, charlen - 1)]);
    }

    return buf.join('');
}

module.exports.register = registerModule;
module.exports.getRandomInt = getRandomInt;
module.exports.generateUID = generateUID;
