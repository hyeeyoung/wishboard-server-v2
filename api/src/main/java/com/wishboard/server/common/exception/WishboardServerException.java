package com.wishboard.server.common.exception;

import lombok.Getter;

@Getter
public abstract class WishboardServerException extends RuntimeException {

	private final ErrorCode errorCode;
	private final ErrorDetailCode errorCodeDetail;

	protected WishboardServerException(String message, ErrorCode errorCode) {
		this(message, errorCode, null);
	}

	protected WishboardServerException(String message, ErrorCode errorCode, ErrorDetailCode errorCodeDetail) {
		super(message);
		this.errorCode = errorCode;
		this.errorCodeDetail = errorCodeDetail;
	}

	public int getStatus() {
		return errorCode.getStatus();
	}
}
