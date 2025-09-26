package com.wishboard.server.folder.presentation.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FolderListResponse {
	private Long id;
	private String folderName;
	private String folderThumbnail;
	private Long itemCount;
}
