package com.wishboard.server.service.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.wishboard.server.common.exception.ValidationException;

import io.micrometer.common.util.StringUtils;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AuthServiceUtils {

	private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	public static String getHashedPassword(String password) {
		if (password == null || StringUtils.isBlank(password)) {
			throw new ValidationException("Password is null or empty");
		}
		return encoder.encode(password);
	}
}
