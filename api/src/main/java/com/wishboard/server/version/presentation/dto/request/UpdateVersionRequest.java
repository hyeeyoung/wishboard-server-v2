package com.wishboard.server.version.presentation.dto.request;

import com.wishboard.server.version.application.dto.command.UpdateVersionCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UpdateVersionRequest(
	@Schema(description = "minVersion", example = "1.0.0")
	@NotBlank(message = "{version.minVersion.notBlank}")
	String minVersion,

	@Schema(description = "recommendedVersion", example = "1.0.0")
	@NotBlank(message = "{version.recommendedVersion.notBlank}")
	String recommendedVersion
) {
	public UpdateVersionCommand toCommand() {
		return new UpdateVersionCommand(this.minVersion, this.recommendedVersion);
	}
}
