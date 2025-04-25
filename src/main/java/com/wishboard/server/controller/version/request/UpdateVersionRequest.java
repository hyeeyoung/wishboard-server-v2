package com.wishboard.server.controller.version.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateVersionRequest {
	@Schema(description = "minVersion", example = "1.0.0")
	@NotBlank(message = "{version.minVersion.notBlank")
	private String minVersion;
	
	@Schema(description = "recommendedVersion", example = "1.0.0")
	@NotBlank(message = "{version.recommendedVersion.notBlank")
	private String recommendedVersion;
}
