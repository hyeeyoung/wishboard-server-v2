package com.wishboard.server.common.application.port.out;

import com.wishboard.server.image.application.dto.request.ImageUploadFileRequest;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String uploadFile(ImageUploadFileRequest request, MultipartFile file);
    void deleteFile(String fileUrl); // Added based on S3Provider's deleteFile method
}
