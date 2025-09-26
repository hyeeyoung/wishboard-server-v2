package com.wishboard.server.common.exception;

public class ValidationException extends WishboardServerException {

    public ValidationException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public ValidationException(String message) {
        super(message, ErrorCode.VALIDATION_EXCEPTION);
    }
}
