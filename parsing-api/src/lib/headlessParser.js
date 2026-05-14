/**
 * Playwright 헤드리스 브라우저 기반 파서.
 *
 * 흐름:
 *  1. browserPool.getBrowser() → newContext() 생성
 *  2. context.route() 로 image/font/media/stylesheet 리소스 차단 (속도 핵심)
 *  3. page.goto(url, { waitUntil: 'domcontentloaded', timeout: 8000 })
 *  4. selector hint가 있으면 waitForSelector (최대 3초). 실패해도 추출 시도.
 *  5. page.content() 로 렌더링된 HTML 획득
 *  6. resolveSiteType(url) → getExtractorBySiteType(siteType) 으로
 *     **정적 파서와 동일한 순수 추출 함수**를 재사용
 *  7. context.close() — browser는 유지
 *
 * 응답 형식은 정적 파서와 동일: { item_img, item_name, item_price }
 */

const logger = require('../config/winston');
const {
  getBrowser,
  acquireSlot,
  releaseSlot,
} = require('./browserPool');
const {
  resolveSiteType,
  getExtractorBySiteType,
  getRandomUserAgent,
} = require('./parser');
const { getSelectorHint } = require('./headlessTargets');

const PAGE_GOTO_TIMEOUT_MS = 8000;
const WAIT_FOR_SELECTOR_TIMEOUT_MS = 3000;

const BLOCKED_RESOURCE_TYPES = new Set([
  'image',
  'font',
  'media',
  'stylesheet',
]);

/**
 * @param {string} url
 * @returns {Promise<{item_img:string|undefined,item_name:string|undefined,item_price:string|undefined}>}
 */
const parseWithHeadless = async (url) => {
  await acquireSlot();

  let context;
  try {
    const browser = await getBrowser();
    context = await browser.newContext({
      userAgent: getRandomUserAgent(),
      locale: 'ko-KR',
      viewport: { width: 1280, height: 800 },
      // 일부 사이트의 ko 로케일 강제 동작에 도움
      extraHTTPHeaders: { 'Accept-Language': 'ko-KR,ko;q=0.9,en;q=0.8' },
    });

    // 리소스 차단 — 응답 시간/메모리 핵심 최적화
    await context.route('**/*', (route) => {
      try {
        const reqType = route.request().resourceType();
        if (BLOCKED_RESOURCE_TYPES.has(reqType)) {
          return route.abort();
        }
        return route.continue();
      } catch (err) {
        // race condition (context close 등) — 무시
        try {
          route.continue();
        } catch (_) {
          /* noop */
        }
      }
    });

    const page = await context.newPage();
    page.setDefaultTimeout(PAGE_GOTO_TIMEOUT_MS);

    try {
      await page.goto(url, {
        waitUntil: 'domcontentloaded',
        timeout: PAGE_GOTO_TIMEOUT_MS,
      });
    } catch (gotoErr) {
      // 타임아웃이어도 부분 렌더링 HTML이 있을 수 있으므로 추출은 계속 시도
      logger.warn(`[headlessParser] goto failed for ${url}: ${gotoErr.message}`);
    }

    // 사이트별 selector hint가 있으면 짧은 대기
    const hint = getSelectorHint(url);
    if (hint?.waitFor) {
      try {
        await page.waitForSelector(hint.waitFor, {
          timeout: WAIT_FOR_SELECTOR_TIMEOUT_MS,
          state: 'attached',
        });
      } catch (selErr) {
        // 못 찾아도 진행 — 부분 응답이라도 회수
        logger.debug?.(
          `[headlessParser] waitForSelector miss for ${url}: ${hint.waitFor}`,
        );
      }
    }

    const html = await page.content();
    const siteType = resolveSiteType(url);
    const extract = getExtractorBySiteType(siteType);
    const result = extract(html, url);

    return result;
  } catch (err) {
    logger.error(`[headlessParser] error for ${url}: ${err?.stack || err}`);
    return {
      item_img: undefined,
      item_name: undefined,
      item_price: undefined,
    };
  } finally {
    if (context) {
      try {
        await context.close();
      } catch (closeErr) {
        logger.warn(`[headlessParser] context close error: ${closeErr.message}`);
      }
    }
    releaseSlot();
  }
};

module.exports = {
  parseWithHeadless,
};
