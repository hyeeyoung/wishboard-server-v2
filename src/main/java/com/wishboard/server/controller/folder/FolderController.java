package com.wishboard.server.controller.folder;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.wishboard.server.common.dto.SuccessResponse;
import com.wishboard.server.common.success.SuccessCode;
import com.wishboard.server.config.interceptor.Auth;
import com.wishboard.server.config.resolver.UserId;
import com.wishboard.server.controller.folder.request.CreateFolderRequest;
import com.wishboard.server.controller.folder.request.UpdateFolderRequest;
import com.wishboard.server.controller.folder.response.FolderInfoWithoutItemCountResponse;
import com.wishboard.server.controller.folder.response.FolderListResponse;
import com.wishboard.server.controller.item.response.ItemInfoResponse;
import com.wishboard.server.service.folder.FolderService;
import com.wishboard.server.service.folder.dto.FolderDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FolderController implements FolderControllerDocs {
	private final FolderService folderService;
	private final ModelMapper modelMapper;

	@Auth
	@GetMapping("/v2/folder")
	@Override
	public SuccessResponse<Page<FolderListResponse>> getFolderList(@UserId Long userId,
		@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
		var folderList = folderService.getFolderList(userId, pageable);
		var response = folderList.map(folder -> modelMapper.map(folder, FolderListResponse.class));
		return SuccessResponse.success(SuccessCode.FOLDER_LIST_SUCCESS, response);
	}

	@ResponseStatus(HttpStatus.CREATED)
	@Auth
	@PostMapping("/v2/folder")
	@Override
	public SuccessResponse<FolderInfoWithoutItemCountResponse> createFolder(@UserId Long userId, @Valid @RequestBody CreateFolderRequest request) {
		var response = folderService.createFolder(userId, modelMapper.map(request, FolderDto.class));
		return SuccessResponse.success(SuccessCode.FOLDER_CREATE_SUCCESS, modelMapper.map(response, FolderInfoWithoutItemCountResponse.class));
	}

	@Auth
	@PutMapping("/v2/folder/{folderId}")
	@Override
	public SuccessResponse<FolderInfoWithoutItemCountResponse> updateFolder(@UserId Long userId, @PathVariable Long folderId,
		@Valid @RequestBody UpdateFolderRequest request) {
		var response = folderService.updateFolder(userId, folderId, modelMapper.map(request, FolderDto.class));
		return SuccessResponse.success(SuccessCode.FOLDER_NAME_UPDATE_SUCCESS, modelMapper.map(response, FolderInfoWithoutItemCountResponse.class));
	}

	@Auth
	@DeleteMapping("/v2/folder/{folderId}")
	@Override
	public SuccessResponse<Object> deleteFolder(@UserId Long userId, @PathVariable Long folderId) {
		folderService.deleteFolder(userId, folderId);
		return SuccessResponse.success(SuccessCode.FOLDER_DELETE_SUCCESS, null);
	}

	@Auth
	@GetMapping("/v2/folder/item/{folderId}")
	@Override
	public SuccessResponse<Page<ItemInfoResponse>> getItemListInFolder(@UserId Long userId, @PathVariable Long folderId,
		@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
		var itemFolderNotificationDto = folderService.getItemListInFolder(userId, folderId, pageable);
		var response = itemFolderNotificationDto.map(folder -> modelMapper.map(folder, ItemInfoResponse.class));
		return SuccessResponse.success(SuccessCode.ITEM_LIST_IN_FOLDER_SUCCESS, response);
	}

	@Auth
	@GetMapping("/v2/folder/list")
	@Override
	public SuccessResponse<List<FolderInfoWithoutItemCountResponse>> getFolderListWithoutItemCount(@UserId Long userId) {
		var folderList = folderService.getFolderList(userId);
		var response = folderList.stream()
			.map(folder -> modelMapper.map(folder, FolderInfoWithoutItemCountResponse.class))
			.toList();
		return SuccessResponse.success(SuccessCode.FOLDER_LIST_FOR_ITEM_DETAIL_SUCCESS, response);
	}
}
