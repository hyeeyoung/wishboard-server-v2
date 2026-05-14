/**
 * 헤드리스 사전 분류 화이트리스트 + 사이트별 selector hint.
 *
 * - 정적 파싱이 본질적으로 불가능하다고 판단되는 사이트만 화이트리스트에 등록한다.
 * - 운영 로그(parser_type) 분포를 보며 후보군을 점진적으로 조정한다.
 *
 * 1차 화이트리스트: 29CM, EQL, ZARA, 네이버 통합검색
 *  - 제시믹스 / SSG 등은 Fallback 경로(static → headless)로 두고, 추후 이동 검토.
 */

const HEADLESS_URL_PREFIXES = [
  // 29CM
  'https://www.29cm.co.kr/',
  'https://m.29cm.co.kr/',

  // EQL (카카오스타일 SPA)
  'https://www.eqlstore.com/',
  'https://m.eqlstore.com/',

  // ZARA
  'https://www.zara.com/',

  // 네이버 통합검색 결과 페이지 (TODO: parser.js 의 long-standing 항목)
  'https://search.shopping.naver.com/',
  'https://m.search.shopping.naver.com/',
  'https://msearch.shopping.naver.com/',
];

/**
 * 사이트별 가격/타이틀/이미지 노드 selector hint.
 *
 * key: HEADLESS_URL_PREFIXES 중 하나의 'host suffix' 또는 prefix 일부 — startsWith 매칭
 * value: { waitFor?: string, ... } — 헤드리스 파서가 waitForSelector에 사용할 1개의 selector
 *
 * waitFor는 "가장 안정적이라 보이는" 1개 selector. 사이트 구조 변경 시 깨질 수 있으나,
 * waitForSelector 타임아웃이 발생해도 page.content()를 가져와 추출 시도한다.
 */
const SITE_SELECTOR_HINTS = {
  'https://www.29cm.co.kr/': {
    waitFor: '[class*="price" i], [data-testid*="price" i]',
  },
  'https://m.29cm.co.kr/': {
    waitFor: '[class*="price" i], [data-testid*="price" i]',
  },
  'https://www.eqlstore.com/': {
    waitFor: '[class*="Price" i], [class*="price" i]',
  },
  'https://m.eqlstore.com/': {
    waitFor: '[class*="Price" i], [class*="price" i]',
  },
  'https://www.zara.com/': {
    // ZARA는 product-detail-info-actions, money-amount__main 등이 자주 보임
    waitFor: '.money-amount__main, [class*="price" i]',
  },
  'https://search.shopping.naver.com/': {
    waitFor: '[class*="price" i]',
  },
  'https://m.search.shopping.naver.com/': {
    waitFor: '[class*="price" i]',
  },
  'https://msearch.shopping.naver.com/': {
    waitFor: '[class*="price" i]',
  },
};

/**
 * URL이 사전 분류 화이트리스트에 해당하는지 판정.
 * @param {string} url
 * @returns {boolean}
 */
const requiresHeadless = (url) => {
  if (typeof url !== 'string') {
    return false;
  }
  return HEADLESS_URL_PREFIXES.some((prefix) => url.startsWith(prefix));
};

/**
 * URL에 매칭되는 selector hint 반환. 없으면 null.
 * @param {string} url
 * @returns {{waitFor?: string} | null}
 */
const getSelectorHint = (url) => {
  if (typeof url !== 'string') {
    return null;
  }
  for (const [prefix, hint] of Object.entries(SITE_SELECTOR_HINTS)) {
    if (url.startsWith(prefix)) {
      return hint;
    }
  }
  return null;
};

module.exports = {
  requiresHeadless,
  getSelectorHint,
  HEADLESS_URL_PREFIXES,
};
