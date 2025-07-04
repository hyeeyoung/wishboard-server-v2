package com.wishboard.server.common.exception;

import static com.wishboard.server.common.exception.ErrorStatusCode.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {

	/**
	 * 400 Bad Request
	 */
	VALIDATION_EXCEPTION(BAD_REQUEST, "잘못된 요청입니다."),
	VALIDATION_ENUM_VALUE_EXCEPTION(BAD_REQUEST, "잘못된 Enum 값 입니다."),
	VALIDATION_REQUEST_MISSING_EXCEPTION(BAD_REQUEST, "필수적인 요청 값이 입력되지 않았습니다."),
	VALIDATION_WRONG_TYPE_EXCEPTION(BAD_REQUEST, "잘못된 타입이 입력되었습니다."),
	VALIDATION_SOCIAL_TYPE_EXCEPTION(BAD_REQUEST, "잘못된 소셜 프로바이더 입니다."),
	VALIDATION_HEADER_USER_AGENT_EXCEPTION(BAD_REQUEST, "잘못된 User-Agent 입니다."),
	VALIDATION_USER_AGENT_EXCEPTION(BAD_REQUEST, "허용하지 않는 User-Agent의 요청입니다."),
	VALIDATION_DEVICE_INFO_EXCEPTION(BAD_REQUEST, "Request Header에 디바이스 정보가 없습니다."),
	VALIDATION_SORT_TYPE_EXCEPTION(BAD_REQUEST, "허용하지 않는 정렬기준을 입력했습니다."),
	VALIDATION_RE_SIGNIN_VERIFY_EXCEPTION(BAD_REQUEST, "검증되지 않았습니다. (verify)"),
	VALIDATION_PASSWORD_EXCEPTION(BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
	VALIDATION_NOTIFICATION_EXCEPTION(BAD_REQUEST, "알림 날짜는 현재 날짜보다 같거나 미래만 가능합니다."),
	VALIDATION_NOTIFICATION_MINUTE_EXCEPTION(BAD_REQUEST, "알림 날짜는 30분 단위로만 등록 가능합니다. (00분 또는 30분)"),
	VALIDATION_ITEM_CREATE_ADD_TYPE_EXCEPTION(BAD_REQUEST, "아이템 등록 타입 (type)은 필수입니다."),
	VALIDATION_ITEM_IMAGE_MAX_COUNT_EXCEPTION(BAD_REQUEST, "아이템 이미지는 최소 1장부터 최대 10장까지 가능합니다."),
	VALIDATION_IMAGE_SIZE_EXCEPTION(BAD_REQUEST, "이미지 최대 크기는 720x720 입니다."),
	VALIDATION_FOLDER_NAME_DUPLICATE_EXCEPTION(BAD_REQUEST, "이미 존재하는 폴더명입니다."),

	/**
	 * 401 UnAuthorized
	 */
	UNAUTHORIZED_EXCEPTION(UNAUTHORIZED, "토큰이 만료되었습니다. 다시 로그인 해주세요."),
	UNAUTHORIZED_INVALID_TOKEN_EXCEPTION(UNAUTHORIZED, "유효하지 않은 토큰입니다."),

	/**
	 * 403 Forbidden
	 */
	FORBIDDEN_EXCEPTION(FORBIDDEN, "허용하지 않는 요청입니다."),
	FORBIDDEN_FILE_TYPE_EXCEPTION(BAD_REQUEST, "허용되지 않은 파일 형식입니다."),
	FORBIDDEN_FILE_NAME_EXCEPTION(BAD_REQUEST, "허용되지 않은 파일 이름입니다."),

	/**
	 * 404 Not Found
	 */
	NOT_FOUND_EXCEPTION(NOT_FOUND, "존재하지 않습니다."),
	NOT_FOUND_USER_EXCEPTION(NOT_FOUND, "탈퇴했거나 존재하지 않는 유저입니다."),
	NOT_FOUND_REFRESH_TOKEN_EXCEPTION(NOT_FOUND, "만료된 리프레시 토큰입니다."),
	NOT_FOUND_ONBOARDING_EXCEPTION(NOT_FOUND, "유저의 온보딩 정보가 존재하지 않습니다."),
	NOT_FOUND_ITEM_EXCEPTION(NOT_FOUND, "존재하지 않는 아이템입니다."),
	NOT_FOUND_FOLDER_EXCEPTION(NOT_FOUND, "존재하지 않는 폴더입니다."),
	NOT_FOUND_NOTIFICATION_EXCEPTION(NOT_FOUND, "존재하지 않는 알림입니다."),

	/**
	 * 405 Method Not Allowed
	 */
	METHOD_NOT_ALLOWED_EXCEPTION(METHOD_NOT_ALLOWED, "지원하지 않는 메소드 입니다."),

	/**
	 * 406 Not Acceptable
	 */
	NOT_ACCEPTABLE_EXCEPTION(NOT_ACCEPTABLE, "Not Acceptable"),

	/**
	 * 409 Conflict
	 */
	CONFLICT_EXCEPTION(CONFLICT, "이미 존재합니다."),
	CONFLICT_USER_EXCEPTION(CONFLICT, "이미 해당 계정으로 회원가입하셨습니다.\n로그인 해주세요."),
	CONFLICT_ITEM_EXCEPTION(CONFLICT, "다른 사용자의 아이템입니다."),

	/**
	 * 415 Unsupported Media Type
	 */
	UNSUPPORTED_MEDIA_TYPE_EXCEPTION(UNSUPPORTED_MEDIA_TYPE, "해당하는 미디어 타입을 지원하지 않습니다."),

	/**
	 * 500 Internal Server Exception
	 */
	INTERNAL_SERVER_EXCEPTION(INTERNAL_SERVER, "예상치 못한 서버 에러가 발생하였습니다."),
	MAIL_SEND_FAILED_EXCEPTION(INTERNAL_SERVER, "이메일 전송 중 예상치 못한 서버 에러가 발생하였습니다."),

	/**
	 * 502 Bad Gateway
	 */
	BAD_GATEWAY_EXCEPTION(BAD_GATEWAY, "일시적인 에러가 발생하였습니다.\n잠시 후 다시 시도해주세요!"),

	/**
	 * 503 Service UnAvailable
	 */
	SERVICE_UNAVAILABLE_EXCEPTION(SERVICE_UNAVAILABLE, "현재 점검 중입니다.\n잠시 후 다시 시도해주세요!"),
	;

	private final ErrorStatusCode statusCode;
	private final String message;

	public int getStatus() {
		return statusCode.getStatus();
	}
}
