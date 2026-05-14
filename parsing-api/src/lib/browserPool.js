/**
 * Playwright 영구 브라우저 + 컨텍스트 풀
 *
 * - 프로세스 당 단일 Chromium 인스턴스를 유지하고, 요청마다 newContext를 생성/폐기한다.
 * - browser.launch()는 비싸므로 호출 1회로 끝낸다.
 * - 자가 회복: isConnected() 체크 후 끊겨 있으면 재기동.
 * - 동시성: acquireSlot/releaseSlot 으로 동시 newContext 수를 제한한다.
 */

const logger = require('../config/winston');

// 'playwright'는 webpack externals 처리 대상이다(단계 4 참고).
// 런타임에 node_modules에서 직접 require 되어야 한다.
const { chromium } = require('playwright');

const MAX_CONCURRENT_CONTEXTS = 4;
let _browser = null;
let _launchingPromise = null;

// 단순 세마포어 — 외부 의존성 없이 동시 컨텍스트 수를 제한한다.
let _activeContexts = 0;
const _waiters = [];

const acquireSlot = () => {
  if (_activeContexts < MAX_CONCURRENT_CONTEXTS) {
    _activeContexts += 1;
    return Promise.resolve();
  }
  return new Promise((resolve) => _waiters.push(resolve));
};

const releaseSlot = () => {
  const next = _waiters.shift();
  if (next) {
    // 슬롯 점유 권한을 그대로 인계 (active count 유지)
    next();
  } else {
    _activeContexts = Math.max(0, _activeContexts - 1);
  }
};

const getBrowser = async () => {
  if (_browser && _browser.isConnected()) {
    return _browser;
  }
  if (_launchingPromise) {
    return _launchingPromise;
  }
  _launchingPromise = (async () => {
    try {
      _browser = await chromium.launch({
        headless: true,
        args: [
          '--no-sandbox',
          '--disable-dev-shm-usage',
          '--disable-gpu',
          '--disable-blink-features=AutomationControlled',
        ],
      });
      _browser.on('disconnected', () => {
        logger.warn('[browserPool] Chromium disconnected — will relaunch on next request');
        _browser = null;
      });
      logger.info('[browserPool] Chromium launched');
      return _browser;
    } finally {
      _launchingPromise = null;
    }
  })();
  return _launchingPromise;
};

const closeBrowser = async () => {
  if (_browser) {
    try {
      await _browser.close();
    } catch (err) {
      logger.error(`[browserPool] error closing browser: ${err}`);
    } finally {
      _browser = null;
    }
  }
};

// graceful shutdown
const _registerShutdown = (signal) => {
  process.on(signal, async () => {
    logger.info(`[browserPool] received ${signal}, closing Chromium`);
    await closeBrowser();
  });
};
_registerShutdown('SIGTERM');
_registerShutdown('SIGINT');

module.exports = {
  getBrowser,
  closeBrowser,
  acquireSlot,
  releaseSlot,
  MAX_CONCURRENT_CONTEXTS,
};
