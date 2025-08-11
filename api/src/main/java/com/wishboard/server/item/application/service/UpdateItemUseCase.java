package com.wishboard.server.item.application.service;

import static com.wishboard.server.common.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.wishboard.server.common.domain.ItemNotificationType;
import com.wishboard.server.common.exception.NotFoundException;
import com.wishboard.server.common.type.FileType;
import com.wishboard.server.folder.application.service.support.FolderReader;
import com.wishboard.server.image.application.dto.request.ImageUploadFileRequest;
import com.wishboard.server.image.application.service.service.S3Provider;
import com.wishboard.server.item.application.dto.ItemFolderNotificationDto;
import com.wishboard.server.item.application.dto.command.UpdateItemCommand;
import com.wishboard.server.item.application.service.support.ItemReader;
import com.wishboard.server.item.domain.model.Item;
import com.wishboard.server.item.domain.model.ItemImage;
import com.wishboard.server.notifications.domain.model.NotificationId;
import com.wishboard.server.notifications.domain.model.Notifications;
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
		var folder = folderReader.findByIdAndUser(updateItemCommand.folderId(), user);
		item.updateFolder(folder);

		// 이미지 변경
		if (!item.getImages().isEmpty()) {
			item.getImages().forEach(image -> {
				if (StringUtils.hasText(image.getItemImageUrl())) {
					s3Provider.deleteFile(image.getItemImageUrl());
				}
			});
			item.getImages().clear();
		}
		if (images != null) {
			List<ItemImage> imageUrls = images.stream()
				.filter(image -> image != null && !image.isEmpty())
				.map(image -> new ItemImage(image.getOriginalFilename(),
					s3Provider.uploadFile(ImageUploadFileRequest.of(FileType.ITEM_IMAGE), image), item))
				.toList();
			item.addItemImage(imageUrls);
		}
		item.updateItemInfo(updateItemCommand.itemName(), String.valueOf(updateItemCommand.itemPrice()), updateItemCommand.itemUrl(),
			updateItemCommand.itemMemo());

		Notifications notifications = null;
		var notificationsByItem = notificationsRepository.findByNotificationId(new NotificationId(item.getUser(), item));
		if (updateItemCommand.itemNotificationType() != null && updateItemCommand.itemNotificationDate() != null) {
			// 알림 생성
			if (notificationsByItem.isEmpty()) {
				notifications = createNotification(item, updateItemCommand.itemNotificationType(),updateItemCommand.itemNotificationDate());
				notificationsRepository.save(notifications);
			}
			// 알림 수정
			else {
				notifications = notificationsByItem.get();
				updateNotification(notifications, updateItemCommand.itemNotificationType(), updateItemCommand.itemNotificationDate());
			}
		}
		// 알림 삭제
		else if (updateItemCommand.itemNotificationType() == null && updateItemCommand.itemNotificationDate() == null) {
			deleteNotification(notificationsByItem);
		}
		return ItemFolderNotificationDto.of(item, notifications);
	}

	private Notifications createNotification(Item item, ItemNotificationType itemNotificationType, String itemNotificationDate) {
			return Notifications.newInstance(
				new NotificationId(item.getUser(), item), itemNotificationType, LocalDateTime.parse(itemNotificationDate, formatter)
			);
	}

	private void updateNotification(Notifications notifications, ItemNotificationType itemNotificationType, String itemNotificationDate) {
		notifications.updateState(itemNotificationType, LocalDateTime.parse(itemNotificationDate, formatter));
	}

	private void deleteNotification(Optional<Notifications> notifications) {
		notifications.ifPresent(notificationsRepository::delete);
	}
}
