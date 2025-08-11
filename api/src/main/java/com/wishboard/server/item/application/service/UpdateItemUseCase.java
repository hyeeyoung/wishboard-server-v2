package com.wishboard.server.item.application.service;

import static com.wishboard.server.common.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.management.Notification;

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
		if (updateItemCommand.folderId() != null) {
			var folder = folderReader.findByIdAndUser(updateItemCommand.folderId(), user);
			item.updateFolder(folder);
		} else {
			item.updateFolder(null);
		}

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

		var maybeNotification = notificationsRepository.findByNotificationId(new NotificationId(item.getUser(), item));

		var reqType = updateItemCommand.itemNotificationType();
		var reqDateStr = updateItemCommand.itemNotificationDate();

		Notifications notifications = null;

		// 1) 생성/수정
		if (reqType != null && reqDateStr != null) {
			if (maybeNotification.isEmpty()) {
				notifications = createNotification(item, reqType, reqDateStr);
				notifications = notificationsRepository.save(notifications);
			} else {
				updateNotification(maybeNotification.get(), reqType, reqDateStr);
			}
			// 2) 삭제
		} else if (reqType == null && reqDateStr == null) {
			if (maybeNotification.isPresent()) {
				deleteNotification(maybeNotification);
			}
		} else {
			// 1개라도 null 인 경우 기존 값 그대로 유지
			notifications = maybeNotification.orElse(null);
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
