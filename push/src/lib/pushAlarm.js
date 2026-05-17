const { firebaseAdmin } = require('../config/firebaseAdmin');
const logger = require('../config/winston');
const { SuccessMessage, ErrorMessage } = require('../utils/response');
const { Strings, NotiTypeLabels } = require('../utils/strings');
const Slack = require('../lib/slack');

const buildMessage = (token, notiTypes) => {
  const numOfNotiItems = notiTypes.length;
  const notiLabel = NotiTypeLabels[notiTypes[0]];
  const body =
    numOfNotiItems === 1
      ? `${Strings.after30minutes} ${notiLabel} ${Strings.notiMessageDescription}`
      : `${Strings.after30minutes} ${notiLabel} 외 ${numOfNotiItems - 1}개의 ${Strings.notiMessageCountDescription}`;

  return {
    notification: {
      title: Strings.notiMessageTitle,
      body,
    },
    android: {
      data: {
        title: Strings.notiMessageTitle,
        body,
      },
    },
    apns: {
      payload: {
        aps: {
          alert: {
            title: Strings.notiMessageTitle,
            body,
          },
        },
      },
    },
    token,
  };
};

const sendFcmTokenToFirebase = async (notiList) => {
  try {
    const messages = [];
    Object.keys(notiList).forEach((userId) => {
      const { notiTypes, tokens } = notiList[userId];
      for (const token of tokens) {
        messages.push(buildMessage(token, notiTypes));
      }
    });

    if (messages.length === 0) {
      return true;
    }

    // firebase-admin v13: sendEach 로 다중 메시지 일괄 전송
    const result = await firebaseAdmin.messaging().sendEach(messages);
    logger.info(
      `${SuccessMessage.notiFCMSend} (success: ${result.successCount}, failure: ${result.failureCount})`,
    );

    if (result.failureCount > 0) {
      const failedMessages = result.responses
        .map((resp, idx) => (!resp.success ? messages[idx] : null))
        .filter(Boolean);

      // 실패 토큰에 한하여 1회 재전송
      const retryResult = await firebaseAdmin
        .messaging()
        .sendEach(failedMessages);
      logger.info(
        `${SuccessMessage.notiFCMSend} retry (success: ${retryResult.successCount}, failure: ${retryResult.failureCount})`,
      );

      Slack.sendMessage({
        color: Slack.Colors.warning,
        title: '푸쉬 알림 실패에 따른 재전송 성공 여부 Responses',
        text: `\`\`\`${JSON.stringify(retryResult)}\`\`\``,
      });
    }

    return true;
  } catch (e) {
    const firebaseError = { err: e };
    if (firebaseError.err.code == 'messaging/invalid-payload') {
      logger.error(ErrorMessage.notiFCMSendError);
      Slack.sendMessage({
        color: Slack.Colors.danger,
        title: `${ErrorMessage.notiFCMSendError}`,
      });
    } else {
      logger.error(e);
      Slack.sendMessage({
        color: Slack.Colors.danger,
        title: 'Firebase FCM Server 에러',
        fields: [
          {
            title: 'Error Stack:',
            value: `\`\`\`${e}\`\`\``,
          },
        ],
      });
    }
    return false;
  }
};

module.exports = {
  sendFcmTokenToFirebase,
};
