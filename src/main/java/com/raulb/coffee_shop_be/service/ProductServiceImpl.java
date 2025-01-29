package com.raulb.coffee_shop_be.service;

import com.raulb.coffee_shop_be.domain.Product;
import com.raulb.coffee_shop_be.dto.ProductDto;
import com.raulb.coffee_shop_be.dto.ProductResponse;
import com.raulb.coffee_shop_be.repository.ProductRepository;
import com.raulb.coffee_shop_be.service.api.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    @Value("${images.baseUrl}")
    private String baseUrl;

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
        String mainImageUrl = baseUrl + productDto.mainImage().getOriginalFilename();
        FileService.saveFile(productDto.mainImage(), baseUrl);
        StringBuilder stringBuilder = new StringBuilder();
        for (var image : productDto.galleryImages()) {
            String url = FileService.saveFile(image, baseUrl);
            stringBuilder.append(url).append(";");
        }

        Product product = Product.builder()
                .name(productDto.name())
                .description(productDto.description())
                .origin(productDto.origin())
                .price(productDto.price())
                .roastLevel(productDto.roastLevel())
                .mainImageUrl(mainImageUrl)
                .galleryImagesUrls(stringBuilder.toString())
                .build();

        productRepository.save(product);
    }

    @Override
    public void updateProduct(Integer id, ProductDto newProduct) {
        Optional<Product> oldProductOptional = productRepository.findById(id);
        oldProductOptional.ifPresent(oldProduct -> {
            String mainImageUrl = oldProduct.getMainImageUrl();
            if (newProduct.mainImage() != null) {
                mainImageUrl = baseUrl + newProduct.mainImage().getOriginalFilename();
                try {
                    FileService.saveFile(newProduct.mainImage(), baseUrl);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            StringBuilder stringBuilder = new StringBuilder(oldProduct.getGalleryImagesUrls());
            if (newProduct.galleryImages() != null) {
                stringBuilder = new StringBuilder();
                for (var image : newProduct.galleryImages()) {
                    String url;
                    try {
                        url = FileService.saveFile(image, baseUrl);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    stringBuilder.append(url).append(";");
                }
            }

            Product product = Product.builder()
                    .id(id)
                    .name(newProduct.name() != null ? newProduct.name() : oldProduct.getName())
                    .description(newProduct.description() != null ? newProduct.description() : oldProduct.getDescription())
                    .origin(newProduct.origin() != null ? newProduct.origin() : oldProduct.getOrigin())
                    .price(newProduct.price() != null ? newProduct.price() : oldProduct.getPrice())
                    .roastLevel(newProduct.roastLevel() != null ? newProduct.roastLevel() : oldProduct.getRoastLevel())
                    .mainImageUrl(mainImageUrl)
                    .galleryImagesUrls(stringBuilder.toString())
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
                .map(this::mapFromProductToProductResponse).orElse(null);
    }

    private ProductResponse mapFromProductToProductResponse(Product p) {
        List<byte[]> galleryImages = getGalleryImages(p.getGalleryImagesUrls());
        return new ProductResponse(p.getId(), p.getName(), p.getPrice(),
                FileService.getBytesAsFile(p.getMainImageUrl()), galleryImages,
                p.getDescription(), p.getOrigin(), p.getRoastLevel()
        );
    }

    private List<byte[]> getGalleryImages(String galleryImagesUrls) {
        List<String> galleryImagesUrlsList = Arrays.stream(galleryImagesUrls.split(";")).toList();
        return galleryImagesUrlsList.stream()
                .map(FileService::getBytesAsFile)
                .toList();
    }
}
