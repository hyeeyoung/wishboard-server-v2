const db = require('../config/db');
const { NotFound } = require('../utils/errors');
const { ErrorMessage } = require('../utils/response');
const { generateNotiItem } = require('../dto/notiItem');

module.exports = {
  selectNotiFrom30minAgo: async function () {
    // 30분 후 알람 찾기
    const sqlSelect = `
      SELECT n.item_notification_type, n.user_id, ut.fcm_token
      FROM notifications n
      INNER JOIN users u ON n.user_id = u.user_id
      INNER JOIN user_token ut ON ut.user_id = u.user_id
      WHERE 
        n.item_notification_date BETWEEN NOW() + INTERVAL 29 MINUTE AND NOW() + INTERVAL 31 MINUTE
        AND u.push_state = true
    `;

    const [rows] = await db.query(sqlSelect);

    if (Array.isArray(rows) && !rows.length) {
      throw new NotFound(ErrorMessage.notiTodayNotFound);
    }

    const notiList = generateNotiItem(rows);
    return notiList;
  },
};
