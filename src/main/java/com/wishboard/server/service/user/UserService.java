package com.wishboard.server.service.user;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.wishboard.server.common.type.FileType;
import com.wishboard.server.controller.user.request.UpdatePasswordRequest;
import com.wishboard.server.controller.user.request.UpdateUserInfoRequest;
import com.wishboard.server.domain.folder.repository.FolderRepository;
import com.wishboard.server.domain.item.repository.ItemRepository;
import com.wishboard.server.domain.notifications.repository.NotificationsRepository;
import com.wishboard.server.domain.user.AuthType;
import com.wishboard.server.domain.user.User;
import com.wishboard.server.domain.user.repository.UserRepository;
import com.wishboard.server.service.auth.AuthServiceUtils;
import com.wishboard.server.service.image.provider.S3Provider;
import com.wishboard.server.service.image.provider.dto.request.ImageUploadFileRequest;
import com.wishboard.server.service.user.dto.CreateUserDto;
import com.wishboard.server.service.user.dto.UserDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService {

	private final UserRepository userRepository;

	private final S3Provider s3Provider;
	private final ModelMapper modelMapper;
	private final FolderRepository folderRepository;
	private final ItemRepository itemRepository;
	private final NotificationsRepository notificationsRepository;

	public Long registerSocialUser(CreateUserDto request) {
		UserServiceUtils.validateNotExistsUser(userRepository, request.getSocialId(), request.getSocialType());
		User user = userRepository.save(
			User.newInstance(request.getSocialId(), AuthType.SOCIAL, request.getSocialType()));
		return user.getId();
	}

	public UserDto getUserInfo(Long userId) {
		var user = UserServiceUtils.findUserById(userRepository, userId);
		return modelMapper.map(user, UserDto.class);
	}

	public UserDto updatePushState(Long userId, boolean pushState) {
		var user = UserServiceUtils.findUserById(userRepository, userId);
		user.updatePushState(pushState);
		return modelMapper.map(user, UserDto.class);
	}

	public UserDto updateUserInfo(Long userId, UpdateUserInfoRequest request, MultipartFile image) {
		var user = UserServiceUtils.findUserById(userRepository, userId);
		user.updateUserNickname(request.nickname());
		if (image != null && !image.isEmpty()) {
			String previousImageUrl = user.getProfileImgUrl();
			if (StringUtils.hasText(previousImageUrl)) {
				s3Provider.deleteFile(previousImageUrl);
			}

			String profileImageUrl = s3Provider.uploadFile(ImageUploadFileRequest.of(FileType.PROFILE_IMAGE), image);
			if (StringUtils.hasText(profileImageUrl)) {
				user.updateProfileImage(image.getOriginalFilename(), profileImageUrl);
			}
		}
		return modelMapper.map(user, UserDto.class);
	}

	public UserDto updatePassword(Long userId, @Valid UpdatePasswordRequest request) {
		var user = UserServiceUtils.findUserById(userRepository, userId);
		String hashedPassword = AuthServiceUtils.getHashedPassword(request.newPassword());
		user.updatePassword(hashedPassword);
		return modelMapper.map(user, UserDto.class);
	}

	public void deleteUser(Long userId) {
		var user = UserServiceUtils.findUserById(userRepository, userId);
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
