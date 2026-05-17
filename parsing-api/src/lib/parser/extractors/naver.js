const cheerio = require('cheerio');
const {
  getPriceWithoutString,
  extractOgMeta,
  titleFallback,
} = require('../utils');

const extractFromNaverHtml = (html, url) => {
  const $ = cheerio.load(html);
  const og = extractOgMeta($);

  const itemName = og.title || titleFallback($);
  const itemImg = og.image;

  const site = String(url);
  const itemPrice =
    site.includes('smartstore') ||
    site.includes('brand') ||
    site.includes('m.shopping')
      ? $('._1LY7DqCnwR').html()
      : $('._25TFi0HDjm').text();

  return {
    item_img: itemImg,
    item_name: itemName,
    item_price: itemPrice ? getPriceWithoutString(itemPrice) : undefined,
  };
};

module.exports = { extractFromNaverHtml };
