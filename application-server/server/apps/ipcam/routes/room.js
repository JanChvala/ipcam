var express = require('express');
var router = express.Router();

// controllers
var roomController = require(__dirname + '/../controllers/room');
var authController = require(__dirname + '/../controllers/auth');

// Create a new route with the prefix /beers
router.route('/rooms')
    .get(authController.isAuthenticated, roomController.getRooms)
    .post(authController.isAuthenticated, roomController.postRooms);

// Create a new route with the /beers/:beer_id prefix
router.route('/rooms/:room_id')
    .get(authController.isAuthenticated, roomController.getRoom)
    .put(authController.isAuthenticated, roomController.putRoom)
    .delete(authController.isAuthenticated, roomController.deleteRoom);

module.exports = router;
