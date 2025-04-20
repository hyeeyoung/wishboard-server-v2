package com.wishboard.server.service.user;

import io.micrometer.common.util.StringUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.wishboard.server.common.type.FileType;
import com.wishboard.server.controller.user.request.UpdatePasswordRequest;
import com.wishboard.server.controller.user.request.UpdateUserInfoRequest;
import com.wishboard.server.domain.user.AuthType;
import com.wishboard.server.domain.user.User;
import com.wishboard.server.domain.user.repository.UserRepository;
import com.wishboard.server.service.auth.AuthServiceUtils;
import com.wishboard.server.service.image.provider.S3Provider;
import com.wishboard.server.service.image.provider.dto.request.ImageUploadFileRequest;
import com.wishboard.server.service.user.dto.CreateUserDto;
import com.wishboard.server.service.user.dto.UserDto;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    private final S3Provider s3Provider;
    private final ModelMapper modelMapper;

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

    // TODO 이미지 업로드까지 정상적으로 잘 되는지 확인 필요!!!
    public UserDto updateUserInfo(Long userId, UpdateUserInfoRequest request, MultipartFile image) {
        var user = UserServiceUtils.findUserById(userRepository, userId);
        user.updateUserNickname(request.getNickname());
        if (image != null && !image.isEmpty()) {
            String profileImageUrl = s3Provider.uploadFile(ImageUploadFileRequest.of(FileType.PROFILE_IMAGE), image);
            if (StringUtils.isNotBlank(profileImageUrl)) {
                user.updateProfileImage(image.getOriginalFilename(), profileImageUrl);
            }
        }
        return modelMapper.map(user, UserDto.class);
    }

    public UserDto updatePassword(Long userId, @Valid UpdatePasswordRequest request) {
        var user = UserServiceUtils.findUserById(userRepository, userId);
        String hashedPassword = AuthServiceUtils.getHashedPassword(request.getNewPassword());
        user.updatePassword(hashedPassword);
        return modelMapper.map(user, UserDto.class);
    }

    public void deleteUser(Long userId) {
        var user = UserServiceUtils.findUserById(userRepository, userId);
        userRepository.delete(user);
    }
}
