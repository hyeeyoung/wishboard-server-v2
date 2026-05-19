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

module.exports = {
  getPriceWithoutString,
  emptyResult,
  extractOgMeta,
  titleFallback,
  looksLikeBotBlock,
  BOT_BLOCK_PATTERNS,
};
