package com.wishboard.server.common.success;

import static com.wishboard.server.common.success.SuccessStatusCode.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SuccessCode {

	/**
	 * 200 OK
	 */
	SUCCESS(OK, "성공입니다."),

	// 인증
	LOGIN_SUCCESS(OK, "로그인 성공입니다."),
	REFRESH_TOKEN_SUCCESS(OK, "토큰 갱신 성공입니다."),

	CHECK_EMAIL_SUCCESS(OK, "wishboard 이메일 확인 성공입니다."),
	SIGNUP_SUCCESS(OK, "wishboard 회원가입 후 로그인 성공입니다."),
	SIGNIN_SUCCESS(OK, "wishboard 로그인 성공입니다."),
	RESIGNIN_SUCCESS(OK, "wishboard 재로그인 성공입니다."),
	SEND_MAIL_SUCCESS(OK, " 이메일 인증 메일 전송 성공입니다."),

	// 유저
	USER_INFO_SUCCESS(OK, "사용자 정보 조회 성공입니다."),
	USER_PUSH_STATE_UPDATE_SUCCESS(OK, "푸시 알림 설정 변경 성공입니다."),
	USER_INFO_UPDATE_SUCCESS(OK, "사용자 정보 변경 성공입니다."),
	USER_PASSWORD_UPDATE_SUCCESS(OK, "사용자 wishboard 비밀번호 성공입니다."),
	USER_DELETE_SUCCESS(OK, "사용자 탈퇴 성공입니다."),

	ITEM_LIST_INFO_SUCCESS(OK, "아이템 리스트 조회 성공입니다."),
	ITEM_INFO_SUCCESS(OK, "아이템 정보 조회 성공입니다."),
	ITEM_CREATE_SUCCESS(OK, "아이템 생성 성공입니다."),
	ITEM_AND_NOTIFICATION_CREATE_SUCCESS(OK, "아이템 및 알림 생성 성공입니다."),
	ITEM_UPDATE_SUCCESS(OK, "아이템 수정 성공입니다."),
	ITEM_AND_NOTIFICATION_UPDATE_SUCCESS(OK, "아이템 및 알림 수정 성공입니다."),
	ITEM_AND_NOTIFICATION_DELETE_SUCCESS(OK, "아이템 삭제 성공입니다."),
	ITEM_FOLDER_UPDATE_SUCCESS(OK, "아이템 폴더 수정 성공입니다."),

	NOTIFICATION_HISTORY_LOAD_SUCCESS(OK, "알림 내역 조회 성공입니다."),
	NOTIFICATIONS_LIST_SUCCESS(OK, "알림 전체 조회 성공입니다."),
	NOTIFICATIONS_READ_STATE_UPDATE_SUCCESS(OK, "아이템 읽음 상태 수정 성공입니다."),
	/**
	 * 201 CREATED
	 */

	/**
	 * 202 ACCEPTED
	 */

	/**
	 * 204 NO_CONTENT
	 */
	;

	private final SuccessStatusCode statusCode;
	private final String message;

	public int getStatus() {
		return statusCode.getStatus();
	}
}
