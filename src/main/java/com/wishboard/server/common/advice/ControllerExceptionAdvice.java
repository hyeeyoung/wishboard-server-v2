package com.wishboard.server.common.advice;

import static com.wishboard.server.common.exception.ErrorCode.*;

import java.util.Objects;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.wishboard.server.common.dto.ErrorResponse;
import com.wishboard.server.common.dto.ErrorResponseWithCode;
import com.wishboard.server.common.exception.WishboardServerException;

import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionAdvice {

	/**
	 * 400 BadRequest
	 * Spring Validation
	 */
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(BindException.class)
	protected ErrorResponse handleBadRequest(final BindException e) {
		log.error(e.getMessage(), e);
		FieldError fieldError = Objects.requireNonNull(e.getFieldError());
		return ErrorResponse.error(VALIDATION_EXCEPTION, String.format("%s (%s)", fieldError.getDefaultMessage(), fieldError.getField()));
	}

	/**
	 * 400 BadRequest
	 * Pageable에 허용하지 않는 정렬기준이 입력된 경우 발생하는 Exception
	 */
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(ConstraintViolationException.class)
	protected ErrorResponse handleConstraintViolationException(final ConstraintViolationException e) {
		log.error(e.getMessage());
		return ErrorResponse.error(VALIDATION_SORT_TYPE_EXCEPTION);
	}

	/**
	 * 400 BadRequest
	 * 잘못된 Enum 값이 입력되었거나 body 자체가 비어있는 경우 발생하는 Exception
	 */
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(HttpMessageNotReadableException.class)
	protected ErrorResponse handleHttpMessageNotReadableException(final HttpMessageNotReadableException e) {
		log.error(e.getMessage());
		return ErrorResponse.error(VALIDATION_EXCEPTION);
	}

	/**
	 * 400 BadRequest
	 * RequestParam, RequestPath, RequestPart 등의 필드가 입력되지 않은 경우 발생하는 Exception
	 */
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MissingRequestValueException.class)
	protected ErrorResponse handle(final MissingRequestValueException e) {
		log.error(e.getMessage());
		return ErrorResponse.error(VALIDATION_REQUEST_MISSING_EXCEPTION);
	}

	/**
	 * 400 BadRequest
	 * 잘못된 타입이 입력된 경우 발생하는 Exception
	 */
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(TypeMismatchException.class)
	protected ErrorResponse handleTypeMismatchException(final TypeMismatchException e) {
		log.error(e.getMessage());
		return ErrorResponse.error(VALIDATION_WRONG_TYPE_EXCEPTION,
			String.format("%s (%s)", VALIDATION_WRONG_TYPE_EXCEPTION.getMessage(), e.getValue()));
	}

	/**
	 * 400 BadRequest
	 */
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler({
		InvalidFormatException.class,
		ServletRequestBindingException.class,
		MethodArgumentTypeMismatchException.class
	})
	protected ErrorResponse handleInvalidFormatException(final Exception e) {
		log.error(e.getMessage());
		return ErrorResponse.error(VALIDATION_EXCEPTION);
	}

	/**
	 * 405 Method Not Allowed
	 * 지원하지 않은 HTTP method 호출 할 경우 발생하는 Exception
	 */
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	protected ErrorResponse handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
		log.error(e.getMessage());
		return ErrorResponse.error(METHOD_NOT_ALLOWED_EXCEPTION);
	}

	/**
	 * 406 Not Acceptable
	 */
	@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
	@ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
	protected ErrorResponse handleHttpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException e) {
		log.error(e.getMessage());
		return ErrorResponse.error(NOT_ACCEPTABLE_EXCEPTION);
	}

	/**
	 * 415 UnSupported Media Type
	 * 지원하지 않는 미디어 타입인 경우 발생하는 Exception
	 */
	@ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
	@ExceptionHandler(HttpMediaTypeException.class)
	protected ErrorResponse handleHttpMediaTypeException(final HttpMediaTypeException e) {
		log.error(e.getMessage(), e);
		return ErrorResponse.error(UNSUPPORTED_MEDIA_TYPE_EXCEPTION);
	}

	/**
	 * Boilerplate Custom Exception
	 */
	@ExceptionHandler(WishboardServerException.class)
	protected ResponseEntity<ErrorResponse> handleBaseException(WishboardServerException exception) {
		if (exception.getStatus() >= 400 && exception.getStatus() < 500) {
			log.warn(exception.getMessage(), exception);
		} else {
			log.error(exception.getMessage(), exception);
			Sentry.captureException(exception);
		}

		if (exception.getErrorCodeDetail() != null) {
			return ResponseEntity.status(exception.getStatus())
				.body(ErrorResponseWithCode.error(exception.getErrorCode(), exception.getErrorCodeDetail()));
		} else {
			return ResponseEntity.status(exception.getStatus())
				.body(ErrorResponse.error(exception.getErrorCode()));
		}
	}

	/**
	 * 500 Internal Server
	 */
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	protected ErrorResponse handleException(final Exception exception) {
		log.error(exception.getMessage(), exception);
		return ErrorResponse.error(INTERNAL_SERVER_EXCEPTION);
	}
}
