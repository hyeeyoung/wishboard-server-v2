const axios = require('axios');
const cheerio = require('cheerio');
const logger = require('../config/winston');
const { NotFound } = require('../utils/errors');
const { ErrorMessage } = require('../utils/response');

const userAgents = [
  'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.67 Safari/537.36',
  'Mozilla/5.0 (Linux; U; Android 2.1; en-us; sdk Build/ERD79) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17',
];

const getRandomUserAgent = () => {
  return userAgents[Math.floor(Math.random() * userAgents.length)];
};

const config = {
  headers: {
    'User-Agent': getRandomUserAgent(),
    Accept:
      'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8',
    'Accept-Encoding': 'gzip, deflate, br',
    Connection: 'keep-alive',
  },
  validateStatus: function (status) {
    return status >= 200 && status < 300;
  },
};

const getHtml = async (url) => {
  try {
    return await axios.get(encodeURI(url), config);
  } catch (err) {
    logger.error(err);
    throw new NotFound(ErrorMessage.itemParseFail);
  }
};

const getPriceWithoutString = (itemPrice) => {
  return String(itemPrice).replace(/[^0-9]/g, '');
};

/* ---------------------------------------------------------------------------
 * 순수 추출 함수 (HTML 문자열을 받아 결과를 반환)
 * - 정적(cheerio) / 동적(Playwright `page.content()`) 양쪽에서 동일하게 사용한다.
 * - 사이트별 함수는 cheerio 인스턴스를 받아 결과 객체를 반환한다.
 * ------------------------------------------------------------------------- */

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
            const description = value;

            const priceRegex = /\d{1,3}(,\d{3})*원/g;
            const matchPriceString = description.match(priceRegex);
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

/* 무신사, 우신사 */
const extractFromMusinsaHtml = (html, _url) => {
  let itemImg;
  let itemName;
  let itemPrice;
  let priceValue;
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
          priceValue = ogValue;
          const matchPrice = priceValue.match(/\d{1,3}(,\d{3})*/g);
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

/* W Concept */
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
    // TODO 개선 필요
    itemPrice = $(
      '#frmproduct > div.price_wrap > dl > dd.normal > em',
    ).text();
  }
  itemPrice = itemPrice ? getPriceWithoutString(itemPrice) : undefined;
  return { item_img: itemImg, item_name: itemName, item_price: itemPrice };
};

/* 네이버 스토어팜, 네이버쇼핑 */
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
  //* 앞 smartStore, 뒤 toptop
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

/* COS */
const extractFromCosHtml = (html, _url) => {
  const $ = cheerio.load(html);
  const itemName = $('title').text();
  const itemImg = $('.m-product-image').children('img').eq(0).attr('src');
  let itemPrice = $('.m-product-price').children('#priceValue').text();
  itemPrice = itemPrice ? getPriceWithoutString(itemPrice) : undefined;
  return { item_img: itemImg, item_name: itemName, item_price: itemPrice };
};

/* Gmarket web/mobile */
const extractFromGmarketHtml = (html, url) => {
  let itemImg;
  let itemName;
  let itemPrice;
  let priceValue;
  const $ = cheerio.load(html);
  itemName = $('title').text();
  /* Gmarket Mobile */
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
  /* Gmarket Web */
  if (!String(url).includes('mitem')) {
    itemPrice = priceValue[priceValue.length - 1];
  }
  itemPrice = itemPrice ? getPriceWithoutString(itemPrice) : undefined;
  return { item_img: itemImg, item_name: itemName, item_price: itemPrice };
};

/* Seoul store */
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

/* ---------------------------------------------------------------------------
 * 사이트별 정적 파서 (axios → 추출 순수함수)
 * ------------------------------------------------------------------------- */

const parsingForGeneral = async (url) => {
  const html = await getHtml(url);
  if (html.status !== 200) {
    return { item_img: undefined, item_name: undefined, item_price: undefined };
  }
  return extractFromGeneralHtml(html.data, url);
};

const parsingForMusinsa = async (url) => {
  const html = await getHtml(url);
  if (html.status !== 200) {
    return { item_img: undefined, item_name: undefined, item_price: undefined };
  }
  return extractFromMusinsaHtml(html.data, url);
};

const parsingForWconcept = async (url) => {
  const html = await getHtml(url);
  if (html.status !== 200) {
    return { item_img: undefined, item_name: undefined, item_price: undefined };
  }
  return extractFromWconceptHtml(html.data, url);
};

