package com.wishboard.server.user.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@Embeddable
public class SocialInfo {

	@Column(length = 200)
	private String socialId;

	@Column(name = "provider_type", length = 45)
	@Enumerated(EnumType.STRING)
	private UserProviderType providerType;

	private SocialInfo(String socialId, UserProviderType providerType) {
		this.socialId = socialId;
		this.providerType = providerType;
	}

	public static SocialInfo of(String socialId, UserProviderType providerType) {
		return new SocialInfo(socialId, providerType);
	}
}
