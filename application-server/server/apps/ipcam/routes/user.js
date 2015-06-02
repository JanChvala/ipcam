var express = require('express');
var router = express.Router();

// controllers
var userController = require(__dirname + '/../controllers/user');
var authController = require(__dirname + '/../controllers/auth');

// Create a new route with the prefix /users
router.route('/users')
    .get(authController.isAuthenticated, userController.getUsers)
    .post(userController.postUsers);

module.exports = router;
