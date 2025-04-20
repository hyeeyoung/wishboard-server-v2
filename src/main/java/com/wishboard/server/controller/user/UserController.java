package com.wishboard.server.controller.user;

import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wishboard.server.common.dto.SuccessResponse;
import com.wishboard.server.common.success.SuccessCode;
import com.wishboard.server.config.interceptor.Auth;
import com.wishboard.server.config.resolver.UserId;
import com.wishboard.server.controller.user.request.UpdatePasswordRequest;
import com.wishboard.server.controller.user.request.UpdateUserInfoRequest;
import com.wishboard.server.controller.user.response.UserInfoResponse;
import com.wishboard.server.service.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController implements UserControllerDocs {
	private final UserService userService;
	private final ModelMapper modelMapper;

	@Auth
	@GetMapping("/v2/user")
	@Override
	public SuccessResponse<UserInfoResponse> getUserInfo(@UserId Long userId) {
		var userDto = userService.getUserInfo(userId);
		return SuccessResponse.success(SuccessCode.USER_INFO_SUCCESS, modelMapper.map(userDto, UserInfoResponse.class));
	}

	@Auth
	@PutMapping("/v2/user/push-state/{pushState}")
	@Override
	public SuccessResponse<UserInfoResponse> updatePushState(@UserId Long userId, @PathVariable("pushState") boolean pushState) {
		var userDto = userService.updatePushState(userId, pushState);
		return SuccessResponse.success(SuccessCode.USER_PUSH_STATE_UPDATE_SUCCESS, modelMapper.map(userDto, UserInfoResponse.class));
	}

	@Auth
	@PutMapping(value = "/v2/user", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Override
	public SuccessResponse<UserInfoResponse> updateUserInfo(@UserId Long userId,
		@Valid @RequestPart("request") UpdateUserInfoRequest request,
		@RequestPart(required = false, name = "profile_img") MultipartFile image) {
		var userDto = userService.updateUserInfo(userId, request, image);
		return SuccessResponse.success(SuccessCode.USER_INFO_UPDATE_SUCCESS, modelMapper.map(userDto, UserInfoResponse.class));
	}

	@Auth
	@PutMapping(value = "/v2/user/re-passwd")
	@Override
	public SuccessResponse<UserInfoResponse> updatePassword(@UserId Long userId, @Valid @RequestBody UpdatePasswordRequest request) {
		var userDto = userService.updatePassword(userId, request);
		return SuccessResponse.success(SuccessCode.USER_PASSWORD_UPDATE_SUCCESS, modelMapper.map(userDto, UserInfoResponse.class));
	}

	@Auth
	@DeleteMapping(value = "/v2/user")
	@Override
	public SuccessResponse<UserInfoResponse> deleteUser(@UserId Long userId) {
		userService.deleteUser(userId);
		return SuccessResponse.success(SuccessCode.USER_DELETE_SUCCESS,null);
	}
}
