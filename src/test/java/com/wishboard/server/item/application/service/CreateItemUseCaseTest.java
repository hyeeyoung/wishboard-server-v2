package com.wishboard.server.item.application.service;

import com.wishboard.server.common.application.port.out.FileStorageService;
import com.wishboard.server.common.domain.ItemNotificationType;
import com.wishboard.server.common.exception.NotFoundException;
import com.wishboard.server.common.type.FileType;
import com.wishboard.server.folder.application.service.support.FolderReader;
import com.wishboard.server.folder.domain.model.Folder;
import com.wishboard.server.item.application.dto.ItemFolderNotificationDto;
import com.wishboard.server.item.application.dto.command.CreateItemCommand;
import com.wishboard.server.item.application.service.support.ItemValidator;
import com.wishboard.server.item.domain.event.ItemCreatedEvent;
import com.wishboard.server.item.domain.model.AddType;
import com.wishboard.server.item.domain.model.Item;
import com.wishboard.server.item.domain.model.ItemImage;
import com.wishboard.server.item.domain.repository.ItemRepository;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateItemUseCaseTest {

    @InjectMocks
    private CreateItemUseCase createItemUseCase;

    @Mock
    private UserReader userReader;
    @Mock
    private FolderReader folderReader;
    @Mock
    private ItemValidator itemValidator;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    private User mockUser;
    private Item mockItem;
    private Folder mockFolder;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @BeforeEach
    void setUp() {
        mockUser = User.newInstance("test@example.com", "password", "fcmToken", "deviceInfo", User.AuthType.INTERNAL, User.OsType.AOS);
        // Simulate setting ID via reflection or ensure constructor/builder allows it if not final
        try {
            java.lang.reflect.Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(mockUser, 1L);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        
        mockItem = Item.newInstance(mockUser, "Test Item", "10000", "http://item.url", "Test Memo", AddType.MANUAL);
        // Simulate setting ID for mockItem
        try {
            java.lang.reflect.Field idField = Item.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(mockItem, 101L);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        mockFolder = Folder.newInstance(mockUser, "Test Folder");
        // Simulate setting ID for mockFolder
         try {
            java.lang.reflect.Field idField = Folder.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(mockFolder, 201L);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Test Case 1: Successful Item Creation (Basic)")
    void createItemBasic() {
        Long userId = 1L;
        CreateItemCommand command = new CreateItemCommand("Test Item", 10000, "http://item.url", "Test Memo", null, null, null);

        when(userReader.findById(userId)).thenReturn(mockUser);
        when(itemRepository.save(any(Item.class))).thenReturn(mockItem);

        ItemFolderNotificationDto resultDto = createItemUseCase.execute(userId, command, Collections.emptyList(), AddType.MANUAL);

        verify(itemRepository, times(1)).save(any(Item.class));
        verify(eventPublisher, never()).publishEvent(any(ItemCreatedEvent.class));
        verify(fileStorageService, never()).uploadFile(any(), any(MultipartFile.class));
        
        assertThat(resultDto).isNotNull();
        assertThat(resultDto.getItemName()).isEqualTo(command.itemName());
        assertThat(resultDto.getItemPrice()).isEqualTo(String.valueOf(command.itemPrice()));
        assertThat(resultDto.getFolderId()).isNull();
        assertThat(resultDto.getItemNotificationType()).isNull();
    }

    @Test
    @DisplayName("Test Case 2: Successful Item Creation with Folder")
    void createItemWithFolder() {
        Long userId = 1L;
        Long folderId = 201L;
        CreateItemCommand command = new CreateItemCommand("Test Item with Folder", 15000, "http://item.folder.url", "Memo", folderId, null, null);
        
        Item itemToSave = Item.newInstance(mockUser, command.itemName(), String.valueOf(command.itemPrice()), command.itemUrl(), command.itemMemo(), AddType.MANUAL);
        // Simulate setting ID for itemToSave
        try {
            java.lang.reflect.Field idField = Item.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(itemToSave, 102L);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }


        when(userReader.findById(userId)).thenReturn(mockUser);
        when(folderReader.findByIdAndUserId(folderId, userId)).thenReturn(mockFolder);
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
            Item savedItem = invocation.getArgument(0);
             try { // Simulate ID assignment by repository
                java.lang.reflect.Field idField = Item.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(savedItem, 102L); // Assign a mock ID
            } catch (NoSuchFieldException | IllegalAccessException e) { e.printStackTrace(); }
            return savedItem;
        });


        ItemFolderNotificationDto resultDto = createItemUseCase.execute(userId, command, Collections.emptyList(), AddType.MANUAL);

        verify(itemRepository, times(1)).save(any(Item.class));
        verify(folderReader, times(1)).findByIdAndUserId(folderId, userId);
        
        ArgumentCaptor<Item> itemCaptor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository).save(itemCaptor.capture());
        assertThat(itemCaptor.getValue().getFolderId()).isEqualTo(folderId);

        assertThat(resultDto).isNotNull();
        assertThat(resultDto.getItemName()).isEqualTo(command.itemName());
        assertThat(resultDto.getFolderId()).isEqualTo(folderId);
        // folderName is set to null in DTO.of, so we don't assert it here based on current DTO logic
    }

    @Test
    @DisplayName("Test Case 3: Successful Item Creation with Notification")
    void createItemWithNotification() {
        Long userId = 1L;
        ItemNotificationType notificationType = ItemNotificationType.RESTOCK;
        String notificationDateStr = LocalDateTime.now().plusDays(1).format(formatter);
        CreateItemCommand command = new CreateItemCommand("Notify Item", 20000, "http://notify.item.url", "Notify Memo", null, notificationType, notificationDateStr);

        when(userReader.findById(userId)).thenReturn(mockUser);
        when(itemRepository.save(any(Item.class))).thenReturn(mockItem); // mockItem already has ID 101L
        // itemValidator.validateDateInFuture is void, no need to mock behavior unless it throws an exception

        ItemFolderNotificationDto resultDto = createItemUseCase.execute(userId, command, Collections.emptyList(), AddType.MANUAL);

        verify(itemRepository, times(1)).save(any(Item.class));
        verify(itemValidator, times(1)).validateDateInFuture(notificationType, notificationDateStr);
        
        ArgumentCaptor<ItemCreatedEvent> eventCaptor = ArgumentCaptor.forClass(ItemCreatedEvent.class);
        verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());
        
        ItemCreatedEvent publishedEvent = eventCaptor.getValue();
        assertThat(publishedEvent.getItemId()).isEqualTo(mockItem.getId());
        assertThat(publishedEvent.getUserId()).isEqualTo(userId);
        assertThat(publishedEvent.getItemNotificationType()).isEqualTo(notificationType);
        assertThat(publishedEvent.getItemNotificationDate()).isEqualTo(LocalDateTime.parse(notificationDateStr, formatter));

        assertThat(resultDto).isNotNull();
        assertThat(resultDto.getItemName()).isEqualTo(command.itemName());
        assertThat(resultDto.getItemNotificationType()).isEqualTo(notificationType);
        assertThat(resultDto.getItemNotificationDate()).isEqualTo(LocalDateTime.parse(notificationDateStr, formatter));
    }
    
    // Test Case 4: Successful Item Creation with Images
    @Test
    @DisplayName("Test Case 4: Successful Item Creation with Images")
    void createItemWithImages() {
        Long userId = 1L;
        CreateItemCommand command = new CreateItemCommand("Image Item", 25000, "http://image.item.url", "Image Memo", null, null, null);
        
        MultipartFile mockFile1 = mock(MultipartFile.class);
        when(mockFile1.getOriginalFilename()).thenReturn("image1.jpg");
        when(mockFile1.isEmpty()).thenReturn(false);
        // If validateAvailableContentType is called on the file, you might need:
        // when(mockFile1.getContentType()).thenReturn("image/jpeg");


        List<MultipartFile> images = List.of(mockFile1);
        String mockImageUrl1 = "http://s3.image.url/image1.jpg";

        when(userReader.findById(userId)).thenReturn(mockUser);
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
            Item savedItem = invocation.getArgument(0);
             try { // Simulate ID assignment by repository
                java.lang.reflect.Field idField = Item.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(savedItem, 103L); 
            } catch (NoSuchFieldException | IllegalAccessException e) { e.printStackTrace(); }
            return savedItem;
        });
        when(fileStorageService.uploadFile(any(com.wishboard.server.image.application.dto.request.ImageUploadFileRequest.class), eq(mockFile1))).thenReturn(mockImageUrl1);
       

        ItemFolderNotificationDto resultDto = createItemUseCase.execute(userId, command, images, AddType.MANUAL);

        verify(itemRepository, times(1)).save(any(Item.class));
        verify(fileStorageService, times(1)).uploadFile(any(com.wishboard.server.image.application.dto.request.ImageUploadFileRequest.class), eq(mockFile1));
        
        ArgumentCaptor<Item> itemCaptor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository).save(itemCaptor.capture());
        Item savedItem = itemCaptor.getValue();
        assertThat(savedItem.getImages()).hasSize(1);
        assertThat(savedItem.getImages().get(0).getItemImageUrl()).isEqualTo(mockImageUrl1);
        assertThat(savedItem.getImages().get(0).getItemImg()).isEqualTo("image1.jpg");

        assertThat(resultDto).isNotNull();
        assertThat(resultDto.getItemImages()).hasSize(1);
        assertThat(resultDto.getItemImages().get(0).getItemImageUrl()).isEqualTo(mockImageUrl1);
    }

    @Test
    @DisplayName("Test Case 5: Successful Item Creation with All Options")
    void createItemWithAllOptions() {
        Long userId = 1L;
        Long folderId = 201L;
        ItemNotificationType notificationType = ItemNotificationType.REMINDER;
        String notificationDateStr = LocalDateTime.now().plusDays(2).format(formatter);
        
        CreateItemCommand command = new CreateItemCommand(
            "Full Option Item", 
            30000, 
            "http://full.item.url", 
            "Full Memo", 
            folderId, 
            notificationType, 
            notificationDateStr
        );

        MultipartFile mockFile1 = mock(MultipartFile.class);
        when(mockFile1.getOriginalFilename()).thenReturn("image_full.jpg");
        when(mockFile1.isEmpty()).thenReturn(false);
        // when(mockFile1.getContentType()).thenReturn("image/jpeg"); // If needed by validator

        List<MultipartFile> images = List.of(mockFile1);
        String mockImageUrl = "http://s3.image.url/image_full.jpg";

        // Mock User
        when(userReader.findById(userId)).thenReturn(mockUser);

        // Mock Folder
        when(folderReader.findByIdAndUserId(folderId, userId)).thenReturn(mockFolder);
        
        // Mock Item save (assign ID to item)
        Item itemToReturn = Item.newInstance(mockUser, command.itemName(), String.valueOf(command.itemPrice()), command.itemUrl(), command.itemMemo(), AddType.MANUAL);
        itemToReturn.updateFolderId(folderId); // Set folderId as it would be after folderReader call
        // Simulate ID assignment by repository
        try {
            java.lang.reflect.Field idField = Item.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(itemToReturn, 104L); 
        } catch (NoSuchFieldException | IllegalAccessException e) { e.printStackTrace(); }
        
        // Add image to itemToReturn to simulate state after image processing
        ItemImage itemImage = new ItemImage("image_full.jpg", mockImageUrl, itemToReturn);
        List<ItemImage> itemImagesList = new ArrayList<>();
        itemImagesList.add(itemImage);
        itemToReturn.addItemImage(itemImagesList); // Simulating images added to the item
        
        when(itemRepository.save(any(Item.class))).thenReturn(itemToReturn);


        // Mock FileStorageService
        when(fileStorageService.uploadFile(any(com.wishboard.server.image.application.dto.request.ImageUploadFileRequest.class), eq(mockFile1))).thenReturn(mockImageUrl);
        
        // itemValidator.validateDateInFuture is void

        // Execute
        ItemFolderNotificationDto resultDto = createItemUseCase.execute(userId, command, images, AddType.MANUAL);

        // Verify interactions
        verify(userReader, times(1)).findById(userId);
        verify(folderReader, times(1)).findByIdAndUserId(folderId, userId);
        verify(itemRepository, times(1)).save(any(Item.class));
        verify(fileStorageService, times(1)).uploadFile(any(com.wishboard.server.image.application.dto.request.ImageUploadFileRequest.class), eq(mockFile1));
        verify(itemValidator, times(1)).validateDateInFuture(notificationType, notificationDateStr);
        
        ArgumentCaptor<ItemCreatedEvent> eventCaptor = ArgumentCaptor.forClass(ItemCreatedEvent.class);
        verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());
        ItemCreatedEvent publishedEvent = eventCaptor.getValue();

        // Assertions
        assertThat(resultDto).isNotNull();
        assertThat(resultDto.getItemName()).isEqualTo(command.itemName());
        assertThat(resultDto.getFolderId()).isEqualTo(folderId);
        // folderName is null
        assertThat(resultDto.getItemNotificationType()).isEqualTo(notificationType);
        assertThat(resultDto.getItemNotificationDate()).isEqualTo(LocalDateTime.parse(notificationDateStr, formatter));
        assertThat(resultDto.getItemImages()).hasSize(1);
        assertThat(resultDto.getItemImages().get(0).getItemImageUrl()).isEqualTo(mockImageUrl);

        assertThat(publishedEvent.getItemId()).isEqualTo(itemToReturn.getId());
        assertThat(publishedEvent.getUserId()).isEqualTo(userId);
        assertThat(publishedEvent.getItemName()).isEqualTo(command.itemName());
        assertThat(publishedEvent.getItemNotificationType()).isEqualTo(notificationType);
        assertThat(publishedEvent.getItemNotificationDate()).isEqualTo(LocalDateTime.parse(notificationDateStr, formatter));
        assertThat(publishedEvent.getItemUrl()).isEqualTo(command.itemUrl());
        assertThat(publishedEvent.getItemImageUrl()).isEqualTo(mockImageUrl); // Image URL from the saved item
    }


    @Test
    @DisplayName("Test Case 6: Folder Not Found")
    void createItemFolderNotFound() {
        Long userId = 1L;
        Long nonExistentFolderId = 999L;
        CreateItemCommand command = new CreateItemCommand("Item with NonExistent Folder", 30000, "http://fail.item.url", "Fail Memo", nonExistentFolderId, null, null);

        when(userReader.findById(userId)).thenReturn(mockUser);
        // Item is saved before folder validation
        when(itemRepository.save(any(Item.class))).thenReturn(mockItem); 
        when(folderReader.findByIdAndUserId(nonExistentFolderId, userId)).thenThrow(new NotFoundException("Folder not found"));

        assertThrows(NotFoundException.class, () -> {
            createItemUseCase.execute(userId, command, Collections.emptyList(), AddType.MANUAL);
        });
        
        // itemRepository.save IS called before folderReader
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    @DisplayName("Test Case 7: Invalid Notification Date (Validation Fails)")
    void createItemInvalidNotificationDate() {
        Long userId = 1L;
        ItemNotificationType notificationType = ItemNotificationType.REMINDER;
        String pastDateStr = LocalDateTime.now().minusDays(1).format(formatter); // Invalid date
        CreateItemCommand command = new CreateItemCommand("Invalid Date Item", 35000, "http://invalid.date.url", "Date Memo", null, notificationType, pastDateStr);

        when(userReader.findById(userId)).thenReturn(mockUser);
        // Item save will be called before validator, so we need to mock it.
        // Use a specific item instance for more clarity if needed, but any(Item.class) is fine for this test focus.
        when(itemRepository.save(any(Item.class))).thenReturn(mockItem); 
        
        // Simulate itemValidator throwing an exception
        doThrow(new IllegalArgumentException("Date must be in the future"))
            .when(itemValidator).validateDateInFuture(notificationType, pastDateStr);

        assertThrows(IllegalArgumentException.class, () -> {
            createItemUseCase.execute(userId, command, Collections.emptyList(), AddType.MANUAL);
        });
        
        // itemRepository.save IS called before itemValidator in the current code structure
        verify(itemRepository, times(1)).save(any(Item.class)); 
        verify(eventPublisher, never()).publishEvent(any(ItemCreatedEvent.class)); // Event publishing should not happen
    }
}
