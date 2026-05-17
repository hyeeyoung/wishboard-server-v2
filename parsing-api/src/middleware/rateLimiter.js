const { rateLimit } = require('express-rate-limit');
const { StatusCode, ErrorMessage } = require('../utils/response');

const limiter = rateLimit({
  windowMs: 60000,
  limit: 100,
  standardHeaders: 'draft-7',
  legacyHeaders: false,
  handler(req, res) {
    res.status(StatusCode.TOO_MANY_REQUEST).json({
      success: false,
      message: ErrorMessage.TOO_MANY_REQUEST,
    });
  },
});

module.exports = limiter;
