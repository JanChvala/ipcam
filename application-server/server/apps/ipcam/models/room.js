// Load required packages
var mongoose = require('mongoose');

// Define our beer schema
var RoomSchema = new mongoose.Schema({
    name: String,
    lastUpdate: Date,
    userId: String
});

// Export the Mongoose model
module.exports = mongoose.model('Room', RoomSchema);