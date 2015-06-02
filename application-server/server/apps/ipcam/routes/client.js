var express = require('express');
var router = express.Router();

// controllers
var clientController = require(__dirname + '/../controllers/client');
var authController = require(__dirname + '/../controllers/auth');

// Create endpoint handlers for /clients
router.route('/clients')
    .get(authController.isAuthenticated, clientController.getClients)
    .post(authController.isAuthenticated, clientController.postClients);

module.exports = router;
