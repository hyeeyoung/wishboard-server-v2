const { onBindParsingType } = require('../lib/parser');
const { parseWithHeadless } = require('../lib/headlessParser');
const { requiresHeadless } = require('../lib/headlessTargets');
const logger = require('../config/winston');
const { BadRequest } = require('../utils/errors');
const {
  StatusCode,
  SuccessMessage,
  ErrorMessage,
} = require('../utils/response');

/**
 * 추출 결과가 비어있는지 판정.
 * - 응답 객체 형식: { item_img, item_name, item_price }
 * - 모든 필드가 falsy(undefined/null/'')이면 비었다고 본다.
 */
const isEmptyResult = (data) => {
  if (!data || typeof data !== 'object') {
    return true;
  }
  const { item_img, item_name, item_price } = data;
  return !item_img && !item_name && !item_price;
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
      let parserType;

      if (requiresHeadless(site)) {
        // 경로 A — 사전 분류 (Direct Headless)
        parserType = 'headless';
        data = await parseWithHeadless(site);
      } else {
        // 경로 B — Static → Headless fallback
        parserType = 'static';
        try {
          data = await onBindParsingType(site);
        } catch (staticErr) {
          // 정적 파싱이 NotFound 등으로 실패한 경우에도 fallback 시도
          logger.warn(
            `[parseItemInfo] static parsing threw for ${site}: ${staticErr.message}`,
          );
          data = {};
        }
        if (isEmptyResult(data)) {
          parserType = 'headless_fallback';
          data = await parseWithHeadless(site);
        }
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
        },
      });
    } catch (err) {
      next(err);
    }
  },
};
