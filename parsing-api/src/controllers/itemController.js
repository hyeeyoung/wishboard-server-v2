const { onBindParsingType } = require('../lib/parser');
const { BadRequest } = require('../utils/errors');
const {
  StatusCode,
  SuccessMessage,
  ErrorMessage,
} = require('../utils/response');

const existEmptyData = (obj) => {
  if (obj.constructor !== Object) {
    return false;
  }
  if (JSON.stringify(obj) !== '{}') {
    return false;
  }
  return true;
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
      if (!isValidUrl(req.query.site)) {
        throw new BadRequest(ErrorMessage.itemSiteUrlNotFound);
      }
      await onBindParsingType(req.query.site)
        .then((data) => {
          if (existEmptyData(data)) {
            return res.status(StatusCode.NO_CONTENT).json();
          }
          return res.status(StatusCode.OK).json({
            success: true,
            message: SuccessMessage.itemParse,
            data,
          });
        })
        .catch((parserFailError) => next(parserFailError));
    } catch (err) {
      next(err);
    }
  },
};
