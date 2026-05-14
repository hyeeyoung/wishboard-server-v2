const { rateLimit } = require('express-rate-limit');
const { StatusCode, ErrorMessage } = require('../utils/response');

const limiter = rateLimit({
  // 1분에 100개의 요청만 가능하도록
  windowMs: 60000, // 1분, 60 * 1000
  limit: 100, // v7: max → limit (max는 deprecated, 호환 유지되지만 limit 사용 권장)
  standardHeaders: 'draft-7', // Return rate limit info in the `RateLimit-*` headers (v7 권장)
  legacyHeaders: false, // Disable the `X-RateLimit-*` headers
  handler(req, res) {
    res.status(StatusCode.TOO_MANY_REQUEST).json({
      success: false,
      message: ErrorMessage.TOO_MANY_REQUEST,
    });
  },
});

module.exports = limiter;
