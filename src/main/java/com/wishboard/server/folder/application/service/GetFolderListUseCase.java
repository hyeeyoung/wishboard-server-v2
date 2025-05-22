package com.wishboard.server.folder.application.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wishboard.server.item.domain.repository.ItemRepository;
import com.wishboard.server.folder.application.dto.FolderDto;
import com.wishboard.server.folder.application.service.support.FolderThumbnailMapper;
import com.wishboard.server.folder.domain.model.Folder;
import com.wishboard.server.folder.domain.repository.FolderRepository;
import com.wishboard.server.item.application.dto.FolderItemDto;
import com.wishboard.server.user.application.service.support.UserReader;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class GetFolderListUseCase {
	private final UserReader userReader;
	private final FolderThumbnailMapper folderThumbnailMapper;

	private final FolderRepository folderRepository;
	private final ItemRepository itemRepository;

	public Page<FolderDto> execute(Long userId, Pageable pageable) {
		var user = userReader.findById(userId);

		// 해당 유저의 전체 폴더 목록
		var folders = folderRepository.findAllByUserId(user.getId(), pageable); // Changed to use userId
		List<Long> folderIds = folders.getContent().stream().map(Folder::getId).toList();

		// 폴더에 저장된 최신 아이템 이미지 (섬네일) 및 아이템 개수 추출
		Map<Long, FolderItemDto> folderItemDtoMap = itemRepository.findLatestItemByFolderIds(folderIds);

		var results = folders.stream().map(folder -> {
			var folderItemDto = folderItemDtoMap.get(folder.getId());
			return folderThumbnailMapper.toDto(folder, folderItemDto);
		}).toList();
		return new PageImpl<>(results, pageable, results.size());
	}

	public List<FolderDto> execute(Long userId) {
		var user = userReader.findById(userId);

		// 해당 유저의 전체 폴더 목록
		var folders = folderRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId()); // Changed to use userId
		List<Long> folderIds = folders.stream().map(Folder::getId).toList();

		// 폴더에 저장된 최신 아이템 이미지 (섬네일) 및 아이템 개수 추출
		Map<Long, FolderItemDto> folderItemDtoMap = itemRepository.findLatestItemByFolderIds(folderIds);

		return folders.stream().map(folder -> {
			var folderItemDto = folderItemDtoMap.get(folder.getId());
			return folderThumbnailMapper.toDto(folder, folderItemDto);
		}).toList();
	}
}
