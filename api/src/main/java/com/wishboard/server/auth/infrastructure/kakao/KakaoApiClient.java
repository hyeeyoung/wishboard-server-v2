package com.wishboard.server.auth.infrastructure.kakao;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.wishboard.server.auth.infrastructure.kakao.dto.response.KakaoProfileResponse;

@Component
@RequiredArgsConstructor
public class KakaoApiClient {

	private final WebClient webClient;

	public Mono<KakaoProfileResponse> getProfileInfo(String accessToken) {
		return webClient.get()
			.uri("https://kapi.kakao.com/v2/user/me")
			.header("Authorization", "Bearer " + accessToken)
			.retrieve()
			.bodyToMono(KakaoProfileResponse.class);
	}
}
