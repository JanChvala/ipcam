var express = require('express');
var router = express.Router();

// controllers
var oauth2Controller = require(__dirname + '/../controllers/oauth2');
var authController = require(__dirname + '/../controllers/auth');

// Create endpoint handlers for oauth2 authorize
router.route('/oauth2/authorize')
    .get(authController.isAuthenticated, oauth2Controller.authorization)
    .post(authController.isAuthenticated, oauth2Controller.decision);

// Create endpoint handlers for oauth2 token
router.route('/oauth2/token')
    .post(authController.isClientAuthenticated, oauth2Controller.token);

module.exports = router;
