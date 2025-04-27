package com.wishboard.server.common.util;

import static com.wishboard.server.common.exception.ErrorCode.*;

import java.util.List;

import org.springframework.util.StringUtils;

import com.wishboard.server.common.exception.ValidationException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class HttpHeaderUtils {

	private static final String BEARER_TOKEN = "Bearer ";
	private static final List<String> IP_HEADERS = List.of(
		"X-Forwarded-For",          // 클라이언트의 원 IP 주소를 나타내기 위한 일반적인 헤더, 여러 IP 주소가 쉼표로 구분되어 있을 수 있음
		"HTTP_FORWARDED",           // RFC 7239에 정의된 표준화된 포워드 헤더, 클라이언트 및 프록시 서버 정보를 포함
		"Proxy-Client-IP",          // 일부 프록시 서버에서 사용하는 헤더, 클라이언트 IP 주소를 포함
		"WL-Proxy-Client-IP",       // WebLogic 서버에서 사용하는 헤더, 클라이언트 IP 주소를 포함
		"HTTP_CLIENT_IP",           // HTTP 요청의 클라이언트 IP를 나타내는 헤더, 일부 프록시 서버에서 사용
		"HTTP_X_FORWARDED_FOR",     // 클라이언트의 원 IP 주소를 나타내는 또 다른 헤더, X-Forwarded-For와 유사
		"X-RealIP",                 // Nginx와 같은 일부 웹 서버에서 사용하는 헤더, 클라이언트의 원 IP 주소를 포함
		"X-Real-IP",                // Nginx와 같은 일부 웹 서버에서 사용하는 헤더, 클라이언트의 원 IP 주소를 포함 (대시 포함 버전)
		"REMOTE_ADDR"               // Java의 ServletRequest에서 제공하는 메서드로, 직접 연결된 클라이언트의 IP 주소를 반환
	);
	private static final String DEVICE_HEADER = "Device-Info";

	public static String getDeviceHeaderKeyName() {
		return DEVICE_HEADER;
	}

	public static String withBearerToken(String token) {
		return BEARER_TOKEN.concat(token);
	}

	public static String getIpFromHeader(HttpServletRequest request) {
		for (String ipHeader : IP_HEADERS) {
			String clientIp = request.getHeader(ipHeader);
			if (StringUtils.hasText(clientIp) && !"unknown".equalsIgnoreCase(clientIp)) {
				log.info("헤더 {}: {}", ipHeader, clientIp);
				return clientIp;
			}
		}
		// 헤더에 IP 정보가 없을 경우, ServletRequest에서 직접 가져온 IP 주소를 사용
		return request.getRemoteAddr();
	}

	public static String getDeviceInfoFromHeader(HttpServletRequest request) {
		String deviceInfo = request.getHeader(DEVICE_HEADER);
		if (!StringUtils.hasText(deviceInfo)) {
			throw new ValidationException("Device-Info 헤더가 없습니다.", VALIDATION_DEVICE_INFO_EXCEPTION);
		}
		return deviceInfo;
	}
}
