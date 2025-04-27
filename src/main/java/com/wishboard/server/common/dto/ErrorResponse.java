package com.wishboard.server.common.dto;

import com.wishboard.server.common.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

	private int status;
	private boolean success;
	private String message;

	public static ErrorResponse error(ErrorCode errorCode) {
		return new ErrorResponse(errorCode.getStatus(), false, errorCode.getMessage());
	}

	public static ErrorResponse error(ErrorCode errorCode, String message) {
		return new ErrorResponse(errorCode.getStatus(), false, message);
	}
}
