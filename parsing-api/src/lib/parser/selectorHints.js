/**
 * 헤드리스 파싱에서 og 메타에 가격이 없을 때, DOM 노드에서 가격을 추출하기 위한 selector hint.
 *
 * 흐름:
 *   1. 사이트별 specific selector 먼저 시도 (정확도 우선)
 *   2. 그 다음 general selector (모든 사이트에 적용되는 휴리스틱)
 *   3. 매치된 텍스트에서 "천 단위 쉼표 포맷" 또는 "4자리 이상 숫자" 형식만 가격으로 인정
 *
 * 화이트리스트 분기가 아니라 **모든 헤드리스 호출에서 시도되는 보강 단계**.
 * PR #15 의 사용자 결정("모든 페이지에서 동작 가능하게") 과 호환:
 *   - URL 사전 분류 없음 (모든 URL 이 동일 흐름)
 *   - 가격 결손 케이스에서만 DOM selector 시도
 */

// 사이트별 specific selector. resolveSiteType 의 반환값을 key 로 사용.
const PRICE_SELECTORS_BY_SITE = {
  cos: ['#priceValue', '.m-product-price #priceValue'],
  gmarket: ['.price-real', '.item_price strong', '.price_real'],
  wconcept: [
    '#frmproduct > div.price_wrap > dl > dd.normal > em',
    '.price_wrap .price',
  ],
  naver: ['._1LY7DqCnwR', '._25TFi0HDjm'],
  musinsa: ['.product-price', '[data-mds="Typography"][class*="price" i]'],
  seoulstore: ['.product-price', '.price'],
};

// 모든 사이트에 적용되는 general fallback selector. 사이트별 selector 가 실패한 후 시도.
const GENERAL_PRICE_SELECTORS = [
  '[data-price]',
  '[class*="price" i]',
  '[class*="Price" i]',
  '[itemprop="price"]',
];

// 가격 텍스트 형식 — 천 단위 쉼표 또는 4자리 이상 숫자만 인정.
// 옵션 코드("01") 나 작은 숫자가 가격으로 잘못 잡히는 것을 방지.
const PRICE_VALUE_PATTERN = /\d{1,3}(?:,\d{3})+|\d{4,}/;

/**
 * 사이트 type 에 대한 가격 selector 순회 리스트 반환.
 * @param {string} siteType
 * @returns {string[]}
 */
const getPriceSelectors = (siteType) => {
  const specific = PRICE_SELECTORS_BY_SITE[siteType] || [];
  return [...specific, ...GENERAL_PRICE_SELECTORS];
};

/**
 * 텍스트에서 가격 형식만 추출하여 숫자만 남긴 문자열로 반환.
 * @param {string | null | undefined} text
 * @returns {string | undefined}
 */
const extractPriceFromText = (text) => {
  if (typeof text !== 'string') return undefined;
  const matched = text.match(PRICE_VALUE_PATTERN);
  if (!matched) return undefined;
  return matched[0].replace(/,/g, '');
};

module.exports = {
  getPriceSelectors,
  extractPriceFromText,
  PRICE_SELECTORS_BY_SITE,
  GENERAL_PRICE_SELECTORS,
  PRICE_VALUE_PATTERN,
};
