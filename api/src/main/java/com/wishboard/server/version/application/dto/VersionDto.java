package com.wishboard.server.version.application.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VersionDto {
	private Long id;
	private String platform;
	private String minVersion;
	private String recommendedVersion;
	private LocalDateTime releaseDate;
}
