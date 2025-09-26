package com.wishboard.server.user.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
	private Long id;
	private String email;
	private String nickname;
	private String profileImgUrl;
	private Boolean pushState;
}
