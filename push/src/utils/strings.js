const Strings = {
  /* 알림 */
  notiMessageTitle: '상품일정알림',
  after30minutes: '30분 후에',
  notiMessageDescription: '아이템이 있어요!',
  notiMessageCountDescription: '상품 일정이 있어요!',

  //* 추후 전체 공지 알림 예정일 경우 사용
  // NOTI_SCREEN: 'NOTI_SCREEN',
  // EVENT_SCEREEN: 'EVENT_SCEREEN',

  /* scheduler 시작/종료 */
  pushNotiSchedulerStart: '푸쉬 알림 스케줄러 시작',
  pushNotiSchedulerEnd: '푸쉬 알림 스케줄러 종료',
};

const NotiTypeLabels = {
  SALE_START: "세일 시작",
  SALE_END: "세일 마감",
  REMINDER: "리마인드",
  OPEN: "오픈",
  RESTOCK: "재입고",
  PREORDER: "프리오더",
};


module.exports = {
  Strings,
  NotiTypeLabels
};
