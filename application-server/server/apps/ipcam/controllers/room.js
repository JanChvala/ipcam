// models
var Room = require(__dirname + '/../models/room');

// GET /api/rooms
exports.getRooms = function (req, res) {
    // Use the Beer model to find all beer
    Room.find({userId: req.user._id}, function (err, rooms) {
        if (err)
            res.send(err);

        res.json(rooms);
    });
};

// POST /api/rooms
exports.postRooms = function (req, res) {
    // Create a new instance of the Room model
    var room = new Room();

    // Set the room properties that came from the POST data
    room.name = req.body.name;
    room.lastUpdate = new Date();
    room.userId = req.user._id;

    // Save the room and check for errors
    room.save(function (err) {
        if (err)
            res.send(err);

        res.json({message: 'Room added to the locker!', data: room});
    });
};

// GET /api/rooms/:room_id
exports.getRoom = function (req, res) {
    // Use the Room model to find a specific beer
    Room.find({userId: req.user._id, _id: req.params.room_id}, function (err, room) {
        if (err)
            res.send(err);

        res.json(room);
    });
};

// PUT /api/rooms/:room_id
exports.putRoom = function (req, res) {
    // Use the Beer model to find a specific beer
    Room.update({userId: req.user._id, _id: req.params.room_id}, {lastUpdate: new Date()}, function (err, num, raw) {
        if (err)
            res.send(err);

        res.json({message: num + ' updated'});
    });
};

// DELETE /api/rooms/:room_id
exports.deleteRoom = function (req, res) {
    // Use the Beer model to find a specific room and remove it
    Room.remove({userId: req.user._id, _id: req.params.room_id}, function (err) {
        if (err)
            res.send(err);

        res.json({message: 'Room removed from the app!'});
    });
};
