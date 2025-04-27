package com.wishboard.server.service.auth;

import static com.wishboard.server.common.exception.ErrorCode.*;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;

import com.wishboard.server.common.exception.ValidationException;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AuthServiceUtils {

	private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	public static String getHashedPassword(String password) {
		if (!StringUtils.hasText(password)) {
			throw new ValidationException("Password is null or empty", VALIDATION_REQUEST_MISSING_EXCEPTION);
		}
		return encoder.encode(password);
	}
}
