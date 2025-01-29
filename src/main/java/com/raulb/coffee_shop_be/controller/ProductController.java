package com.raulb.coffee_shop_be.controller;


import com.raulb.coffee_shop_be.dto.ProductDto;
import com.raulb.coffee_shop_be.dto.ProductResponse;
import com.raulb.coffee_shop_be.service.api.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin(exposedHeaders = {"Content-Disposition"})
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public void addProduct(@ModelAttribute ProductDto productDto) throws IOException {
        System.out.println(productDto);
        productService.addProduct(productDto);
    }

    @PutMapping(path = "/{productId}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public void updateProduct(@PathVariable("productId") Integer productId, @ModelAttribute ProductDto productDto) throws IOException {
        System.out.println(productDto);
        System.out.println(productId);
        productService.updateProduct(productId, productDto);
    }

    @DeleteMapping("/{productId}")
    public void delete(@PathVariable("productId") Integer productId) {
        productService.delete(productId);
    }

    @GetMapping
    public List<ProductResponse> getProducts() {
        return productService.getProducts();
    }

    @GetMapping("/{productId}")
    public ProductResponse getProductsById(@PathVariable("productId") Integer productId) {
        return productService.getProductsById(productId);
    }
}
