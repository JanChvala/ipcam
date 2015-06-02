var express = require('express');
var router = express.Router();

/* GET just to see if it is working. */
router.get('/', function (req, res) {
  res.send('Welcome to API v 0.0.1!');
});

module.exports = router;
