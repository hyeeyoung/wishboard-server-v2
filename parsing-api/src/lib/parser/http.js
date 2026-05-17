const axios = require('axios');
const logger = require('../../config/winston');
const { NotFound } = require('../../utils/errors');
const { ErrorMessage } = require('../../utils/response');

const userAgents = [
  'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.67 Safari/537.36',
  'Mozilla/5.0 (Linux; U; Android 2.1; en-us; sdk Build/ERD79) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17',
];

const getRandomUserAgent = () => {
  return userAgents[Math.floor(Math.random() * userAgents.length)];
};

const buildRequestConfig = () => ({
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
});

const getHtml = async (url) => {
  try {
    return await axios.get(encodeURI(url), buildRequestConfig());
  } catch (err) {
    logger.error(err);
    throw new NotFound(ErrorMessage.itemParseFail);
  }
};

module.exports = {
  userAgents,
  getRandomUserAgent,
  getHtml,
};
