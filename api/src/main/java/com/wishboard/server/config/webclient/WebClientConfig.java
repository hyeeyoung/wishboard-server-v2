package com.wishboard.server.config.webclient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

	@Bean
	public WebClient webClient() {

		// HttpClient 설정
		HttpClient httpClient = HttpClient.create()
			.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 15000)
			.doOnConnected(conn -> conn
				.addHandlerLast(new ReadTimeoutHandler(15, TimeUnit.MILLISECONDS))
				.addHandlerLast(new WriteTimeoutHandler(15, TimeUnit.MILLISECONDS))
			);
		ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient.wiretap(true));
		return WebClient.builder()
			.clientConnector(connector) // 커넥터 설정
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.build();
	}
}
