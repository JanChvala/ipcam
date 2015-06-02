var express = require('express');
var router = express.Router();

// controllers
var deviceController = require(__dirname + '/../controllers/device');

// Create a new route with the prefix /devices
router.route('/devices')
    .post(deviceController.postDevices);

router.route('/devices/:device_id/start-stream')
    .get(deviceController.startDeviceStreaming);

module.exports = router;
