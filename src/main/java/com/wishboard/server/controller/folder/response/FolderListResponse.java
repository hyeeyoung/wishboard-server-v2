package com.wishboard.server.controller.folder.response;

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
