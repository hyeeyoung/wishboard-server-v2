package com.wishboard.server.controller.folder.request;

import com.wishboard.server.service.folder.dto.command.CreateFolderCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CreateFolderRequest(
	@Schema(description = "folderName", example = "상의")
	@NotBlank(message = "{folder.folderName.notBlank}")
	String folderName
) {
	public CreateFolderCommand toCommand() {
		return new CreateFolderCommand(this.folderName);
	}
}
