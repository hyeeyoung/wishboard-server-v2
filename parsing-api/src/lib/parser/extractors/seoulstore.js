const cheerio = require('cheerio');
const {
  getPriceWithoutString,
  extractOgMeta,
  titleFallback,
} = require('../utils');

const extractFromSeoulStoreHtml = (html, _url) => {
  const $ = cheerio.load(html);
  const og = extractOgMeta($);

  const itemName = og.title || titleFallback($);
  const itemImg = og.image;
  // 서울스토어 og:title 패턴: "[브랜드] | 상품명 | 가격"
  const itemPrice = og.title ? og.title.split('|').reverse()[1] : undefined;

  return {
    item_img: itemImg,
    item_name: itemName,
    item_price: itemPrice ? getPriceWithoutString(itemPrice) : undefined,
  };
};

module.exports = { extractFromSeoulStoreHtml };
