package com.wishboard.server.item.application.service;

import static com.wishboard.server.common.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.wishboard.server.common.exception.NotFoundException;
import com.wishboard.server.common.type.FileType;
import com.wishboard.server.folder.application.service.support.FolderReader;
import com.wishboard.server.image.application.dto.request.ImageUploadFileRequest;
import com.wishboard.server.image.application.dto.service.S3Provider;
import com.wishboard.server.item.application.dto.ItemFolderNotificationDto;
import com.wishboard.server.item.application.dto.command.UpdateItemCommand;
import com.wishboard.server.item.application.service.support.ItemReader;
import com.wishboard.server.item.domain.model.ItemImage;
import com.wishboard.server.notifications.domain.model.NotificationId;
import com.wishboard.server.notifications.domain.repository.NotificationsRepository;
import com.wishboard.server.user.application.service.support.UserReader;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class UpdateItemUseCase {
	private final UserReader userReader;
	private final FolderReader folderReader;
	private final ItemReader itemReader;

	private final S3Provider s3Provider;
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private final NotificationsRepository notificationsRepository;

	public ItemFolderNotificationDto execute(Long userId, Long itemId, UpdateItemCommand updateItemCommand, List<MultipartFile> images) {
		var user = userReader.findById(userId);
		var item = itemReader.findById(itemId, user.getId());

		// 폴더 변경
		if (updateItemCommand.itemName() != null) {
			var folder = folderReader.findByIdAndUser(updateItemCommand.folderId(), user);
			item.updateFolder(folder);
		}

		// 이미지 변경
		if (images != null && !images.isEmpty()) {
			List<ItemImage> imageUrls = images.stream()
				.map(image -> {
					if (image != null && !image.isEmpty()) {
						return new ItemImage(image.getOriginalFilename(),
							s3Provider.uploadFile(ImageUploadFileRequest.of(FileType.ITEM_IMAGE), image), item);
					}
					return null;
				})
				.collect(Collectors.toList());
			item.addItemImage(imageUrls);
		}
		if (!item.getImages().isEmpty()) {
			item.getImages().forEach(image -> {
				if (StringUtils.hasText(image.getItemImageUrl())) {
					s3Provider.deleteFile(image.getItemImageUrl());
				}
			});
			item.getImages().clear();
		}
		item.updateItemInfo(updateItemCommand.itemName(), String.valueOf(updateItemCommand.itemPrice()), updateItemCommand.itemUrl(),
			updateItemCommand.itemMemo());

		// 알림 수정
		var notificationsByItem = notificationsRepository.findByNotificationId(new NotificationId(item.getUser(), item))
			.orElseThrow(
				() -> new NotFoundException(String.format("알림이 존재하지 않습니다. (itemId: %s, userId: %s)", item.getId(), item.getUser().getId()),
					NOT_FOUND_NOTIFICATION_EXCEPTION));

		if (updateItemCommand.itemNotificationType() != null && updateItemCommand.itemNotificationDate() != null) {
			notificationsByItem.updateState(
				updateItemCommand.itemNotificationType(),
				LocalDateTime.parse(updateItemCommand.itemNotificationDate(), formatter)
			);
		}
		return ItemFolderNotificationDto.of(item, notificationsByItem);
	}
}
