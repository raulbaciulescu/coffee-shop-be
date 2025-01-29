package com.raulb.coffee_shop_be.dto;

import java.util.List;


public record ProductResponse(
        Integer id,
        String name,
        Double price,
        byte[] mainImage,
        List<byte[]> galleryImages,
        String description,
        String origin,
        String roastLevel
) {
}
