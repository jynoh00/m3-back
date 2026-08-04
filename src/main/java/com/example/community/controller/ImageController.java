package com.example.community.controller;

import com.example.community.common.ResponseFormat;
import com.example.community.common.ResponseMessage;
import com.example.community.dto.ImageUploadResponseDTO;
import com.example.community.service.ImageStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ImageController { // 사용자 이미지 업로드 요청 처리
    private final ImageStorageService imageStorageService;

    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadUserImage(@RequestParam("image") MultipartFile image) {
        String imagePath = imageStorageService.storeUserImage(image);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseFormat.of(ResponseMessage.IMAGE_UPLOAD_SUCCESS.getMessage(), new ImageUploadResponseDTO(imagePath))
        );
    }
}