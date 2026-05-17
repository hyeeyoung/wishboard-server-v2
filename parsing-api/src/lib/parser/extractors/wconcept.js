const cheerio = require('cheerio');
const {
  getPriceWithoutString,
  extractOgMeta,
  titleFallback,
} = require('../utils');

const extractFromWconceptHtml = (html, _url) => {
  const $ = cheerio.load(html);
  const og = extractOgMeta($);

  const itemName = og.description || titleFallback($);
  const itemImg = og.image;

  // TODO selector 개선 필요 (사이트 구조 변경 시 깨짐)
  const itemPrice = $('#frmproduct > div.price_wrap > dl > dd.normal > em').text();

  return {
    item_img: itemImg,
    item_name: itemName,
    item_price: itemPrice ? getPriceWithoutString(itemPrice) : undefined,
  };
};

module.exports = { extractFromWconceptHtml };
