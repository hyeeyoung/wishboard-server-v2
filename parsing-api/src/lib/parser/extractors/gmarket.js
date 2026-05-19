const cheerio = require('cheerio');
const {
  getPriceWithoutString,
  extractOgMeta,
  titleFallback,
} = require('../utils');

// 천 단위 쉼표 포맷("17,900") 또는 4자리 이상 숫자만 가격으로 인정.
// "01" 같은 옵션 코드가 가격으로 잘못 추출되는 것을 막는다.
const PRICE_TOKEN_PATTERN = /\d{1,3}(?:,\d{3})+|\d{4,}/;

const extractFromGmarketHtml = (html, url) => {
  const $ = cheerio.load(html);
  const og = extractOgMeta($);

  const itemName = titleFallback($);
  const itemImg = og.image;

  let itemPrice;
  if (String(url).includes('mitem')) {
    itemPrice = og.description;
  } else if (og.title) {
    const matched = og.title
      .split(' ')
      .reverse()
      .find((token) => PRICE_TOKEN_PATTERN.test(token));
    itemPrice = matched;
  }

  return {
    item_img: itemImg,
    item_name: itemName,
    item_price: itemPrice ? getPriceWithoutString(itemPrice) : undefined,
  };
};

module.exports = { extractFromGmarketHtml };
