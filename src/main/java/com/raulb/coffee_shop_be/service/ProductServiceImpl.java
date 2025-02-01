package com.raulb.coffee_shop_be.service;

import com.raulb.coffee_shop_be.domain.Product;
import com.raulb.coffee_shop_be.dto.ProductDto;
import com.raulb.coffee_shop_be.dto.ProductResponse;
import com.raulb.coffee_shop_be.repository.ProductRepository;
import com.raulb.coffee_shop_be.service.api.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final AzureBlobStorageService azureBlobStorageService;

    @Override
    public List<ProductResponse> getProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream()
                .filter(p -> p.getMainImageUrl() != null)
                .map(this::mapFromProductToProductResponse)
                .toList();
    }

    @Override
    public void addProduct(ProductDto productDto) throws IOException {
        String mainImageUrl = azureBlobStorageService.uploadFile(productDto.mainImage());

        StringBuilder galleryUrls = new StringBuilder();
        for (var image : productDto.galleryImages()) {
            String url = azureBlobStorageService.uploadFile(image);
            galleryUrls.append(url).append(";");
        }

        Product product = Product.builder()
                .name(productDto.name())
                .description(productDto.description())
                .origin(productDto.origin())
                .price(productDto.price())
                .roastLevel(productDto.roastLevel())
                .mainImageUrl(mainImageUrl)
                .galleryImagesUrls(galleryUrls.toString())
                .build();

        productRepository.save(product);
    }

    @Override
    public void updateProduct(Integer id, ProductDto newProduct) {
        Optional<Product> oldProductOptional = productRepository.findById(id);
        oldProductOptional.ifPresent(oldProduct -> {
            String mainImageUrl = oldProduct.getMainImageUrl();

            // Update main image if provided
            if (newProduct.mainImage() != null) {
                try {
                    mainImageUrl = azureBlobStorageService.uploadFile(newProduct.mainImage());
                } catch (IOException e) {
                    throw new RuntimeException("Failed to upload main image", e);
                }
            }

            // Update gallery images if provided
            StringBuilder galleryUrls = new StringBuilder(oldProduct.getGalleryImagesUrls());
            if (newProduct.galleryImages() != null) {
                galleryUrls = new StringBuilder();
                for (var image : newProduct.galleryImages()) {
                    try {
                        String url = azureBlobStorageService.uploadFile(image);
                        galleryUrls.append(url).append(";");
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to upload gallery image", e);
                    }
                }
            }

            // Update product details
            Product product = Product.builder()
                    .id(id)
                    .name(newProduct.name() != null ? newProduct.name() : oldProduct.getName())
                    .description(newProduct.description() != null ? newProduct.description() : oldProduct.getDescription())
                    .origin(newProduct.origin() != null ? newProduct.origin() : oldProduct.getOrigin())
                    .price(newProduct.price() != null ? newProduct.price() : oldProduct.getPrice())
                    .roastLevel(newProduct.roastLevel() != null ? newProduct.roastLevel() : oldProduct.getRoastLevel())
                    .mainImageUrl(mainImageUrl)
                    .galleryImagesUrls(galleryUrls.toString())
                    .build();

            productRepository.save(product);
        });
    }

    @Override
    public void delete(Integer productId) {
        productRepository.deleteById(productId);
    }

    @Override
    public ProductResponse getProductsById(Integer productId) {
        return productRepository.findById(productId)
                .map(this::mapFromProductToProductResponse)
                .orElse(null);
    }

    private ProductResponse mapFromProductToProductResponse(Product product) {
        List<String> galleryImages = Arrays.stream(product.getGalleryImagesUrls().split(";"))
                .toList();

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getMainImageUrl(),
                galleryImages,
                product.getDescription(),
                product.getOrigin(),
                product.getRoastLevel()
        );
    }
}