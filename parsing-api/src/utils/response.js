const StatusCode = {
  OK: 200,
  CREATED: 201,
  NO_CONTENT: 204,
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  NOTFOUND: 404,
  CONFLICT: 409,
  TOO_MANY_REQUEST: 429,
};

const SuccessMessage = {
  /* 아이템 */
  itemParse: '아이템 파싱 성공',
};

const ErrorMessage = {
  /* 아이템*/
  itemParseFail: '아이템 파싱 실패',
  itemSiteUrlNotFound: '요청하신 URL은 유효한 링크가 아닙니다.',

  /* 공통*/
  BadRequest: '잘못된 요청',
  ApiUrlIsInvalid: '잘못된 경로',
  RequestWithIntentionalInvalidUrl: '의도적인 잘못된 경로 요청',
  TOO_MANY_REQUEST: 'Too many accounts created from this IP',
};

module.exports = { StatusCode, SuccessMessage, ErrorMessage };
