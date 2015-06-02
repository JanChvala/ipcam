var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var compression = require('compression');
var bodyParser = require('body-parser');

var passport = require('passport');
var subdomain = require('subdomain');

var session = require('express-session');

var app = express();

// Connect to the MongoDB
var mongoose = require('mongoose');
var mongoOpts = {
    user: 'chvala',
    pass: 'eanorauphaiz'
};
mongoose.connect('mongodb://pluto.rosti.cz/chvala', mongoOpts);

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

// uncomment after placing your favicon in /public
app.use(favicon(__dirname + '/public/favicon.ico'));
app.use(compression());
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: false}));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

// Use the passport package in our application
app.use(passport.initialize());

// Use express session support since OAuth2orize requires it
app.use(session({
    secret: 'Super Secret Session Key 222545454',
    saveUninitialized: true,
    resave: true
}));

// common file which exposes configuration
var common = require('./common');
var config = common.config();

// Subdomain settings
var subdomainOptions = {
    base: config.serverName
};
app.use(subdomain(subdomainOptions));

// register each app project on subdomain
common.registerApplications(app, __dirname + "/apps/");

// basic domain routes
// redirect to blog
app.get('/', function (req, res) {
    res.redirect('http://www.janchvala.cz');
});

// basic domain routes
// redirect to blog
app.get('/:path*', function (req, res) {
    console.log('redirecting: ' + req.params.path);
    res.redirect('http://www.janchvala.cz/' + req.params.path);
});


// catch 404 and forward to error handler
app.use(function (req, res, next) {
    var err = new Error();
    err.status = 404;
    next(err);
});

// error handlers

// development error handler
// will print stacktrace
if (app.get('env') === 'development') {
    app.use(function (err, req, res, next) {
        res.status(err.status || 500);
        res.render('error', {
            message: err.message,
            error: err
        });
    });
}

// production error handler
// no stack traces leaked to user
app.use(function (err, req, res, next) {
    res.status(err.status || 500);
    res.render('error', {
        message: err.message,
        error: {}
    });
});


module.exports = app;
