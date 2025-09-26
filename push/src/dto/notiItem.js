function generateNotiItem(notiList) {
  const result = {};

  for (const data of notiList) {
    const userId = data.user_id;
    const notiType = data.item_notification_type;
    const token = data.fcm_token;

    if (!result[userId]) {
      result[userId] = {
        notiTypes: new Set(),
        tokens: new Set(),
      };
    }

    result[userId].notiTypes.add(notiType);
    result[userId].tokens.add(token);
  }

  // Set -> Array
  Object.keys(result).forEach((userId) => {
    result[userId].notiTypes = Array.from(result[userId].notiTypes);
    result[userId].tokens = Array.from(result[userId].tokens);
  });

  return result;
}

module.exports = { generateNotiItem };
