package com.wishboard.server.folder.application.service.support;

import org.springframework.stereotype.Component;

import com.wishboard.server.folder.application.dto.FolderDto;
import com.wishboard.server.folder.domain.model.Folder;
import com.wishboard.server.item.application.dto.FolderItemDto;
import com.wishboard.server.item.domain.model.ItemImage;

import jakarta.validation.constraints.NotNull;

@Component
public class FolderThumbnailMapper {
	public FolderDto toDto(Folder folder, @NotNull FolderItemDto folderItemDto) {
		var builder = FolderDto.builder()
			.id(folder.getId())
			.folderName(folder.getFolderName())
			.itemCount(folderItemDto.getItemCount());

		if (folderItemDto.getItemCount() > 0) {
			String thumbnail = folderItemDto.getLastestItem().getImages().stream()
				.findFirst()
				.map(ItemImage::getItemImageUrl)
				.orElse(null);
			builder.folderThumbnail(thumbnail);
		}

		return builder.build();
	}
}

