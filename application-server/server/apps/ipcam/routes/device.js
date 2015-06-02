var express = require('express');
var router = express.Router();

// controllers
var deviceController = require(__dirname + '/../controllers/device');
var authController = require(__dirname + '/../controllers/auth');

// Create a new route with the prefix /devices
router.route('/devices')
    .get(authController.isAuthenticated, deviceController.getDevices)
    .post(authController.isAuthenticated, deviceController.postDevices);

// Create a new route with the /devices/:beer_id prefix
router.route('/devices/:device_id')
    .get(authController.isAuthenticated, deviceController.getDevice)
    .put(authController.isAuthenticated, deviceController.putDevice)
    .delete(authController.isAuthenticated, deviceController.deleteDevice);

router.route('/devices/:device_id/start-stream')
    .get(deviceController.startDeviceStreaming);

module.exports = router;
