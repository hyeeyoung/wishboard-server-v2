package com.wishboard.server.folder.presentation.dto.request;

import com.wishboard.server.folder.application.dto.command.UpdateFolderCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;

public record UpdateFolderRequest(
	@Schema(description = "folderName", example = "상의")
	@NotBlank(message = "{folder.folderName.notBlank}")
	@Max(value = 10, message = "{folder.folderName.maxLength")
	String folderName
) {
	public UpdateFolderCommand toCommand() {
		return new UpdateFolderCommand(this.folderName);
	}
}
