package com.raulb.coffee_shop_be.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public record ProductDto(
        String name,
        Double price,
        MultipartFile mainImage,
        List<MultipartFile> galleryImages,
        String description,
        String origin,
        String roastLevel
) {
}
