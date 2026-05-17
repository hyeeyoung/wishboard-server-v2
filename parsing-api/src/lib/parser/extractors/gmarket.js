const cheerio = require('cheerio');
const { getPriceWithoutString } = require('../utils');

// 천 단위 쉼표 포맷("17,900") 또는 4자리 이상 숫자만 가격으로 인정.
// "01" 같은 옵션 코드가 가격으로 잘못 추출되는 것을 막는다.
const PRICE_TOKEN_PATTERN = /\d{1,3}(?:,\d{3})+|\d{4,}/;

const extractFromGmarketHtml = (html, url) => {
  let itemImg;
  let itemName;
  let itemPrice;
  let titleTokens;
  const $ = cheerio.load(html);
  itemName = $('title').text();
  $('meta').each((_, el) => {
    const ogTag = $(el).attr('property')?.split(/^og:/)[1];
    if (ogTag) {
      const ogValue = $(el).attr('content');
      switch (ogTag) {
        case 'title':
          titleTokens = ogValue.split(' ');
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
  if (!String(url).includes('mitem') && titleTokens) {
    const matched = [...titleTokens]
      .reverse()
      .find((token) => PRICE_TOKEN_PATTERN.test(token));
    itemPrice = matched || undefined;
  }
  itemPrice = itemPrice ? getPriceWithoutString(itemPrice) : undefined;
  return { item_img: itemImg, item_name: itemName, item_price: itemPrice };
};

module.exports = { extractFromGmarketHtml };
