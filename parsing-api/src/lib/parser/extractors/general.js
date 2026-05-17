const cheerio = require('cheerio');
const { getPriceWithoutString } = require('../utils');

const extractFromGeneralHtml = (html, _url) => {
  let itemImg;
  let itemName;
  let itemPrice;
  const $ = cheerio.load(html);
  $('meta').each((_, el) => {
    const tag = $(el).attr('property')?.split(':')[1];
    if (tag) {
      const value = $(el).attr('content');
      switch (tag) {
        case 'title':
          itemName = value;
          break;
        case 'image':
          if (!itemImg) {
            itemImg = value;
          }
          break;
        case 'price':
        case 'Price':
        case 'amount':
          if (!itemPrice) {
            itemPrice = value;
          }
          break;
        case 'description':
          if (!itemPrice) {
            const priceRegex = /\d{1,3}(,\d{3})*원/g;
            const matchPriceString = value.match(priceRegex);
            if (matchPriceString) {
              itemPrice = matchPriceString[0];
            }
          }
          break;
      }
    }
    if (!itemName) {
      const text = $('title').text();
      if (text) {
        itemName = text;
      }
    }
  });
  itemPrice = itemPrice ? getPriceWithoutString(itemPrice) : undefined;
  return { item_img: itemImg, item_name: itemName, item_price: itemPrice };
};

module.exports = { extractFromGeneralHtml };
