package com.example.community.service;

import com.example.community.common.ExceptionMessage;
import com.example.community.exception.InternalServerException;
import com.example.community.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
public class ImageStorageService {
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String storeUserImage(MultipartFile file) {
        validate(file);

        String filename = UUID.randomUUID() + extractExtension(file);

        try {
            Path targetDir = Path.of(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(targetDir);
            file.transferTo(targetDir.resolve(filename));
        } catch (IOException e) {
            throw new InternalServerException(ExceptionMessage.IMAGE_UPLOAD_FAILED.getMessage());
        }

        return "/images/" + filename;
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidRequestException(ExceptionMessage.EMPTY_IMAGE_FILE.getMessage());
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new InvalidRequestException(ExceptionMessage.INVALID_IMAGE_FILE.getMessage());
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidRequestException(ExceptionMessage.INVALID_IMAGE_FILE.getMessage());
        }
    }

    private String extractExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex >= 0 && dotIndex < originalFilename.length() - 1) {
                return originalFilename.substring(dotIndex).toLowerCase();
            }
        }

        return switch (Objects.requireNonNull(file.getContentType())) {
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
    }
}
