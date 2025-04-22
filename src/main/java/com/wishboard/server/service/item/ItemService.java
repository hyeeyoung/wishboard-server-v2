package com.wishboard.server.service.item;

import static com.wishboard.server.common.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.wishboard.server.common.exception.NotFoundException;
import com.wishboard.server.common.type.FileType;
import com.wishboard.server.controller.item.request.CreateItemRequest;
import com.wishboard.server.controller.item.request.UpdateItemRequest;
import com.wishboard.server.domain.folder.repository.FolderRepository;
import com.wishboard.server.domain.item.AddType;
import com.wishboard.server.domain.item.Item;
import com.wishboard.server.domain.item.ItemImage;
import com.wishboard.server.domain.item.repository.ItemRepository;
import com.wishboard.server.domain.notifications.NotificationId;
import com.wishboard.server.domain.notifications.Notifications;
import com.wishboard.server.domain.notifications.repository.NotificationsRepository;
import com.wishboard.server.domain.user.repository.UserRepository;
import com.wishboard.server.service.folder.FolderServiceUtils;
import com.wishboard.server.service.image.provider.S3Provider;
import com.wishboard.server.service.image.provider.dto.request.ImageUploadFileRequest;
import com.wishboard.server.service.item.dto.ItemFolderNotificationDto;
import com.wishboard.server.service.user.UserServiceUtils;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class ItemService {
	private final NotificationsRepository notificationsRepository;
	private final FolderRepository folderRepository;
	private final ItemRepository itemRepository;
	private final UserRepository userRepository;

	private final S3Provider s3Provider;
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public List<ItemFolderNotificationDto> getAllItemInfo(Long userId) {
		var user = UserServiceUtils.findUserById(userRepository, userId);
		return itemRepository.findAllByUserId(user.getId());
	}

	public ItemFolderNotificationDto getItemInfo(Long userId, Long itemId) {
		var user = UserServiceUtils.findUserById(userRepository, userId);
		var item = ItemServiceUtils.findItemById(itemRepository, itemId, userId);
		var notificationsByItem = notificationsRepository.findByNotificationId(new NotificationId(user, item)).orElse(null);
		return ItemFolderNotificationDto.of(item, notificationsByItem);
	}

	public ItemFolderNotificationDto createItem(Long userId, CreateItemRequest request, List<MultipartFile> images, AddType addType) {
		var user = UserServiceUtils.findUserById(userRepository, userId);
		var item = itemRepository.save(
			Item.newInstance(user, request.getItemName(), String.valueOf(request.getItemPrice()), request.getItemUrl(), request.getItemMemo(),
				addType));

		// 이미지 추가
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
		// 폴더 추가
		if (request.getFolderId() != null) {
			var folder = FolderServiceUtils.findFolderByIdAndUserId(folderRepository, request.getFolderId(), user);
			item.updateFolder(folder);
		}
		// 알림 추가
		Notifications notifications = null;
		if (request.getItemNotificationType() != null && request.getItemNotificationDate() != null) {
			request.validateDateInFuture();
			// 알림 생성
			notifications = notificationsRepository.save(
				Notifications.newInstance(
					new NotificationId(item.getUser(), item),
					request.getItemNotificationType(),
					LocalDateTime.parse(request.getItemNotificationDate(), formatter))
			);
		}

		return ItemFolderNotificationDto.of(item, notifications);
	}

	public ItemFolderNotificationDto updateItem(Long userId, Long itemId, UpdateItemRequest request, List<MultipartFile> images) {
		var user = UserServiceUtils.findUserById(userRepository, userId);
		var item = ItemServiceUtils.findItemById(itemRepository, itemId, userId);

		// 폴더 변경
		if (request.getFolderId() != null) {
			var folder = FolderServiceUtils.findFolderByIdAndUserId(folderRepository, request.getFolderId(), user);
			item.updateFolder(folder);
		}

		// 이미지 변경
		if (!item.getImages().isEmpty()) {
			item.getImages().forEach(image -> {
				if (StringUtils.isNotBlank(image.getItemImageUrl())) {
					s3Provider.deleteFile(image.getItemImageUrl());
				}
			});
			item.getImages().clear();
		}
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
		item.updateItemInfo(request.getItemName(), String.valueOf(request.getItemPrice()), request.getItemUrl(), request.getItemMemo());

		// 알림 수정
		var notificationsByItem = notificationsRepository.findByNotificationId(new NotificationId(item.getUser(), item))
			.orElseThrow(
				() -> new NotFoundException(String.format("알림이 존재하지 않습니다. (itemId: %s, userId: %s)", item.getId(), item.getUser().getId()),
					NOT_FOUND_NOTIFICATION_EXCEPTION));
		notificationsByItem.updateState(
			request.getItemNotificationType(),
			LocalDateTime.parse(request.getItemNotificationDate(), formatter)
		);
		return ItemFolderNotificationDto.of(item, notificationsByItem);
	}

	public void deleteItem(Long userId, Long itemId) {
		var user = UserServiceUtils.findUserById(userRepository, userId);
		var item = ItemServiceUtils.findItemById(itemRepository, itemId, userId);

		// 이미지 삭제
		item.getImages().forEach(image -> {
			if (StringUtils.isNotBlank(image.getItemImageUrl())) {
				s3Provider.deleteFile(image.getItemImageUrl());
			}
		});
		item.getImages().clear();

		// 알림 삭제
		var notificationsByItem = notificationsRepository.findByNotificationId(new NotificationId(user, item));
		notificationsByItem.ifPresent(notificationsRepository::delete);

		itemRepository.delete(item);
	}

	public ItemFolderNotificationDto updateItemFolder(Long userId, Long itemId, Long folderId) {
		var user = UserServiceUtils.findUserById(userRepository, userId);
		var item = ItemServiceUtils.findItemById(itemRepository, itemId, userId);
		var folder = FolderServiceUtils.findFolderByIdAndUserId(folderRepository, folderId, user);
		var notificationsByItem = notificationsRepository.findByNotificationId(new NotificationId(user, item)).orElse(null);
		item.updateFolder(folder);
		return ItemFolderNotificationDto.of(item, notificationsByItem);
	}
}
