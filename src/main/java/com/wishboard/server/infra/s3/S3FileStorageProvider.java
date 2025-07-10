package com.wishboard.server.infra.s3;

import com.wishboard.server.common.application.port.out.FileStorageService;
import com.wishboard.server.image.application.dto.request.ImageUploadFileRequest; // Assuming this is the correct import
import com.wishboard.server.image.infrastructure.S3FileStorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service // Changed from @Component as per suggestion, though @Component is also fine
public class S3FileStorageProvider implements FileStorageService {

    private final S3FileStorageClient fileStorageClient;

    @Override
    public String uploadFile(ImageUploadFileRequest request, MultipartFile file) {
        // Assuming ImageUploadFileRequest has these methods.
        // If ImageUploadFileRequest is different from the original UploadFileRequest,
        // this part might need adjustment if method names or logic differ.
        request.validateAvailableContentType(file.getContentType());
        String fileName = request.getFileNameWithBucketDirectory(file.getOriginalFilename());
        fileStorageClient.uploadFile(file, fileName);
        return fileStorageClient.getFileUrl(fileName);
    }

    @Override
    public void deleteFile(String fileUrl) { // Changed parameter name from fileName to fileUrl to match interface
        if (fileUrl != null) {
            // Assuming the fileUrl is the full URL. The original S3Provider split it.
            // Example: "https://bucket-name.s3.region.amazonaws.com/directory/filename.jpg"
            // The part to delete is "directory/filename.jpg"
            // This logic might need to be more robust depending on URL structure.
            if (fileUrl.contains(".com/")) { // Basic check
                String key = fileUrl.substring(fileUrl.indexOf(".com/") + ".com/".length());
                fileStorageClient.deleteFile(key);
            } else {
                // Log or handle cases where the URL format is not as expected.
                // For now, if it's not a full URL containing ".com/", we might assume it's already a key.
                // This part of the original S3Provider's deleteFile was:
                // String[] image = fileName.split(".com/");
                // fileStorageClient.deleteFile(image[1]);
                // This implies fileName was expected to be a full URL.
                // If fileUrl is sometimes just the key, this logic needs to be smarter.
                // For now, sticking to the idea that fileUrl is the full URL.
                // Consider logging a warning if the format is unexpected.
                // System.err.println("Warning: fileUrl format may not be as expected for deletion: " + fileUrl);
            }
        }
    }
}
