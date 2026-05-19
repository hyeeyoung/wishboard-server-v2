const express = require('express');
const app = express();
const helmet = require('helmet');
const hpp = require('hpp');
const morgan = require('morgan');
const logger = require('./config/winston');
require('dotenv').config({ path: '../.env' });
const port = process.env.PORT;
const nodeEnv = process.env.NODE_ENV;

const handleErrors = require('./middleware/handleError');
const { NotFound } = require('./utils/errors');
const { ErrorMessage } = require('./utils/response');

const rateLimit = require('./middleware/rateLimiter');
app.set('trust proxy', 1 /* number of proxies between user and server */);

/** 기본 설정 */
// 서버 환경에 따라 다르게 설정 (배포/개발)
app.use(helmet());
app.use(hpp());
morganFormat = 'combined'; // Apache 표준
app.use(morgan(morganFormat, { stream: logger.stream }));

let isDisableKeepAlive = false;
app.use(function (req, res, next) {
  if (isDisableKeepAlive) {
    res.set('Connection', 'close');
  }
  next();
});

const server = app.listen(port, () => {
  /** 앱 시작 알림 (PM2 wait_ready). fork mode 에서 IPC 미활성 가능성 대비 typeof 체크 */
  if (typeof process.send === 'function') {
    process.send('ready');
  }
  logger.info(`[Parsing Api Server] on port ${port} | ${nodeEnv}`);
});

const SHUTDOWN_TIMEOUT_MS = 4000;

const shutdown = (signal) => {
  logger.info(`received ${signal}, starting graceful shutdown`);
  isDisableKeepAlive = true;
  // 활성 keepAlive 연결을 강제로 닫지 않으면 server.close() 콜백이 호출되지 않음.
  // Node 18.2+ 의 closeAllConnections 가 있으면 사용, 없으면 idle 만 닫음.
  if (typeof server.closeAllConnections === 'function') {
    server.closeAllConnections();
  } else if (typeof server.closeIdleConnections === 'function') {
    server.closeIdleConnections();
  }
  // 안전망: close 콜백이 일정 시간 안에 안 오면 강제 종료
  const forceTimer = setTimeout(() => {
    logger.warn(`force exit after ${SHUTDOWN_TIMEOUT_MS}ms shutdown timeout`);
    process.exit(1);
  }, SHUTDOWN_TIMEOUT_MS);
  forceTimer.unref();

  server.close((err) => {
    clearTimeout(forceTimer);
    if (err) {
      logger.error(`server.close error: ${err.message}`);
    }
    logger.info('pm2 process closed');
    process.exit(0);
  });
};

process.on('SIGINT', () => shutdown('SIGINT'));
process.on('SIGTERM', () => shutdown('SIGTERM'));

app.use(express.urlencoded({ extended: true }));
app.use(express.json());

/** DDos 공격 방지 */
app.use(rateLimit);

/** router 설정 */
app.use(require('./routes/index'));

/** 에러페이지 및 에러 핸들링 */
app.use((req, res, next) => {
  throw new NotFound(ErrorMessage.RequestWithIntentionalInvalidUrl);
});
app.use(handleErrors);

module.exports = app;
