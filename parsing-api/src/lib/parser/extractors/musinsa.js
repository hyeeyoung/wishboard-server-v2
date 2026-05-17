const cheerio = require('cheerio');
const { getPriceWithoutString } = require('../utils');

const extractFromMusinsaHtml = (html, _url) => {
  let itemImg;
  let itemName;
  let itemPrice;
  const $ = cheerio.load(html);
  $('meta').each((_, el) => {
    const ogTag = $(el).attr('property')?.split(/^og:/)[1];
    if (ogTag) {
      const ogValue = $(el).attr('content');
      switch (ogTag) {
        case 'title':
          itemName = ogValue;
          break;
        case 'image':
          if (!itemImg) {
            itemImg = ogValue;
          }
          break;
        case 'description': {
          const matchPrice = ogValue.match(/\d{1,3}(,\d{3})*/g);
          itemPrice = matchPrice[matchPrice.length - 1];
          break;
        }
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

module.exports = { extractFromMusinsaHtml };
