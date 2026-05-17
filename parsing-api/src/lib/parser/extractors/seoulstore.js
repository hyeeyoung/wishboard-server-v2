const cheerio = require('cheerio');
const { getPriceWithoutString } = require('../utils');

const extractFromSeoulStoreHtml = (html, _url) => {
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
          itemPrice = value.split('|').reverse()[1];
          break;
        case 'image':
          if (!itemImg) {
            itemImg = value;
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

module.exports = { extractFromSeoulStoreHtml };
