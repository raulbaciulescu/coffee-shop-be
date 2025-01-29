package com.raulb.coffee_shop_be.domain;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private Double price;
    private String mainImageUrl;
    private String galleryImagesUrls;
    private String description;
    private String origin;
    private String roastLevel;
}
