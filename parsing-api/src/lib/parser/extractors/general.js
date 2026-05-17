const cheerio = require('cheerio');
const { getPriceWithoutString, titleFallback } = require('../utils');

// general 파서는 og: 외에 product:price:amount 등 다양한 property를 받아야 하므로
// extractOgMeta 헬퍼 대신 첫번째 토큰 무시 + 두번째 토큰을 키로 사용한다.
const extractFromGeneralHtml = (html, _url) => {
  let itemImg;
  let itemName;
  let itemPrice;
  const $ = cheerio.load(html);
  $('meta').each((_, el) => {
    const tag = $(el).attr('property')?.split(':')[1];
    if (!tag) {
      return;
    }
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
  });
  if (!itemName) {
    itemName = titleFallback($);
  }
  return {
    item_img: itemImg,
    item_name: itemName,
    item_price: itemPrice ? getPriceWithoutString(itemPrice) : undefined,
  };
};

module.exports = { extractFromGeneralHtml };
