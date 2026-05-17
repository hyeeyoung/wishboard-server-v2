const { getHtml, userAgents, getRandomUserAgent } = require('./http');
const { emptyResult } = require('./utils');

const { extractFromGeneralHtml } = require('./extractors/general');
const { extractFromMusinsaHtml } = require('./extractors/musinsa');
const { extractFromWconceptHtml } = require('./extractors/wconcept');
const { extractFromNaverHtml } = require('./extractors/naver');
const { extractFromCosHtml } = require('./extractors/cos');
const { extractFromGmarketHtml } = require('./extractors/gmarket');
const { extractFromSeoulStoreHtml } = require('./extractors/seoulstore');

const SITE_PREFIXES = [
  {
    siteType: 'musinsa',
    prefixes: [
      'https://store.musinsa.com/',
      'https://musinsaapp.page.link/',
      'https://www.musinsa.com/',
    ],
  },
  {
    siteType: 'seoulstore',
    prefixes: ['https://www.seoulstore.com/'],
  },
  {
    siteType: 'wconcept',
    prefixes: ['https://m.wconcept.co.kr/', 'https://www.wconcept.co.kr/'],
  },
  {
    siteType: 'naver',
    prefixes: [
      'https://m.smartstore.naver.com/',
      'https://smartstore.naver.com/',
      'https://m.shopping.naver.com/',
      'https://brand.naver.com/',
      'https://toptop.naver.com/',
    ],
  },
  {
    siteType: 'cos',
    prefixes: ['https://www.cosstores.com/', 'https://www.cos.com/'],
  },
  {
    siteType: 'gmarket',
    prefixes: ['http://mitem.gmarket.co.kr/', 'http://item.gmarket.co.kr/'],
  },
];

const EXTRACTORS = {
  musinsa: extractFromMusinsaHtml,
  seoulstore: extractFromSeoulStoreHtml,
  wconcept: extractFromWconceptHtml,
  naver: extractFromNaverHtml,
  cos: extractFromCosHtml,
  gmarket: extractFromGmarketHtml,
  general: extractFromGeneralHtml,
};

const resolveSiteType = (url) => {
  const site = String(url);
  for (const { siteType, prefixes } of SITE_PREFIXES) {
    if (prefixes.some((p) => site.startsWith(p))) {
      return siteType;
    }
  }
  return 'general';
};

const getExtractorBySiteType = (siteType) => {
  return EXTRACTORS[siteType] || EXTRACTORS.general;
};

const onBindParsingType = async (url) => {
  const site = String(url);
  const html = await getHtml(site);
  if (html.status !== 200) {
    return emptyResult();
  }
  const extract = getExtractorBySiteType(resolveSiteType(site));
  return extract(html.data, site);
};

module.exports = {
  onBindParsingType,
  resolveSiteType,
  getExtractorBySiteType,
  userAgents,
  getRandomUserAgent,
};
