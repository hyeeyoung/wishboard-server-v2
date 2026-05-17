const cheerio = require('cheerio');
const { getPriceWithoutString } = require('../utils');

const extractFromWconceptHtml = (html, _url) => {
  let itemImg;
  let itemName;
  let itemPrice;
  const $ = cheerio.load(html);
  $('meta').each((_, el) => {
    const ogTag = $(el).attr('property')?.split(/^og:/)[1];
    if (ogTag) {
      const ogValue = $(el).attr('content');
      switch (ogTag) {
        case 'description':
          itemName = ogValue;
          break;
        case 'image':
          itemImg = ogValue;
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
  if (!itemPrice) {
    // TODO selector 개선 필요 (사이트 구조 변경 시 깨짐)
    itemPrice = $('#frmproduct > div.price_wrap > dl > dd.normal > em').text();
  }
  itemPrice = itemPrice ? getPriceWithoutString(itemPrice) : undefined;
  return { item_img: itemImg, item_name: itemName, item_price: itemPrice };
};

module.exports = { extractFromWconceptHtml };
