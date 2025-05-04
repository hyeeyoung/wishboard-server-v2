package com.wishboard.server.service.folder;

import static com.wishboard.server.common.exception.ErrorCode.*;

import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.wishboard.server.common.exception.ValidationException;
import com.wishboard.server.domain.folder.Folder;
import com.wishboard.server.domain.folder.repository.FolderRepository;
import com.wishboard.server.domain.item.repository.ItemRepository;
import com.wishboard.server.domain.user.User;
import com.wishboard.server.domain.user.repository.UserRepository;
import com.wishboard.server.service.folder.dto.FolderDto;
import com.wishboard.server.service.folder.dto.command.CreateFolderCommand;
import com.wishboard.server.service.folder.dto.command.UpdateFolderCommand;
import com.wishboard.server.service.item.dto.FolderItemDto;
import com.wishboard.server.service.item.dto.ItemFolderNotificationDto;
import com.wishboard.server.service.user.UserServiceUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class FolderService {
	private final UserRepository userRepository;
	private final FolderRepository folderRepository;
	private final ModelMapper modelMapper;
	private final ItemRepository itemRepository;

	public Page<FolderDto> getFolderList(Long userId, Pageable pageable) {
		var user = UserServiceUtils.findUserById(userRepository, userId);

		// 해당 유저의 전체 폴더 목록
		var folders = folderRepository.findAllByUser(user, pageable);
		List<Long> folderIds = folders.getContent().stream().map(Folder::getId).toList();

		// 폴더에 저장된 최신 아이템 이미지 (섬네일) 및 아이템 개수 추출
		Map<Long, FolderItemDto> folderItemDtoMap = itemRepository.findLatestItemByFolderIds(folderIds);

		var results = folders.stream().map(folder -> {
			var folderItemDto = folderItemDtoMap.get(folder.getId());
			if (folderItemDto.getItemCount() == 0L) {
				return FolderDto.builder()
					.id(folder.getId())
					.folderName(folder.getFolderName())
					.itemCount(0L)
					.build();
			}
			return FolderDto.builder()
				.id(folder.getId())
				.folderName(folder.getFolderName())
				.folderThumbnail(folderItemDto.getLastestItem().getImages().getFirst().getItemImageUrl())
				.itemCount(folderItemDto.getItemCount())
				.build();
		}).toList();
		return new PageImpl<>(results, pageable, results.size());
	}

	public List<FolderDto> getFolderList(Long userId) {
		var user = UserServiceUtils.findUserById(userRepository, userId);

		// 해당 유저의 전체 폴더 목록
		var folders = folderRepository.findAllByUserOrderByCreatedAtDesc(user);
		List<Long> folderIds = folders.stream().map(Folder::getId).toList();

		// 폴더에 저장된 최신 아이템 이미지 (섬네일) 및 아이템 개수 추출
		Map<Long, FolderItemDto> folderItemDtoMap = itemRepository.findLatestItemByFolderIds(folderIds);

		return folders.stream().map(folder -> {
			var folderItemDto = folderItemDtoMap.get(folder.getId());
			if (folderItemDto.getItemCount() == 0L) {
				return FolderDto.builder()
					.id(folder.getId())
					.folderName(folder.getFolderName())
					.itemCount(0L)
					.build();
			}
			return FolderDto.builder()
				.id(folder.getId())
				.folderName(folder.getFolderName())
				.folderThumbnail(folderItemDto.getLastestItem().getImages().getFirst().getItemImageUrl())
				.itemCount(folderItemDto.getItemCount())
				.build();
		}).toList();
	}

	public FolderDto createFolder(Long userId, CreateFolderCommand createFolderCommand) {
		var user = UserServiceUtils.findUserById(userRepository, userId);
		checkDuplicateFolderName(user, createFolderCommand.folderName());
		var folder = folderRepository.save(Folder.newInstance(user, createFolderCommand.folderName()));
		return modelMapper.map(folder, FolderDto.class);
	}

	public FolderDto updateFolder(Long userId, Long folderId, UpdateFolderCommand updateFolderCommand) {
		var user = UserServiceUtils.findUserById(userRepository, userId);
		checkDuplicateFolderName(user, updateFolderCommand.folderName());
		var folder = FolderServiceUtils.findFolderByIdAndUserId(folderRepository, folderId, user);
		folder.updateFolderName(updateFolderCommand.folderName());
		return modelMapper.map(folder, FolderDto.class);
	}

	public void deleteFolder(Long userId, Long folderId) {
		var user = UserServiceUtils.findUserById(userRepository, userId);
		var folder = FolderServiceUtils.findFolderByIdAndUserId(folderRepository, folderId, user);
		folderRepository.delete(folder);
	}

	public Page<ItemFolderNotificationDto> getItemListInFolder(Long userId, Long folderId, Pageable pageable) {
		var user = UserServiceUtils.findUserById(userRepository, userId);
		var folder = FolderServiceUtils.findFolderByIdAndUserId(folderRepository, folderId, user);
		return folderRepository.findItemListByUserIdAndFolderId(user.getId(), folder.getId(), pageable);
	}

	private void checkDuplicateFolderName(User user, String folderName) {
		var folder = folderRepository.findByUserAndFolderName(user, folderName);
		if (folder.isPresent()) {
			throw new ValidationException("이미 존재하는 폴더명입니다.", VALIDATION_FOLDER_NAME_DUPLICATE_EXCEPTION);
		}
	}
}
