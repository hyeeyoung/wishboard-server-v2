const express = require('express');

const router = new express.Router();

router.get('/', (req, res) => res.send('Welcome to WishBoard!!'));

router.use('/v2/item', require('./itemRoutes'));

module.exports = router;
