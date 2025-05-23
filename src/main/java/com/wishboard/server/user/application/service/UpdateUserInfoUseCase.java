package com.wishboard.server.user.application.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.wishboard.server.common.type.FileType;
import com.wishboard.server.image.application.dto.request.ImageUploadFileRequest;
import com.wishboard.server.image.application.dto.service.S3Provider;
import com.wishboard.server.user.application.dto.UserDto;
import com.wishboard.server.user.application.dto.command.UpdateUserCommand;
import com.wishboard.server.user.application.service.support.UserReader;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class UpdateUserInfoUseCase {
	private final UserReader userReader;

	private final S3Provider s3Provider;
	private final ModelMapper modelMapper;

	public UserDto excute(Long userId, UpdateUserCommand updateUserCommand, MultipartFile image) {
		var user = userReader.findById(userId);
		user.updateUserNickname(updateUserCommand.nickname());
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

}
