package com.wishboard.server.controller.folder.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FolderInfoWithoutItemCountResponse {
	private Long id;
	private String folderName;
	private String folderThumbnail;
}
