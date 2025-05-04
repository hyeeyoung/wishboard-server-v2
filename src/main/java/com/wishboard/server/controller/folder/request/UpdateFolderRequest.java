package com.wishboard.server.controller.folder.request;

import com.wishboard.server.service.folder.dto.command.UpdateFolderCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UpdateFolderRequest(
	@Schema(description = "folderName", example = "상의")
	@NotBlank(message = "{folder.folderName.notBlank}")
	String folderName
) {
	public UpdateFolderCommand toCommand() {
		return new UpdateFolderCommand(this.folderName);
	}
}
