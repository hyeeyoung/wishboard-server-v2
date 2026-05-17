const { onBindParsingType } = require('../lib/parser');
const { parseWithHeadless } = require('../lib/headlessParser');
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
