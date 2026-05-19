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

module.exports = {
  getPriceWithoutString,
  emptyResult,
  extractOgMeta,
  titleFallback,
};
