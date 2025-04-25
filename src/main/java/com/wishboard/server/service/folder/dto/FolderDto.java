package com.wishboard.server.service.folder.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolderDto {
	private Long id;
	private String folderName;
	private String folderThumbnail;
	private Long itemCount;
}
