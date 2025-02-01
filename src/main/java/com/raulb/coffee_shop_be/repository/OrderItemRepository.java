package com.raulb.coffee_shop_be.repository;


import com.raulb.coffee_shop_be.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
