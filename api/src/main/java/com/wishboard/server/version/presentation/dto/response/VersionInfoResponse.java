package com.wishboard.server.version.presentation.dto.response;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VersionInfoResponse {
	private Long id;
	private String platform;
	private String minVersion;
	private String recommendedVersion;
	private LocalDateTime releaseDate;
}
