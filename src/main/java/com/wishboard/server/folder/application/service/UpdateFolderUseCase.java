package com.wishboard.server.folder.application.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wishboard.server.folder.application.dto.FolderDto;
import com.wishboard.server.folder.application.dto.command.UpdateFolderCommand;
import com.wishboard.server.folder.application.service.support.FolderReader;
import com.wishboard.server.folder.application.service.support.FolderValidator;
import com.wishboard.server.user.application.service.support.UserReader;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class UpdateFolderUseCase {
	private final UserReader userReader;
	private final FolderReader folderReader;
	private final FolderValidator folderValidator;

	private final ModelMapper modelMapper;

	public FolderDto execute(Long userId, Long folderId, UpdateFolderCommand updateFolderCommand) {
		var user = userReader.findById(userId);
		// Assuming folderValidator.checkDuplicateFolderName will also be updated to take userId
		folderValidator.checkDuplicateFolderName(user.getId(), updateFolderCommand.folderName()); 
		var folder = folderReader.findByIdAndUserId(folderId, user.getId()); // Changed to use userId
		folder.updateFolderName(updateFolderCommand.folderName());
		return modelMapper.map(folder, FolderDto.class);
	}

}
