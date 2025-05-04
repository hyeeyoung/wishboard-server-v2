package com.wishboard.server.auth.infrastructure.jwt;

import java.security.Key;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.wishboard.server.auth.presentation.dto.response.TokenResponseDto;
import com.wishboard.server.common.constant.JwtKeys;
import com.wishboard.server.common.constant.RedisKey;
import com.wishboard.server.common.util.YamlPropertySourceFactory;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@PropertySource(value = "classpath:application-jwt.yml", factory = YamlPropertySourceFactory.class, ignoreResourceNotFound = true)
public class JwtClient {

	private static final long EXPIRED_TIME = 1L;
	private final RedisTemplate<String, Object> redisTemplate;
	private final Key secretKey;

	@Value("${jwt.access-token-expire-time}")
	private Long accessTokenExpireTime;
	@Value("${jwt.refresh-token-expire-time}")
	private Long refreshTokenExpireTime;

	public JwtClient(@Value("${jwt.secret}") String secretKey, RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		this.secretKey = Keys.hmacShaKeyFor(keyBytes);
	}

	public TokenResponseDto createTokenInfo(Long userId, String deviceInfo) {
		long now = (new Date()).getTime();
		Date accessTokenExpiresIn = new Date(now + accessTokenExpireTime);
		Date refreshTokenExpiresIn = new Date(now + refreshTokenExpireTime);

		// Access Token 생성
		String accessToken = Jwts.builder()
			.claim(JwtKeys.USER_ID, userId)
			.setExpiration(accessTokenExpiresIn)
			.signWith(secretKey, SignatureAlgorithm.HS512)
			.compact();

		// Refresh Token 생성
		String refreshToken = Jwts.builder()
			.setExpiration(refreshTokenExpiresIn)
			.signWith(secretKey, SignatureAlgorithm.HS512)
			.compact();

		saveRefreshTokenToRedis(userId, deviceInfo, refreshToken, now);

		return TokenResponseDto.of(accessToken, refreshToken);
	}

	public void expireRefreshToken(Long userId, String deviceInfo) {
		String keyName = generateKeyName(RedisKey.REFRESH_TOKEN, deviceInfo, String.valueOf(userId));
		expireRefreshToken(keyName);
	}

	private void expireRefreshToken(String keyName) {
		redisTemplate.opsForValue().set(keyName, "", EXPIRED_TIME, TimeUnit.MILLISECONDS);
	}

	public String getRefreshToken(Long userId, String deviceInfo) {
		// v2 전 기존 키 형식의 토큰이 존재한다면
		String refreshToken = (String)redisTemplate.opsForValue().get(String.valueOf(userId));
		if (StringUtils.hasText(refreshToken)) {
			return refreshToken;
		}

		return (String)redisTemplate.opsForValue().get(generateKeyName(RedisKey.REFRESH_TOKEN, deviceInfo, String.valueOf(userId)));
	}

	public Boolean isLogoutDevice(Long userId, String deviceInfo) {
		String logoutDeviceKeyName = generateKeyName(RedisKey.LOGOUT_FLAG, deviceInfo, String.valueOf(userId));
		if (Boolean.TRUE.equals(redisTemplate.hasKey(logoutDeviceKeyName))) {
			redisTemplate.delete(logoutDeviceKeyName);
			return true;
		}
		return false;
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
			return true;
		} catch (SecurityException | MalformedJwtException e) {
			log.info("Invalid JWT Token", e);
		} catch (ExpiredJwtException e) {
			log.info("Expired JWT Token", e);
		} catch (UnsupportedJwtException e) {
			log.info("Unsupported JWT Token", e);
		} catch (IllegalArgumentException e) {
			log.info("JWT claims string is empty.", e);
		}
		return false;
	}

	public Long getUserIdFromJwt(String accessToken) {
		return parseClaims(accessToken).get(JwtKeys.USER_ID, Long.class);
	}

	private Claims parseClaims(String accessToken) {
		try {
			return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(accessToken).getBody();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		}
	}

	private String generateKeyName(String... names) {
		return String.join("_", names);
	}

	private void saveRefreshTokenToRedis(Long userId, String deviceInfo, String refreshToken, Long now) {
		String refreshTokenKeyName = generateKeyName(RedisKey.REFRESH_TOKEN, deviceInfo, String.valueOf(userId));
		String zsetKeyName = generateKeyName(RedisKey.USER_DEVICE, String.valueOf(userId));

		// (legacy) v1 에 저장된 형태를 마이그레이션 하면서 v2 형태로 저장
		if (migrateLegacyRefreshTokenIfExist(userId, refreshTokenKeyName, zsetKeyName, now)) {
			return;
		}

		// 신규 토큰 저장
		saveNewRefreshToken(refreshTokenKeyName, refreshToken, zsetKeyName, now);

		// 로그인 기기 초과 시 정리
		removeOldestDeviceIfExceedLimit(zsetKeyName, userId);
	}

	private boolean migrateLegacyRefreshTokenIfExist(Long userId, String newRefreshTokenKeyName, String zsetKeyName, Long now) {
		String legacyRefreshTokenKeyName = String.valueOf(userId);
		if (Boolean.TRUE.equals(redisTemplate.hasKey(legacyRefreshTokenKeyName))) {
			String legacyRefreshToken = (String)redisTemplate.opsForValue().get(legacyRefreshTokenKeyName);
			if (StringUtils.hasText(legacyRefreshToken)) {
				saveNewRefreshToken(newRefreshTokenKeyName, legacyRefreshToken, zsetKeyName, now);
				expireRefreshToken(legacyRefreshTokenKeyName);
				return true;
			}
		}
		return false;
	}

	private void saveNewRefreshToken(String refreshTokenKeyName, String refreshToken, String zsetKeyName, Long now) {
		redisTemplate.opsForValue().set(refreshTokenKeyName, refreshToken, refreshTokenExpireTime, TimeUnit.MILLISECONDS);
		redisTemplate.opsForZSet().add(zsetKeyName, refreshTokenKeyName, now);
	}

	private void removeOldestDeviceIfExceedLimit(String zsetKeyName, Long userId) {
		Long loginDeviceCount = redisTemplate.opsForZSet().size(zsetKeyName);
		if (loginDeviceCount != null && loginDeviceCount > 3) {
			Set<Object> oldest = redisTemplate.opsForZSet().range(zsetKeyName, 0, 0);
			if (oldest != null && !oldest.isEmpty()) {
				String oldRefreshTokenKeyName = oldest.iterator().next().toString();
				expireRefreshToken(oldRefreshTokenKeyName);
				redisTemplate.opsForZSet().remove(zsetKeyName, oldRefreshTokenKeyName);
				log.info("Exceeded device limit. Deleted oldest refresh token: {}", oldRefreshTokenKeyName);
				String oldKeyDeviceInfo = oldRefreshTokenKeyName.split("_")[1];
				redisTemplate.opsForValue()
					.set(generateKeyName(RedisKey.LOGOUT_FLAG, oldKeyDeviceInfo, String.valueOf(userId)), String.valueOf(true));
			}
		}
	}
}
