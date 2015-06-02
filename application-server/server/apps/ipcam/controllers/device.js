// models
var Device = require(__dirname + '/../models/device');
var common = require(__dirname + '/../common');

// GoogleCloudMessaging
var gcm = require('node-gcm');

// GET /api/devices
exports.getDevices = function (req, res) {
    // Use the Beer model to find all beer
    Device.find({userId: req.user._id}, function (err, devices) {
        if (err)
            res.send(err);

        res.json(devices);
    });
};

// POST /api/devices
exports.postDevices = function (req, res) {
    // Create a new instance of the Device model
    var device = new Device();

    // Set the device properties that came from the POST data
    device.name = req.body.name;
    device.gcmRegistrationId = req.body.gcmRegistrationId;

    device.userId = req.user._id;
    device.code = common.generateUID(8);
    device.lastUpdate = new Date();

    // Save the device and check for errors
    device.save(function (err) {
        if (err)
            res.send(err);

        res.json({token: device.code});
    });
};

function sendGcmMessageToDevice(gcmRegistrationId, callback){
    var message = new gcm.Message();
    message.addData('action', 'start-streaming');

    var regIds = [gcmRegistrationId];
    var sender = new gcm.Sender('AIzaSyCccl1qflhoB11mi4iQb761-L11zcG82-s');

    sender.send(message, regIds, callback);
}

// GET /api/devices/:device_id/start-stream
exports.startDeviceStreaming = function (req, res) {
    // Use the Device model to find a specific beer
    console.log(req.params);
    Device.find({code: req.params.device_id}, function (err, devices) {
        console.log(err);
        console.log(devices);
        if (err){
            res.send(err);
            return;
        }

        console.log(devices);

        if (devices[0] == undefined) {
            res.send("device not found.");
            return;
        }

        sendGcmMessageToDevice(devices[0].gcmRegistrationId, function (err, result) {
            if(err) {
                console.error(err);
                res.json({result: "error", message: "Sending GCM message failed."});
            }
            else {
                console.log(result);
                res.json({result: "success", message: "Sending GCM message successful."});
            }
        });
    });
};

// GET /api/devices/:device_id
exports.getDevice = function (req, res) {
    // Use the Device model to find a specific beer
    Device.find({userId: req.user._id, _id: req.params.device_id}, function (err, device) {
        if (err)
            res.send(err);

        res.json(device);
    });
};

// PUT /api/devices/:device_id
exports.putDevice = function (req, res) {
    // Use the Beer model to find a specific beer
    Device.update({userId: req.user._id, _id: req.params.device_id}, {lastUpdate: new Date()}, function (err, num, raw) {
        if (err)
            res.send(err);

        res.json({message: num + ' updated'});
    });
};

// DELETE /api/devices/:device_id
exports.deleteDevice = function (req, res) {
    // Use the Beer model to find a specific device and remove it
    Device.remove({userId: req.user._id, _id: req.params.device_id}, function (err) {
        if (err)
            res.send(err);

        res.json({message: 'Device removed from the app!'});
    });
};