const parsingForNaver = async (url) => {
  const html = await getHtml(url);
  if (html.status !== 200) {
    return { item_img: undefined, item_name: undefined, item_price: undefined };
  }
  return extractFromNaverHtml(html.data, url);
};

const parsingForCos = async (url) => {
  const html = await getHtml(url);
  if (html.status !== 200) {
    return { item_img: undefined, item_name: undefined, item_price: undefined };
  }
  return extractFromCosHtml(html.data, url);
};

const parsingForGmarket = async (url) => {
  const html = await getHtml(url);
  if (html.status !== 200) {
    return { item_img: undefined, item_name: undefined, item_price: undefined };
  }
  return extractFromGmarketHtml(html.data, url);
};

const parsingForSeoulStore = async (url) => {
  const html = await getHtml(url);
  if (html.status !== 200) {
    return { item_img: undefined, item_name: undefined, item_price: undefined };
  }
  return extractFromSeoulStoreHtml(html.data, url);
};

/* ---------------------------------------------------------------------------
 * URL → 파서 매핑 (정적 경로)
 * ------------------------------------------------------------------------- */

/**
 * URL prefix에 따라 적절한 사이트 분류 키를 반환.
 * 헤드리스 파서와 정적 파서가 동일한 추출 로직을 사용할 수 있도록 키로 분기한다.
 *
 * @param {string} url
 * @returns {'musinsa'|'seoulstore'|'wconcept'|'naver'|'cos'|'gmarket'|'general'}
 */
const resolveSiteType = (url) => {
  const site = String(url);
  if (
    site.startsWith('https://store.musinsa.com/') ||
    site.startsWith('https://musinsaapp.page.link/') ||
    site.startsWith('https://www.musinsa.com/')
  ) {
    return 'musinsa';
  }
  if (site.startsWith('https://www.seoulstore.com/')) {
    return 'seoulstore';
  }
  if (
    site.startsWith('https://m.wconcept.co.kr/') ||
    site.startsWith('https://www.wconcept.co.kr/')
  ) {
    return 'wconcept';
  }
  if (
    site.startsWith('https://m.smartstore.naver.com/') ||
    site.startsWith('https://smartstore.naver.com/') ||
    site.startsWith('https://m.shopping.naver.com/') ||
    site.startsWith('https://brand.naver.com/') ||
    site.startsWith('https://toptop.naver.com/')
  ) {
    return 'naver';
  }
  if (
    site.startsWith('https://www.cosstores.com/') ||
    site.startsWith('https://www.cos.com/')
  ) {
    return 'cos';
  }
  if (
    site.startsWith('http://mitem.gmarket.co.kr/') ||
    site.startsWith('http://item.gmarket.co.kr/')
  ) {
    return 'gmarket';
  }
  return 'general';
};

/**
 * 사이트 키에 대응하는 추출 순수 함수 반환.
 * @param {string} siteType
 * @returns {(html: string, url: string) => {item_img: string|undefined, item_name: string|undefined, item_price: string|undefined}}
 */
const getExtractorBySiteType = (siteType) => {
  switch (siteType) {
    case 'musinsa':
      return extractFromMusinsaHtml;
    case 'seoulstore':
      return extractFromSeoulStoreHtml;
    case 'wconcept':
      return extractFromWconceptHtml;
    case 'naver':
      return extractFromNaverHtml;
    case 'cos':
      return extractFromCosHtml;
    case 'gmarket':
      return extractFromGmarketHtml;
    case 'general':
    default:
      return extractFromGeneralHtml;
  }
};

module.exports = {
  // TODO 네이버로 검색한 쇼핑 목록의 경우 -> general에서 동작하도록 변경 필요
  // site.startsWith('https://search.shopping.naver.com/') ||
  // site.startsWith('https://m.search.shopping.naver.com/') ||
  // site.startsWith('https://msearch.shopping.naver.com/') ||
  onBindParsingType: async function (url) {
    const site = String(url);
    const siteType = resolveSiteType(site);
    switch (siteType) {
      case 'musinsa':
        return await parsingForMusinsa(site);
      case 'seoulstore':
        return await parsingForSeoulStore(site);
      case 'wconcept':
        return await parsingForWconcept(site);
      case 'naver':
        return await parsingForNaver(site);
      case 'cos':
        return await parsingForCos(site);
      case 'gmarket':
        return await parsingForGmarket(site);
      case 'general':
      default:
        return await parsingForGeneral(site);
    }
  },
  // 헤드리스 파서가 사용하기 위해 외부에 노출
  resolveSiteType,
  getExtractorBySiteType,
  userAgents,
  getRandomUserAgent,
};
