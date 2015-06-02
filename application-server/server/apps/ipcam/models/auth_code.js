// Load required packages
var mongoose = require('mongoose');

// Define our token schema
var AuthCodeSchema = new mongoose.Schema({
    value: {type: String, required: true},
    redirectUri: {type: String, required: true},
    userId: {type: String, required: true},
    clientId: {type: String, required: true}
});

// Export the Mongoose model
module.exports = mongoose.model('AuthCode', AuthCodeSchema);