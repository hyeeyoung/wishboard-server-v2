package com.wishboard.server.common.exception;

import lombok.Getter;

@Getter
public abstract class WishboardServerException extends RuntimeException {

    private final ErrorCode errorCode;

    public WishboardServerException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getStatus() {
        return errorCode.getStatus();
    }
}
