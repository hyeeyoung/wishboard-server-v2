const { GeneralError } = require('../utils/errors');
require('dotenv').config({ path: '../.env' });
const logger = require('../config/winston');
const Slack = require('../lib/slack');
const { ErrorMessage } = require('../utils/response');

const handleErrors = (err, req, res, next) => {
  console.log(err);
  logger.error(err.stack); 

  //* Custom Error
  if (err instanceof GeneralError) {
    //* 슬랙에 알림은 production 모드인 경우에만
    if (
      process.env.NODE_ENV === 'production' &&
      err.message !== ErrorMessage.RequestWithIntentionalInvalidUrl
    ) {
      Slack.sendMessage({
        color: Slack.Colors.info,
        title: `${err.getCode()} ${err.message}`,
        text: `[${req.method}] ${req.url} \`\`\`user-agent: ${req.header(
          'user-agent',
        )} \nip-adress: ${req.ip}\`\`\``,
      });
    }

    return res.status(err.getCode()).json({
      success: false,
      message: err.message,
    });
  }

  //* 슬랙에 알림은 production 모드인 경우에만
  if (process.env.NODE_ENV === 'production') {
    Slack.sendMessage({
      color: Slack.Colors.danger,
      title: 'wishboard 서버 에러',
      text: err,
      fields: [
        {
          title: 'Error Stack:',
          value: `\`\`\`${err.stack}\`\`\``,
        },
      ],
    });
  }
  return res.status(500).json({
    success: false,
    message: 'wishboard 서버 에러',
  });
};

module.exports = handleErrors;
