const getPriceWithoutString = (itemPrice) => {
  return String(itemPrice).replace(/[^0-9]/g, '');
};

const emptyResult = () => ({
  item_img: undefined,
  item_name: undefined,
  item_price: undefined,
});

/**
 * @param {cheerio.CheerioAPI} $ cheerio.load(html) 결과
 * @returns {Record<string, string>} og:title → { title }, og:image → { image } 등으로 평탄화한 객체
 */
const extractOgMeta = ($) => {
  const og = {};
  $('meta').each((_, el) => {
    const property = $(el).attr('property');
    if (typeof property !== 'string' || !property.startsWith('og:')) {
      return;
    }
    og[property.slice(3)] = $(el).attr('content');
  });
  return og;
};

/**
 * @param {cheerio.CheerioAPI} $ cheerio.load(html) 결과
 * @returns {string | undefined} <title> 텍스트. 비어있으면 undefined.
 */
const titleFallback = ($) => {
  const text = $('title').text();
  return text || undefined;
};

// 봇 차단 / 대기 페이지 패턴. itemName 에 매치되면 추출 결과를 신뢰할 수 없는 것으로 본다.
const BOT_BLOCK_PATTERNS = [
  /access\s*denied/i,
  /forbidden/i,
  /\b403\b/,
  /bot\s*detection/i,
  /잠시만\s*기다리(십시오|세요)/, // Gmarket 등의 봇 검증 대기 페이지
  /just\s*a\s*moment/i, // Cloudflare 대기 페이지
  /please\s*wait/i,
  /checking\s*your\s*browser/i,
  /verification/i,
];

/**
 * 추출된 itemName 이 봇 차단 / 인증 거부 페이지의 타이틀인지 판정.
 * @param {{item_name?: string|undefined} | null | undefined} result
 * @returns {boolean}
 */
const looksLikeBotBlock = (result) => {
  const name = result?.item_name;
  if (typeof name !== 'string' || !name.trim()) {
    return false;
  }
  return BOT_BLOCK_PATTERNS.some((pattern) => pattern.test(name));
};

// URL 추적 파라미터 블랙리스트.
// - 정확 매치 또는 startsWith 매치 (utm_* 류).
// - 도메인별 화이트리스트보다 블랙리스트가 안전: 미지원 도메인의 상품 ID 파라미터를 실수로 제거하지 않도록.
const TRACKING_PARAM_PREFIXES = ['utm_'];
const TRACKING_PARAM_EXACT = new Set([
  // 광고 클릭 추적
  'fbclid',
  'gclid',
  'dclid',
  // 광고 / 추천 트래킹
  'businessTracking',
  'gaListId',
  'gaListName',
  'ciderListId',
  'ciderListName',
  'NaPm',
  // Shopcider 류
  'linkUrl',
  'operationContent',
  'operationImage',
  'operationpageTitle',
  'operationPosition',
  'operationType',
  'productPosition',
  // 쿠팡 류
  '_NC',
  'wPcid',
  'wRef',
  'wTime',
  'redirect',
  'addtag',
  'ctag',
  'lptag',
  'itime',
  'pageType',
  'pageValue',
]);

const isTrackingParam = (key) => {
  if (TRACKING_PARAM_EXACT.has(key)) return true;
  return TRACKING_PARAM_PREFIXES.some((prefix) => key.startsWith(prefix));
};

/**
 * URL 에서 추적 파라미터를 제거한 정규화 URL 반환.
 * 파싱 실패하면 원본 그대로 반환.
 * @param {string} rawUrl
 * @returns {string}
 */
const normalizeUrl = (rawUrl) => {
  if (typeof rawUrl !== 'string' || !rawUrl) {
    return rawUrl;
  }
  try {
    const url = new URL(rawUrl);
    const keysToDelete = [];
    for (const key of url.searchParams.keys()) {
      if (isTrackingParam(key)) {
        keysToDelete.push(key);
      }
    }
    for (const key of keysToDelete) {
      url.searchParams.delete(key);
    }
    return url.toString();
  } catch (e) {
    return rawUrl;
  }
};

/**
 * 두 추출 결과를 머지. primary 의 falsy 필드를 secondary 값으로 채운다.
 * 빈 문자열·undefined·null 모두 falsy 로 처리.
 */
const mergeResults = (primary, secondary) => {
  const p = primary || {};
  const s = secondary || {};
  return {
    item_img: p.item_img || s.item_img,
    item_name: p.item_name || s.item_name,
    item_price: p.item_price || s.item_price,
  };
};

module.exports = {
  getPriceWithoutString,
  emptyResult,
  extractOgMeta,
  titleFallback,
  looksLikeBotBlock,
  BOT_BLOCK_PATTERNS,
  normalizeUrl,
  mergeResults,
};
