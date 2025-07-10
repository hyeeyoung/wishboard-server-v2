package com.wishboard.server.folder.application.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wishboard.server.folder.application.dto.FolderDto;
import com.wishboard.server.folder.application.dto.command.CreateFolderCommand;
import com.wishboard.server.folder.application.service.support.FolderValidator;
import com.wishboard.server.folder.domain.model.Folder;
import com.wishboard.server.folder.domain.repository.FolderRepository;
import com.wishboard.server.user.application.service.support.UserReader;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class CreateFolderUseCase {
	private final UserReader userReader;
	private final FolderValidator folderValidator;

	private final FolderRepository folderRepository;

	private final ModelMapper modelMapper;

	public FolderDto execute(Long userId, CreateFolderCommand createFolderCommand) {
		var user = userReader.findById(userId);
		folderValidator.checkDuplicateFolderName(user.getId(), createFolderCommand.folderName()); // Changed to use userId
		var folder = folderRepository.save(Folder.newInstance(user, createFolderCommand.folderName()));
		return modelMapper.map(folder, FolderDto.class);
	}

}
