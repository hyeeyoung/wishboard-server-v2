package com.wishboard.server.folder.application.service;

import com.wishboard.server.common.exception.NotFoundException;
import com.wishboard.server.common.exception.ValidationException; // Assuming this is used by FolderValidator
import com.wishboard.server.folder.application.dto.FolderDto;
import com.wishboard.server.folder.application.dto.command.CreateFolderCommand;
import com.wishboard.server.folder.application.service.support.FolderValidator;
import com.wishboard.server.folder.domain.model.Folder;
import com.wishboard.server.folder.domain.repository.FolderRepository;
import com.wishboard.server.user.application.service.support.UserReader;
import com.wishboard.server.user.domain.model.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateFolderUseCaseTest {

    @InjectMocks
    private CreateFolderUseCase createFolderUseCase;

    @Mock
    private UserReader userReader;
    @Mock
    private FolderValidator folderValidator;
    @Mock
    private FolderRepository folderRepository;
    @Mock
    private ModelMapper modelMapper;

    private User mockUser;
    private Folder mockFolder;
    private CreateFolderCommand createFolderCommand;

    @BeforeEach
    void setUp() {
        // Mock User
        mockUser = User.newInstance("test@example.com", "password", "fcmToken", "deviceInfo", User.AuthType.INTERNAL, User.OsType.AOS);
        // Simulate setting ID for mockUser using reflection as constructor doesn't take ID
        try {
            java.lang.reflect.Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(mockUser, 1L);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        createFolderCommand = new CreateFolderCommand("New Folder");

        // Mock Folder (as would be returned by repository.save)
        mockFolder = Folder.newInstance(mockUser, createFolderCommand.folderName());
        // Simulate setting ID for mockFolder
        try {
            java.lang.reflect.Field idField = Folder.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(mockFolder, 101L); 
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Test Case 1: Successful Folder Creation")
    void createFolder_Success() {
        // Setup
        Long userId = 1L;

        when(userReader.findById(userId)).thenReturn(mockUser);
        doNothing().when(folderValidator).checkDuplicateFolderName(userId, createFolderCommand.folderName());
        when(folderRepository.save(any(Folder.class))).thenReturn(mockFolder);
        
        FolderDto expectedDto = FolderDto.builder()
            .folderId(mockFolder.getId())
            .folderName(mockFolder.getFolderName())
            .build();
        when(modelMapper.map(mockFolder, FolderDto.class)).thenReturn(expectedDto);

        // Action
        FolderDto resultDto = createFolderUseCase.execute(userId, createFolderCommand);

        // Verification
        verify(userReader, times(1)).findById(userId);
        verify(folderValidator, times(1)).checkDuplicateFolderName(userId, createFolderCommand.folderName());
        
        ArgumentCaptor<Folder> folderCaptor = ArgumentCaptor.forClass(Folder.class);
        verify(folderRepository, times(1)).save(folderCaptor.capture());
        Folder savedFolder = folderCaptor.getValue();

        assertThat(savedFolder.getFolderName()).isEqualTo(createFolderCommand.folderName());
        assertThat(savedFolder.getUser()).isEqualTo(mockUser);

        assertThat(resultDto).isNotNull();
        assertThat(resultDto.getFolderId()).isEqualTo(mockFolder.getId());
        assertThat(resultDto.getFolderName()).isEqualTo(createFolderCommand.folderName());
    }

    @Test
    @DisplayName("Test Case 2: Folder Creation Fails Due to Duplicate Name")
    void createFolder_DuplicateName_ThrowsValidationException() {
        // Setup
        Long userId = 1L;
        
        when(userReader.findById(userId)).thenReturn(mockUser);
        doThrow(new ValidationException("Duplicate folder name"))
            .when(folderValidator).checkDuplicateFolderName(userId, createFolderCommand.folderName());

        // Action & Verification
        assertThrows(ValidationException.class, () -> {
            createFolderUseCase.execute(userId, createFolderCommand);
        });

        verify(userReader, times(1)).findById(userId);
        verify(folderValidator, times(1)).checkDuplicateFolderName(userId, createFolderCommand.folderName());
        verify(folderRepository, never()).save(any(Folder.class));
    }

    @Test
    @DisplayName("Test Case 3: User Not Found")
    void createFolder_UserNotFound_ThrowsNotFoundException() {
        // Setup
        Long nonExistentUserId = 999L;
        
        when(userReader.findById(nonExistentUserId)).thenThrow(new NotFoundException("User not found"));

        // Action & Verification
        assertThrows(NotFoundException.class, () -> {
            createFolderUseCase.execute(nonExistentUserId, createFolderCommand);
        });

        verify(userReader, times(1)).findById(nonExistentUserId);
        verify(folderValidator, never()).checkDuplicateFolderName(anyLong(), anyString());
        verify(folderRepository, never()).save(any(Folder.class));
    }
}
