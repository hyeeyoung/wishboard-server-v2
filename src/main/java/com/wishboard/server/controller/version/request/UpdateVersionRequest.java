package com.wishboard.server.controller.version.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UpdateVersionRequest(
	@Schema(description = "minVersion", example = "1.0.0")
	@NotBlank(message = "{version.minVersion.notBlank")
	String minVersion,

	@Schema(description = "recommendedVersion", example = "1.0.0")
	@NotBlank(message = "{version.recommendedVersion.notBlank")
	String recommendedVersion
) {
}
