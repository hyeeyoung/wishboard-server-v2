package com.wishboard.server.common.dto;

import com.wishboard.server.common.exception.ErrorCode;
import com.wishboard.server.common.exception.ErrorDetailCode;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ErrorResponseWithCode extends ErrorResponse {

	private ErrorDetailCode code;

	private ErrorResponseWithCode(ErrorCode errorCode, String message, ErrorDetailCode detailCode) {
		super(errorCode.getStatus(), false, message);
		this.code = detailCode;
	}

	public static ErrorResponseWithCode error(ErrorCode errorCode, ErrorDetailCode detailCode) {
		return new ErrorResponseWithCode(errorCode, errorCode.getMessage(), detailCode);
	}
}
