package com.example.community.dto.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ImageUploadResponseDTO {
    @JsonProperty("image_path")
    private final String imagePath;

    public ImageUploadResponseDTO(String imagePath) {
        this.imagePath = imagePath;
    }
}
