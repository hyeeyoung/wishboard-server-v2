package com.wishboard.server.user.presentation;

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
import com.wishboard.server.user.application.service.DeleteUserUseCase;
import com.wishboard.server.user.application.service.GetUserInfoUseCase;
import com.wishboard.server.user.application.service.UpdatePasswordUseCase;
import com.wishboard.server.user.application.service.UpdatePushStateUseCase;
import com.wishboard.server.user.application.service.UpdateUserInfoUseCase;
import com.wishboard.server.user.presentation.docs.UserControllerDocs;
import com.wishboard.server.user.presentation.dto.request.UpdatePasswordRequest;
import com.wishboard.server.user.presentation.dto.request.UpdateUserInfoRequest;
import com.wishboard.server.user.presentation.dto.response.UserInfoResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController implements UserControllerDocs {
	private final GetUserInfoUseCase getUserInfoUseCase;
	private final UpdatePushStateUseCase updatePushStateUseCase;
	private final UpdateUserInfoUseCase updateUserInfoUseCase;
	private final UpdatePasswordUseCase updatePasswordUseCase;
	private final DeleteUserUseCase deleteUserUseCase;
	private final ModelMapper modelMapper;

	@Auth
	@GetMapping("/v2/user")
	@Override
	public SuccessResponse<UserInfoResponse> getUserInfo(@UserId Long userId) {
		var userDto = getUserInfoUseCase.execute(userId);
		return SuccessResponse.success(SuccessCode.USER_INFO_SUCCESS, modelMapper.map(userDto, UserInfoResponse.class));
	}

	@Auth
	@PutMapping("/v2/user/push-state/{pushState}")
	@Override
	public SuccessResponse<UserInfoResponse> updatePushState(@UserId Long userId, @PathVariable("pushState") boolean pushState) {
		var userDto = updatePushStateUseCase.execute(userId, pushState);
		return SuccessResponse.success(SuccessCode.USER_PUSH_STATE_UPDATE_SUCCESS, modelMapper.map(userDto, UserInfoResponse.class));
	}

	@Auth
	@PutMapping(value = "/v2/user", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Override
	public SuccessResponse<UserInfoResponse> updateUserInfo(@UserId Long userId,
		@Valid @RequestPart("request") UpdateUserInfoRequest request,
		@RequestPart(required = false, name = "profileImage") MultipartFile image) {
		var userDto = updateUserInfoUseCase.excute(userId, request.toCommand(), image);
		return SuccessResponse.success(SuccessCode.USER_INFO_UPDATE_SUCCESS, modelMapper.map(userDto, UserInfoResponse.class));
	}

	@Auth
	@PutMapping(value = "/v2/user/re-passwd")
	@Override
	public SuccessResponse<UserInfoResponse> updatePassword(@UserId Long userId, @Valid @RequestBody UpdatePasswordRequest request) {
		var userDto = updatePasswordUseCase.execute(userId, request.toCommand());
		return SuccessResponse.success(SuccessCode.USER_PASSWORD_UPDATE_SUCCESS, modelMapper.map(userDto, UserInfoResponse.class));
	}

	@Auth
	@DeleteMapping(value = "/v2/user")
	@Override
	public SuccessResponse<Object> deleteUser(@UserId Long userId) {
		deleteUserUseCase.execute(userId);
		return SuccessResponse.success(SuccessCode.USER_DELETE_SUCCESS, null);
	}
}
