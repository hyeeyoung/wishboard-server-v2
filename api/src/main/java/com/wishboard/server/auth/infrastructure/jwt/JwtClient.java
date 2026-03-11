package com.wishboard.server.auth.infrastructure.jwt;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.wishboard.server.auth.presentation.dto.response.TokenResponseDto;
import com.wishboard.server.common.constant.JwtKeys;
import com.wishboard.server.common.constant.RedisKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtClient {
	private static final long REFRESH_ROTATE_GRACE_MILLIS = 15_000L;
	private static final String TOKEN_PAIR_DELIMITER = "|";
	private static final String KEY_WILDCARD = "*";

	public enum RefreshTokenRotateStatus {
		SUCCESS,
		TOKEN_NOT_FOUND,
		TOKEN_MISMATCH
	}

	public record RotateTokenResult(RefreshTokenRotateStatus status, TokenResponseDto tokenInfo) {
		public static RotateTokenResult success(TokenResponseDto tokenInfo) {
			return new RotateTokenResult(RefreshTokenRotateStatus.SUCCESS, tokenInfo);
		}

		public static RotateTokenResult tokenNotFound() {
			return new RotateTokenResult(RefreshTokenRotateStatus.TOKEN_NOT_FOUND, null);
		}

		public static RotateTokenResult tokenMismatch() {
			return new RotateTokenResult(RefreshTokenRotateStatus.TOKEN_MISMATCH, null);
		}
	}

	private final RedisTemplate<String, Object> redisTemplate;
	private final RedisScript<Long> refreshTokenRotateCasScript;
	private final Key secretKey;

	@Value("${jwt.access-token-expire-time}")
	private Long accessTokenExpireTime;
	@Value("${jwt.refresh-token-expire-time}")
	private Long refreshTokenExpireTime;

	public JwtClient(@Value("${jwt.secret}") String secret, RedisTemplate<String, Object> redisTemplate,
		@Qualifier("refreshTokenRotateCasScript") RedisScript<Long> refreshTokenRotateCasScript) {
		this.redisTemplate = redisTemplate;
		this.refreshTokenRotateCasScript = refreshTokenRotateCasScript;
		byte[] keyBytes = Decoders.BASE64.decode(secret);
		this.secretKey = Keys.hmacShaKeyFor(keyBytes);
	}

	public TokenResponseDto createTokenInfo(Long userId, String deviceInfo) {
		long now = (new Date()).getTime();
		String accessToken = createAccessToken(userId, now);
		String refreshToken = createRefreshToken(now);

		saveRefreshTokenToRedis(userId, deviceInfo, refreshToken, now);

		return TokenResponseDto.of(accessToken, refreshToken);
	}

	public RotateTokenResult rotateTokenInfoWithCompareAndSet(Long userId, String deviceInfo, String currentRefreshToken) {
		long now = (new Date()).getTime();
		String newAccessToken = createAccessToken(userId, now);
		String newRefreshToken = createRefreshToken(now);

		String refreshTokenKeyName = generateKeyName(RedisKey.REFRESH_TOKEN, deviceInfo, String.valueOf(userId));
		String legacyRefreshTokenKeyName = String.valueOf(userId);
		String zsetKeyName = generateKeyName(RedisKey.USER_DEVICE, String.valueOf(userId));
		String graceKeyName = generateGraceKeyName(userId, deviceInfo, currentRefreshToken);
		String graceValue = newAccessToken + TOKEN_PAIR_DELIMITER + newRefreshToken;

		Long scriptResult = redisTemplate.execute(
			refreshTokenRotateCasScript,
			List.of(refreshTokenKeyName, legacyRefreshTokenKeyName, zsetKeyName, graceKeyName),
			currentRefreshToken,
			newRefreshToken,
			String.valueOf(refreshTokenExpireTime),
			String.valueOf(now),
			graceValue,
			String.valueOf(REFRESH_ROTATE_GRACE_MILLIS)
		);

		if (scriptResult == 0L) {
			return RotateTokenResult.tokenNotFound();
		}

		if (scriptResult == -1L) {
			var graceTokenPair = getGraceRotatedToken(userId, deviceInfo, currentRefreshToken);
			if (graceTokenPair != null) {
				return RotateTokenResult.success(graceTokenPair);
			}
			return RotateTokenResult.tokenMismatch();
		}

		removeOldestDeviceIfExceedLimit(zsetKeyName, userId);
		var tokenInfo = TokenResponseDto.of(newAccessToken, newRefreshToken);
		return RotateTokenResult.success(tokenInfo);
	}

	public void expireRefreshToken(Long userId, String deviceInfo) {
		String keyName = generateKeyName(RedisKey.REFRESH_TOKEN, deviceInfo, String.valueOf(userId));
		expireRefreshToken(keyName);
		clearGraceRotatedTokenCacheForDevice(userId, deviceInfo);
	}

	private void expireRefreshToken(String keyName) {
		redisTemplate.delete(keyName);
	}

	public String getRefreshToken(Long userId, String deviceInfo) {
		// v2 전 기존 키 형식의 토큰이 존재한다면
		String legacy = (String)redisTemplate.opsForValue().get(String.valueOf(userId));
		if (StringUtils.hasText(legacy)) {
			return legacy;
		}

		return (String)redisTemplate.opsForValue().get(generateKeyName(RedisKey.REFRESH_TOKEN, deviceInfo, String.valueOf(userId)));
	}

	public Boolean isLogoutDevice(Long userId, String deviceInfo) {
		String logoutDeviceKeyName = generateKeyName(RedisKey.LOGOUT_FLAG, deviceInfo, String.valueOf(userId));
		if (redisTemplate.hasKey(logoutDeviceKeyName)) {
			redisTemplate.delete(logoutDeviceKeyName);
			return true;
		}
		return false;
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
			return true;
		} catch (SignatureException e) {
			log.error("JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.", e);
		} catch (SecurityException | MalformedJwtException e) {
			log.error("Invalid JWT Token", e);
		} catch (ExpiredJwtException e) {
			log.error("Expired JWT Token", e);
		} catch (UnsupportedJwtException e) {
			log.error("Unsupported JWT Token", e);
		} catch (IllegalArgumentException e) {
			log.error("JWT claims string is empty.", e);
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
		return String.join(":", names);
	}

	private void clearGraceRotatedTokenCacheForDevice(Long userId, String deviceInfo) {
		String pattern = generateKeyName(
			RedisKey.REFRESH_TOKEN_ROTATE_GRACE,
			deviceInfo,
			String.valueOf(userId),
			KEY_WILDCARD
		);
		var graceKeys = redisTemplate.keys(pattern);
		if (graceKeys != null && !graceKeys.isEmpty()) {
			redisTemplate.delete(graceKeys);
		}
	}

	private TokenResponseDto getGraceRotatedToken(Long userId, String deviceInfo, String oldRefreshToken) {
		String keyName = generateGraceKeyName(userId, deviceInfo, oldRefreshToken);
		Object cached = redisTemplate.opsForValue().get(keyName);
		if (!(cached instanceof String cachedValue) || !cachedValue.contains(TOKEN_PAIR_DELIMITER)) {
			return null;
		}
		String[] parts = cachedValue.split("\\|", 2);
		if (parts.length != 2 || !StringUtils.hasText(parts[0]) || !StringUtils.hasText(parts[1])) {
			return null;
		}
		return TokenResponseDto.of(parts[0], parts[1]);
	}

	private String generateGraceKeyName(Long userId, String deviceInfo, String refreshToken) {
		return generateKeyName(
			RedisKey.REFRESH_TOKEN_ROTATE_GRACE,
			deviceInfo,
			String.valueOf(userId),
			sha256Hex(refreshToken)
		);
	}

	private String sha256Hex(String value) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
			StringBuilder builder = new StringBuilder(hash.length * 2);
			for (byte b : hash) {
				builder.append(String.format(Locale.ROOT, "%02x", b));
			}
			return builder.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("SHA-256 algorithm is not available", e);
		}
	}

	private String createAccessToken(Long userId, long now) {
		Date accessTokenExpiresIn = new Date(now + accessTokenExpireTime);
		return Jwts.builder()
			.claim(JwtKeys.USER_ID, userId)
			.setExpiration(accessTokenExpiresIn)
			.signWith(secretKey, SignatureAlgorithm.HS512)
			.compact();
	}

	private String createRefreshToken(long now) {
		Date refreshTokenExpiresIn = new Date(now + refreshTokenExpireTime);
		return Jwts.builder()
			.setId(UUID.randomUUID().toString())
			.setIssuedAt(new Date(now))
			.setExpiration(refreshTokenExpiresIn)
			.signWith(secretKey, SignatureAlgorithm.HS512)
			.compact();
	}

	private void saveRefreshTokenToRedis(Long userId, String deviceInfo, String refreshToken, Long now) {
		String refreshTokenKeyName = generateKeyName(RedisKey.REFRESH_TOKEN, deviceInfo, String.valueOf(userId));
		String zsetKeyName = generateKeyName(RedisKey.USER_DEVICE, String.valueOf(userId));

		// legacy 토큰이 존재한다면 삭제
		cleanupLegacyRefreshTokenIfExist(userId);

		// 신규 토큰 저장
		saveNewRefreshToken(refreshTokenKeyName, refreshToken, zsetKeyName, now);

		// 로그인 기기 초과 시 정리
		removeOldestDeviceIfExceedLimit(zsetKeyName, userId);
	}

	private void cleanupLegacyRefreshTokenIfExist(Long userId) {
		String legacyRefreshTokenKeyName = String.valueOf(userId);
		if (redisTemplate.hasKey(legacyRefreshTokenKeyName)) {
			expireRefreshToken(legacyRefreshTokenKeyName);
			log.warn("@@ Deleted legacy refresh token key: {}", legacyRefreshTokenKeyName);
		}
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

				String oldKeyDeviceInfo = oldRefreshTokenKeyName.split(":")[1];
				clearGraceRotatedTokenCacheForDevice(userId, oldKeyDeviceInfo);
				redisTemplate.opsForValue()
					.set(generateKeyName(RedisKey.LOGOUT_FLAG, oldKeyDeviceInfo, String.valueOf(userId)), String.valueOf(true));
				log.warn("@@ Exceeded device limit. Deleted oldest refresh token: {}", oldRefreshTokenKeyName);
			}
		}
	}
}
