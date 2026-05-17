const cheerio = require('cheerio');
const { getPriceWithoutString } = require('../utils');

const extractFromNaverHtml = (html, url) => {
  let itemImg;
  let itemName;
  let itemPrice;
  const $ = cheerio.load(html);
  $('meta').each((_, el) => {
    const egTag = $(el).attr('property')?.split(/^og:/)[1];
    if (egTag) {
      const egValue = $(el).attr('content');
      switch (egTag) {
        case 'title':
          itemName = egValue;
          break;
        case 'image':
          if (!itemImg) {
            itemImg = egValue;
          }
          break;
      }
      if (!itemName) {
        const text = $('title').text();
        if (text) {
          itemName = text;
        }
      }
    }
  });
  if (!itemPrice) {
    const site = String(url);
    if (
      site.includes('smartstore') ||
      site.includes('brand') ||
      site.includes('m.shopping')
    ) {
      itemPrice = $('._1LY7DqCnwR').html();
    } else {
      itemPrice = $('._25TFi0HDjm').text();
    }
  }
  itemPrice = itemPrice ? getPriceWithoutString(itemPrice) : undefined;
  return { item_img: itemImg, item_name: itemName, item_price: itemPrice };
};

module.exports = { extractFromNaverHtml };
