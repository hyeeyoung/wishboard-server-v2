package com.wishboard.server.service.user.dto;

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
