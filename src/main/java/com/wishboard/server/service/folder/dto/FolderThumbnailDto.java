package com.wishboard.server.service.folder.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FolderThumbnailDto {
	private Long id;
	private String folderThumbnail;
	private LocalDateTime createdAt;
}
