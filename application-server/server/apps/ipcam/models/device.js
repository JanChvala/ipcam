// Load required packages
var mongoose = require('mongoose');

// Define our beer schema
var DeviceSchema = new mongoose.Schema({
    name: String,
    code: String,
    gcmRegistrationId: String,
    lastUpdate: Date,
    userId: String
});

// Export the Mongoose model
module.exports = mongoose.model('Device', DeviceSchema);