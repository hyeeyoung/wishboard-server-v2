const itemController = require('../controllers/itemController');
const express = require('express');
const router = new express.Router();

router.get('/parse', itemController.parseItemInfo);

module.exports = router;
