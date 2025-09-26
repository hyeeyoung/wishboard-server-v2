package com.wishboard.server.auth.application.service.support;

import org.springframework.stereotype.Component;

import com.wishboard.server.user.domain.model.TemporaryNickname;

import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
public class NicknameGenerator {
	public String generate() {
		return new TemporaryNickname().getNickname();
	}
}
