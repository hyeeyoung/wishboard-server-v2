const cheerio = require('cheerio');
const { getPriceWithoutString } = require('../utils');

const extractFromGmarketHtml = (html, url) => {
  let itemImg;
  let itemName;
  let itemPrice;
  let priceValue;
  const $ = cheerio.load(html);
  itemName = $('title').text();
  $('meta').each((_, el) => {
    const ogTag = $(el).attr('property')?.split(/^og:/)[1];
    if (ogTag) {
      const ogValue = $(el).attr('content');
      switch (ogTag) {
        case 'title':
          priceValue = ogValue.split(' ');
          break;
        case 'image':
          if (!itemImg) {
            itemImg = ogValue;
          }
          break;
        case 'description':
          itemPrice = ogValue;
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
  if (!String(url).includes('mitem')) {
    itemPrice = priceValue[priceValue.length - 1];
  }
  itemPrice = itemPrice ? getPriceWithoutString(itemPrice) : undefined;
  return { item_img: itemImg, item_name: itemName, item_price: itemPrice };
};

module.exports = { extractFromGmarketHtml };
