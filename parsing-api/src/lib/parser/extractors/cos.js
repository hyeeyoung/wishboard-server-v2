const cheerio = require('cheerio');
const { getPriceWithoutString } = require('../utils');

const extractFromCosHtml = (html, _url) => {
  const $ = cheerio.load(html);
  const itemName = $('title').text();
  const itemImg = $('.m-product-image').children('img').eq(0).attr('src');
  let itemPrice = $('.m-product-price').children('#priceValue').text();
  itemPrice = itemPrice ? getPriceWithoutString(itemPrice) : undefined;
  return { item_img: itemImg, item_name: itemName, item_price: itemPrice };
};

module.exports = { extractFromCosHtml };
