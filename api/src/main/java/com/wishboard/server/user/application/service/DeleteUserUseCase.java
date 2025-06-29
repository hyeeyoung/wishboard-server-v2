package com.wishboard.server.user.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.wishboard.server.item.domain.repository.ItemRepository;
import com.wishboard.server.folder.domain.repository.FolderRepository;
import com.wishboard.server.notifications.domain.repository.NotificationsRepository;
import com.wishboard.server.image.application.dto.service.S3Provider;
import com.wishboard.server.user.application.service.support.UserReader;
import com.wishboard.server.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class DeleteUserUseCase {

	private final UserReader userReader;
	private final S3Provider s3Provider;

	private final UserRepository userRepository;
	private final NotificationsRepository notificationsRepository;
	private final ItemRepository itemRepository;
	private final FolderRepository folderRepository;

	public void execute(Long userId) {
		var user = userReader.findById(userId);
		// 프로필 이미지 삭제
		if (user.getProfileImgUrl() != null) {
			s3Provider.deleteFile(user.getProfileImgUrl());
		}
		user.getFcmTokens().clear();

		// 알림 삭제
		notificationsRepository.deleteAllByUserId(user.getId());

		// 아이템 삭제
		var items = itemRepository.findAllByUser(user);
		if (!items.isEmpty()) {
			items.forEach(item -> {
				var itemImages = item.getImages();
				if (!itemImages.isEmpty()) {
					itemImages.forEach(image -> {
						if (StringUtils.hasText(image.getItemImageUrl())) {
							s3Provider.deleteFile(image.getItemImageUrl());
						}
					});
				}
				item.getImages().clear();
			});
		}
		itemRepository.deleteAllByUser(user);

		// 폴더 삭제
		folderRepository.deleteAllByUser(user);

		// 유저 삭제
		userRepository.delete(user);
	}
}
