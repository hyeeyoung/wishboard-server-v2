const { onBindParsingType, normalizeUrl } = require('../lib/parser');
const { parseWithHeadless } = require('../lib/headlessParser');
const { mergeResults } = require('../lib/parser/utils');
const logger = require('../config/winston');
const { BadRequest } = require('../utils/errors');
const {
  StatusCode,
  SuccessMessage,
  ErrorMessage,
} = require('../utils/response');

const isEmptyResult = (data) => {
  if (!data || typeof data !== 'object') {
    return true;
  }
  const { item_img, item_name, item_price } = data;
  return !item_img && !item_name && !item_price;
};

// itemPrice 만 결손이고 다른 필드는 있을 때 → 헤드리스로 가격만 보강.
// 정적이 부분 성공(name+img는 잡았지만 가격 미달)한 케이스를 위한 판정.
const needsPriceFallback = (data) => {
  if (!data || typeof data !== 'object') return false;
  if (isEmptyResult(data)) return false;
  return !data.item_price;
};

const isValidUrl = (url) => {
  try {
    new URL(url);
    return true;
  } catch (e) {
    return false;
  }
};

module.exports = {
  parseItemInfo: async function (req, res, next) {
    try {
      if (!req.query.site) {
        throw new BadRequest(ErrorMessage.BadRequest);
      }
      const site = req.query.site;
      if (!isValidUrl(site)) {
        throw new BadRequest(ErrorMessage.itemSiteUrlNotFound);
      }

      let data;
      let parserType = 'static';
      try {
        data = await onBindParsingType(site);
      } catch (staticErr) {
        logger.warn(
          `[parseItemInfo] static parsing threw for ${site}: ${staticErr.message}`,
        );
        data = {};
      }
      if (isEmptyResult(data)) {
        parserType = 'headless_fallback';
        data = await parseWithHeadless(site);
      } else if (needsPriceFallback(data)) {
        parserType = 'static_with_price_fallback';
        const headlessData = await parseWithHeadless(site);
        data = mergeResults(data, headlessData);
      }

      logger.info(
        `[parseItemInfo] parser_type=${parserType} url=${site} empty=${isEmptyResult(
          data,
        )}`,
      );

      if (isEmptyResult(data)) {
        return res.status(StatusCode.NO_CONTENT).json();
      }
      return res.status(StatusCode.OK).json({
        success: true,
        message: SuccessMessage.itemParse,
        data: {
          itemImageUrl: data.item_img,
          itemName: data.item_name,
          itemPrice: data.item_price,
          // 추적 파라미터 제거된 정규화 URL — 클라이언트가 백엔드 저장 시 이 값 사용 권장
          itemUrl: normalizeUrl(site),
        },
      });
    } catch (err) {
      next(err);
    }
  },
};
