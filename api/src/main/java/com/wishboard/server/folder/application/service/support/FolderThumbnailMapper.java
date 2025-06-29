package com.wishboard.server.folder.application.service.support;

import org.springframework.stereotype.Component;

import com.wishboard.server.folder.application.dto.FolderDto;
import com.wishboard.server.folder.domain.model.Folder;
import com.wishboard.server.item.application.dto.FolderItemDto;

import jakarta.validation.constraints.NotNull;

@Component
public class FolderThumbnailMapper {
	public FolderDto toDto(Folder folder, @NotNull FolderItemDto folderItemDto) {
		if (folderItemDto.getItemCount() == 0L) {
			return FolderDto.builder()
				.id(folder.getId())
				.folderName(folder.getFolderName())
				.itemCount(0L)
				.build();
		}
		return FolderDto.builder()
			.id(folder.getId())
			.folderName(folder.getFolderName())
			.folderThumbnail(folderItemDto.getLastestItem().getImages().getFirst().getItemImageUrl())
			.itemCount(folderItemDto.getItemCount())
			.build();
	}
}
