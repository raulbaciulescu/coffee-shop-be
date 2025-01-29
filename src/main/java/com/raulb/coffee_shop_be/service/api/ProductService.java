package com.raulb.coffee_shop_be.service.api;

import com.raulb.coffee_shop_be.dto.ProductDto;
import com.raulb.coffee_shop_be.dto.ProductResponse;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    List<ProductResponse> getProducts();

    void updateProduct(Integer id, ProductDto product) throws IOException;

    void delete(Integer productId);

    ProductResponse getProductsById(Integer productId);

    void addProduct(ProductDto productDto) throws IOException;
}
