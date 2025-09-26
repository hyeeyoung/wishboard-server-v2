package com.wishboard.server.item.application.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.wishboard.server.common.type.FileType;
import com.wishboard.server.folder.application.service.support.FolderReader;
import com.wishboard.server.image.application.dto.request.ImageUploadFileRequest;
import com.wishboard.server.image.application.service.S3Provider;
import com.wishboard.server.item.application.dto.ItemFolderNotificationDto;
import com.wishboard.server.item.application.dto.command.CreateItemCommand;
import com.wishboard.server.item.application.service.support.ItemValidator;
import com.wishboard.server.item.domain.model.AddType;
import com.wishboard.server.item.domain.model.Item;
import com.wishboard.server.item.domain.model.ItemImage;
import com.wishboard.server.item.domain.repository.ItemRepository;
import com.wishboard.server.notifications.domain.model.NotificationId;
import com.wishboard.server.notifications.domain.model.Notifications;
import com.wishboard.server.notifications.domain.repository.NotificationsRepository;
import com.wishboard.server.user.application.service.support.UserReader;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class CreateItemUseCase {
	private final UserReader userReader;
	private final FolderReader folderReader;
	private final ItemValidator itemValidator;

	private final S3Provider s3Provider;
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private final ItemRepository itemRepository;
	private final NotificationsRepository notificationsRepository;

	public ItemFolderNotificationDto execute(Long userId, CreateItemCommand createItemCommand, List<MultipartFile> images, AddType addType) {
		var user = userReader.findById(userId);
		var item = itemRepository.save(
			Item.newInstance(user, createItemCommand.itemName(), String.valueOf(createItemCommand.itemPrice()), createItemCommand.itemUrl(),
				createItemCommand.itemMemo(), addType));

		// 이미지 추가
		if (images != null) {
			List<ItemImage> imageUrls = images.stream()
				.filter(image -> image != null && !image.isEmpty())
				.map(image -> new ItemImage(
					image.getOriginalFilename(),
					s3Provider.uploadPermanentFile(ImageUploadFileRequest.of(FileType.ITEM_IMAGE), image),
					item))
				.toList();
			item.addItemImage(imageUrls);
		}

		// 폴더 추가
		if (createItemCommand.folderId() != null) {
			var folder = folderReader.findByIdAndUser(createItemCommand.folderId(), user);
			item.updateFolder(folder);
		}

		// 알림 추가
		Notifications notifications = null;
		if (createItemCommand.itemNotificationType() != null && createItemCommand.itemNotificationDate() != null) {
			itemValidator.validateDateInFuture(createItemCommand.itemNotificationType(), createItemCommand.itemNotificationDate());
			// 알림 생성
			notifications = notificationsRepository.save(
				Notifications.newInstance(
					new NotificationId(item.getUser(), item),
					createItemCommand.itemNotificationType(),
					LocalDateTime.parse(createItemCommand.itemNotificationDate(), formatter))
			);
		}

		return ItemFolderNotificationDto.of(item, notifications);
	}
}
