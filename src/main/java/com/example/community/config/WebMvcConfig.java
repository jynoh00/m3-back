package com.example.community.config;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        Path uploadPath = Path.of(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadPath + "/", "classpath:/static/images/");
    }
}