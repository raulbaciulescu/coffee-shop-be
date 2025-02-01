package com.raulb.coffee_shop_be.dto;

import java.util.List;


public record ProductResponse(
        Integer id,
        String name,
        Double price,
        String mainImage,
        List<String> galleryImages,
        String description,
        String origin,
        String roastLevel
) {
}
