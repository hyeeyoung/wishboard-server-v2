package com.wishboard.server.common.exception;

public class UnAuthorizedException extends WishboardServerException {

	public UnAuthorizedException(String message) {
		super(message, ErrorCode.UNAUTHORIZED_EXCEPTION);
	}

	public UnAuthorizedException(String message, ErrorCode errorCode, ErrorDetailCode errorCodeDetail) {
		super(message, errorCode, errorCodeDetail);
	}

	public UnAuthorizedException(String message, ErrorDetailCode errorCodeDetail) {
		super(message, ErrorCode.UNAUTHORIZED_EXCEPTION, errorCodeDetail);
	}
}
