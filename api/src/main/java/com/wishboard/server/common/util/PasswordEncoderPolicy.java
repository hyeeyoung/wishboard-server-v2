package com.wishboard.server.common.util;

import static com.wishboard.server.common.exception.ErrorCode.*;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.wishboard.server.common.exception.ValidationException;

import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
public class PasswordEncoderPolicy {

	private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	public String encode(String password) {
		if (!StringUtils.hasText(password)) {
			throw new ValidationException("Password is null or empty", VALIDATION_REQUEST_MISSING_EXCEPTION);
		}
		return encoder.encode(password);
	}

	public boolean matches(String password, String hashedPassword) {
		return encoder.matches(password, hashedPassword);
	}
}
