const rateLimit = require('express-rate-limit');
const { StatusCode, ErrorMessage } = require('../utils/response');

const limiter = rateLimit({
  // 1초에 100개의 요청만 가능하도록
  windowMs: 60000, // 1초, 60 * 1000
  max: 100,
  delayMs: 1000,
  standardHeaders: true, // Return rate limit info in the `RateLimit-*` headers
  legacyHeaders: false, // Disable the `X-RateLimit-*` headers
  handler(req, res) {
    res.status(StatusCode.TOO_MANY_REQUEST).json({
      success: false,
      message: ErrorMessage.TOO_MANY_REQUEST,
    });
  },
});

module.exports = limiter;
