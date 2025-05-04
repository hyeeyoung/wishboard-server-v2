package com.wishboard.server.controller.item;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wishboard.server.common.dto.SuccessResponse;
import com.wishboard.server.common.success.SuccessCode;
import com.wishboard.server.config.interceptor.Auth;
import com.wishboard.server.config.resolver.UserId;
import com.wishboard.server.controller.item.request.CreateItemRequest;
import com.wishboard.server.controller.item.request.UpdateItemRequest;
import com.wishboard.server.controller.item.response.ItemInfoResponse;
import com.wishboard.server.domain.item.AddType;
import com.wishboard.server.service.item.ItemService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ItemController implements ItemControllerDocs {
	private final ModelMapper modelMapper;
	private final ItemService itemService;

	@Auth
	@GetMapping("/v2/item")
	@Override
	public SuccessResponse<Page<ItemInfoResponse>> getAllItemInfo(@UserId Long userId,
		@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
		var itemNotificationDto = itemService.getAllItemInfo(userId, pageable);
		var response = itemNotificationDto.map(item -> modelMapper.map(item, ItemInfoResponse.class));
		return SuccessResponse.success(SuccessCode.ITEM_LIST_INFO_SUCCESS, response);
	}

	@Auth
	@GetMapping("/v2/item/{itemId}")
	@Override
	public SuccessResponse<ItemInfoResponse> getItemInfo(@UserId Long userId, @PathVariable Long itemId) {
		var itemNotificationDto = itemService.getItemInfo(userId, itemId);
		return SuccessResponse.success(SuccessCode.ITEM_INFO_SUCCESS, modelMapper.map(itemNotificationDto, ItemInfoResponse.class));
	}

	@ResponseStatus(HttpStatus.CREATED)
	@Auth
	@PostMapping(value = "/v2/item", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Override
	public SuccessResponse<ItemInfoResponse> createItem(@UserId Long userId, @Valid @RequestPart("request") CreateItemRequest request,
		@RequestPart(required = false, value = "itemImages") List<MultipartFile> images, @RequestParam("type") AddType addType) {
		var itemNotificationDto = itemService.createItem(userId, request, images, addType);
		if (request.itemNotificationType() != null && request.itemNotificationDate() != null) {
			return SuccessResponse.success(SuccessCode.ITEM_AND_NOTIFICATION_CREATE_SUCCESS,
				modelMapper.map(itemNotificationDto, ItemInfoResponse.class));
		}
		return SuccessResponse.success(SuccessCode.ITEM_CREATE_SUCCESS, modelMapper.map(itemNotificationDto, ItemInfoResponse.class));
	}

	@Auth
	@PutMapping(value = "/v2/item/{itemId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Override
	public SuccessResponse<ItemInfoResponse> updateItem(@UserId Long userId, @Valid @RequestPart("request") UpdateItemRequest request,
		@RequestPart(required = false, value = "itemImages") List<MultipartFile> images, @PathVariable Long itemId) {
		var itemNotificationDto = itemService.updateItem(userId, itemId, request, images);
		if (request.itemNotificationType() != null && request.itemNotificationDate() != null) {
			return SuccessResponse.success(SuccessCode.ITEM_AND_NOTIFICATION_UPDATE_SUCCESS,
				modelMapper.map(itemNotificationDto, ItemInfoResponse.class));
		}
		return SuccessResponse.success(SuccessCode.ITEM_UPDATE_SUCCESS, modelMapper.map(itemNotificationDto, ItemInfoResponse.class));
	}

	@Auth
	@DeleteMapping(value = "/v2/item/{itemId}")
	@Override
	public SuccessResponse<Object> deleteItem(@UserId Long userId, @PathVariable Long itemId) {
		itemService.deleteItem(userId, itemId);
		return SuccessResponse.success(SuccessCode.ITEM_AND_NOTIFICATION_DELETE_SUCCESS, null);
	}

	@Auth
	@PutMapping(value = "/v2/item/{itemId}/folder/{folderId}")
	@Override
	public SuccessResponse<ItemInfoResponse> updateItemFolder(@UserId Long userId, @PathVariable Long itemId, @PathVariable Long folderId) {
		var itemNotificationDto = itemService.updateItemFolder(userId, itemId, folderId);
		return SuccessResponse.success(SuccessCode.ITEM_FOLDER_UPDATE_SUCCESS, modelMapper.map(itemNotificationDto, ItemInfoResponse.class));
	}
}
