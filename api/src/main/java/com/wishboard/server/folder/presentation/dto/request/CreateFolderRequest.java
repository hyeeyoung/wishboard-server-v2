package com.wishboard.server.folder.presentation.dto.request;

import com.wishboard.server.folder.application.dto.command.CreateFolderCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateFolderRequest(
	@Schema(description = "folderName", example = "상의")
	@NotBlank(message = "{folder.folderName.notBlank}")
	@Size(max = 10, message = "{folder.folderName.maxLength")
	String folderName
) {
	public CreateFolderCommand toCommand() {
		return new CreateFolderCommand(this.folderName);
	}
}
