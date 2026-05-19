const cheerio = require('cheerio');
const {
  getPriceWithoutString,
  extractOgMeta,
  titleFallback,
} = require('../utils');

const extractFromMusinsaHtml = (html, _url) => {
  const $ = cheerio.load(html);
  const og = extractOgMeta($);

  const itemName = og.title || titleFallback($);
  const itemImg = og.image;

  let itemPrice;
  if (og.description) {
    const matchPrice = og.description.match(/\d{1,3}(,\d{3})*/g);
    if (matchPrice && matchPrice.length > 0) {
      itemPrice = matchPrice[matchPrice.length - 1];
    }
  }

  return {
    item_img: itemImg,
    item_name: itemName,
    item_price: itemPrice ? getPriceWithoutString(itemPrice) : undefined,
  };
};

module.exports = { extractFromMusinsaHtml };
