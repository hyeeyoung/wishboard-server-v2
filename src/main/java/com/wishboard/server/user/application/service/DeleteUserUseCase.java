package com.wishboard.server.user.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.wishboard.server.item.domain.repository.ItemRepository;
import com.wishboard.server.folder.domain.repository.FolderRepository;
import com.wishboard.server.notifications.domain.repository.NotificationsRepository;
// import com.wishboard.server.image.application.dto.service.S3Provider; // Remove this
import com.wishboard.server.common.application.port.out.FileStorageService; // Add this
import com.wishboard.server.user.application.service.support.UserReader;
import com.wishboard.server.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class DeleteUserUseCase {

	private final UserReader userReader;
	// private final S3Provider s3Provider; // Remove this
	private final FileStorageService fileStorageService; // Add this

	private final UserRepository userRepository;
	private final NotificationsRepository notificationsRepository;
	private final ItemRepository itemRepository;
	private final FolderRepository folderRepository;

	public void execute(Long userId) {
		var user = userReader.findById(userId);
		// 프로필 이미지 삭제
		if (user.getProfileImgUrl() != null) {
			fileStorageService.deleteFile(user.getProfileImgUrl()); // Changed s3Provider to fileStorageService
		}
		user.getFcmTokens().clear();

		// 알림 삭제
		notificationsRepository.deleteAllByUserId(user.getId());

		// 아이템 삭제
		var items = itemRepository.findAllByUserId(user.getId()); // Changed to use userId
		if (!items.isEmpty()) {
			items.forEach(item -> {
				var itemImages = item.getImages();
				if (!itemImages.isEmpty()) {
					itemImages.forEach(image -> {
						if (StringUtils.hasText(image.getItemImageUrl())) {
							fileStorageService.deleteFile(image.getItemImageUrl()); // Changed s3Provider to fileStorageService
						}
					});
				}
				item.getImages().clear();
			});
		}
		itemRepository.deleteAllByUserId(user.getId()); // Changed to use userId

		// 폴더 삭제
		folderRepository.deleteAllByUserId(user.getId()); // Changed to use userId

		// 유저 삭제
		userRepository.delete(user);
	}
}
