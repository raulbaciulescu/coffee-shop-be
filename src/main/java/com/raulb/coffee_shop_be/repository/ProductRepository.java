package com.raulb.coffee_shop_be.repository;


import com.raulb.coffee_shop_be.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductRepository extends JpaRepository<Product, Integer> {
    void deleteById(Integer id);
}
