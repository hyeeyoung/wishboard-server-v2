package com.wishboard.server.common.exception;

public class UnAuthorizedException extends WishboardServerException {

    public UnAuthorizedException(String message) {
        super(message, ErrorCode.UNAUTHORIZED_EXCEPTION);
    }
}
