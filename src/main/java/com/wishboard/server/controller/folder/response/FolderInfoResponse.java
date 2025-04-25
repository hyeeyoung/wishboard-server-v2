package com.wishboard.server.controller.folder.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FolderInfoResponse {
	private Long id;
	private String folderName;
	private String folderThumbnail;
	private int itemCount;
}
