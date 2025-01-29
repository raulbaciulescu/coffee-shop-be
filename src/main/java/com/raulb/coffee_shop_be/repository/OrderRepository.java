package com.raulb.coffee_shop_be.repository;


import com.raulb.coffee_shop_be.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
