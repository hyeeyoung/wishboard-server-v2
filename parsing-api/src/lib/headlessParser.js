/**
 * Playwright 헤드리스 브라우저 기반 파서.
 *
 * 흐름:
 *  1. browserPool.getBrowser() → newContext() 생성
 *  2. context.route() 로 image/font/media/stylesheet 리소스 차단 (속도 핵심)
 *  3. page.goto(url, { waitUntil: 'domcontentloaded', timeout: 8000 })
 *  4. page.content() 로 렌더링된 HTML 획득
 *  5. resolveSiteType(url) → getExtractorBySiteType(siteType) 으로
 *     **정적 파서와 동일한 순수 추출 함수**를 재사용
 *  6. context.close() — browser는 유지
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
const { emptyResult, looksLikeBotBlock } = require('./parser/utils');
const {
  getPriceSelectors,
  extractPriceFromText,
} = require('./parser/selectorHints');

const PAGE_GOTO_TIMEOUT_MS = 8000;
const SELECTOR_TEXT_TIMEOUT_MS = 1500;

const BLOCKED_RESOURCE_TYPES = new Set([
  'image',
  'font',
  'media',
  'stylesheet',
]);

const parseWithHeadless = async (url) => {
  await acquireSlot();

  let context;
  let page;
  let response;
  try {
    const browser = await getBrowser();
    context = await browser.newContext({
      userAgent: getRandomUserAgent(),
      locale: 'ko-KR',
      viewport: { width: 1280, height: 800 },
      extraHTTPHeaders: { 'Accept-Language': 'ko-KR,ko;q=0.9,en;q=0.8' },
    });

    await context.route('**/*', (route) => {
      try {
        const reqType = route.request().resourceType();
        if (BLOCKED_RESOURCE_TYPES.has(reqType)) {
          return route.abort();
        }
        return route.continue();
      } catch (err) {
        logger.debug?.(`[headlessParser] routing error: ${err.message}`);
      }
    });

    page = await context.newPage();
    page.setDefaultTimeout(PAGE_GOTO_TIMEOUT_MS);

    try {
      response = await page.goto(url, {
        waitUntil: 'domcontentloaded',
        timeout: PAGE_GOTO_TIMEOUT_MS,
      });
    } catch (gotoErr) {
      logger.warn(
        `[headlessParser] goto failed for ${url}: ${gotoErr.message} final_url=${page.url?.()}`,
      );
    }

    const html = await page.content();
    const siteType = resolveSiteType(url);
    const extract = getExtractorBySiteType(siteType);
    const result = extract(html, url);
    if (looksLikeBotBlock(result)) {
      logger.warn(
        `[headlessParser] bot block detected for ${url}: name="${result.item_name}" ` +
          `status=${response?.status?.() ?? 'unknown'} final_url=${page.url?.()}`,
      );
      return emptyResult();
    }
    // 가격이 og 메타에 없을 때 DOM selector 로 보강.
    // 사이트별 specific selector → general selector 순서로 시도.
    if (!result.item_price) {
      const selectors = getPriceSelectors(siteType);
      for (const selector of selectors) {
        try {
          const text = await page
            .locator(selector)
            .first()
            .textContent({ timeout: SELECTOR_TEXT_TIMEOUT_MS });
          const price = extractPriceFromText(text);
          if (price) {
            result.item_price = price;
            logger.info(
              `[headlessParser] price extracted via selector "${selector}" for ${url}: ${price}`,
            );
            break;
          }
        } catch (_) {
          // selector 미존재 / timeout — 다음 selector 시도
        }
      }
    }
    return result;
  } catch (err) {
    logger.error(
      `[headlessParser] error for ${url}: status=${response?.status?.() ?? 'unknown'} ` +
        `final_url=${page?.url?.() ?? 'unknown'} stack=${err?.stack || err}`,
    );
    return emptyResult();
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
