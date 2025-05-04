package com.wishboard.server.user.presentation.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoResponse {
	private Long id;
	private String email;
	private String nickname;
	private String profileImgUrl;
	private Boolean pushState;
}
