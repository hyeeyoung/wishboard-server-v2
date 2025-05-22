package com.wishboard.server.item.application.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.wishboard.server.common.type.FileType;
import com.wishboard.server.folder.application.service.support.FolderReader;
import com.wishboard.server.image.application.dto.request.ImageUploadFileRequest;
// import com.wishboard.server.image.application.dto.service.S3Provider; // Remove this
import com.wishboard.server.common.application.port.out.FileStorageService; // Add this
import com.wishboard.server.item.application.dto.ItemFolderNotificationDto;
import com.wishboard.server.item.application.dto.command.CreateItemCommand;
import com.wishboard.server.item.application.service.support.ItemValidator;
import com.wishboard.server.item.domain.model.AddType;
import com.wishboard.server.item.domain.model.Item;
import com.wishboard.server.item.domain.model.ItemImage;
import com.wishboard.server.item.domain.repository.ItemRepository;
// import com.wishboard.server.notifications.domain.model.Notifications; // Removed
// import com.wishboard.server.notifications.domain.repository.NotificationsRepository; // Removed
import com.wishboard.server.user.application.service.support.UserReader;
import com.wishboard.server.item.domain.event.ItemCreatedEvent; // Added
import org.springframework.context.ApplicationEventPublisher; // Added

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class CreateItemUseCase {
	private final UserReader userReader;
	private final FolderReader folderReader;
	private final ItemValidator itemValidator;
	// private final S3Provider s3Provider; // Remove this
	private final FileStorageService fileStorageService; // Add this
	private final ItemRepository itemRepository;
	// private final NotificationsRepository notificationsRepository; // Removed
	private final ApplicationEventPublisher eventPublisher; // Added

	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public ItemFolderNotificationDto execute(Long userId, CreateItemCommand createItemCommand, List<MultipartFile> images, AddType addType) {
		var user = userReader.findById(userId);
		var item = itemRepository.save(
			Item.newInstance(user, createItemCommand.itemName(), String.valueOf(createItemCommand.itemPrice()), createItemCommand.itemUrl(),
				createItemCommand.itemMemo(), addType));

		// 이미지 추가
		if (images != null && !images.isEmpty()) {
			List<ItemImage> imageUrls = images.stream()
				.map(image -> {
					if (image != null && !image.isEmpty()) {
						return new ItemImage(image.getOriginalFilename(),
							fileStorageService.uploadFile(ImageUploadFileRequest.of(FileType.ITEM_IMAGE), image), item); // Changed s3Provider to fileStorageService
					}
					return null;
				})
				.collect(Collectors.toList());
			item.addItemImage(imageUrls);
		}
		// 폴더 추가
		if (createItemCommand.folderId() != null) {
			var folder = folderReader.findByIdAndUserId(createItemCommand.folderId(), user.getId()); // Changed to use userId
			item.updateFolderId(folder.getId()); // Update with the ID
		}
		// 알림 추가 -> 이벤트 발행으로 변경
		// Notifications notifications = null; // Removed
		if (createItemCommand.itemNotificationType() != null && createItemCommand.itemNotificationDate() != null) {
			itemValidator.validateDateInFuture(createItemCommand.itemNotificationType(), createItemCommand.itemNotificationDate());

			String mainImageUrl = null;
			if (item.getImages() != null && !item.getImages().isEmpty()) {
				// Assuming ItemImage has getItemImageUrl() or similar method
				mainImageUrl = item.getImages().get(0).getItemImageUrl();
			}

			ItemCreatedEvent event = new ItemCreatedEvent(
				item.getId(),
				user.getId(), // or item.getUser().getId()
				item.getItemName(),
				createItemCommand.itemNotificationType(),
				LocalDateTime.parse(createItemCommand.itemNotificationDate(), formatter),
				item.getItemUrl(),
				mainImageUrl
			);
			eventPublisher.publishEvent(event);
		}

        // Pass null for the Notifications object, or adjust DTO to take individual fields from createItemCommand
        // Updated call to ItemFolderNotificationDto.of with parameters from createItemCommand
        if (createItemCommand.itemNotificationType() != null && createItemCommand.itemNotificationDate() != null) {
            return ItemFolderNotificationDto.of(
                item,
                createItemCommand.itemNotificationType(),
                LocalDateTime.parse(createItemCommand.itemNotificationDate(), formatter)
            );
        } else {
            return ItemFolderNotificationDto.of(item, null, null);
        }
	}
}
