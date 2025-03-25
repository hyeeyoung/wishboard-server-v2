package com.wishboard.server.external.client.apple;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.wishboard.server.external.client.apple.dto.response.ApplePublicKeyResponse;

import reactor.core.publisher.Mono;

@Component
public class AppleApiClient {

    private final WebClient webClient;

    public AppleApiClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://appleid.apple.com/auth").build();
    }

    public Mono<ApplePublicKeyResponse> getAppleAuthPublicKey() {
        return webClient.get()
            .uri("/keys")
            .retrieve()
            .bodyToMono(ApplePublicKeyResponse.class);
    }
}
