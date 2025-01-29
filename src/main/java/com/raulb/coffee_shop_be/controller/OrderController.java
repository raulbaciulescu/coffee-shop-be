package com.raulb.coffee_shop_be.controller;


import com.raulb.coffee_shop_be.domain.Order;
import com.raulb.coffee_shop_be.dto.OrderDto;
import com.raulb.coffee_shop_be.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@CrossOrigin(exposedHeaders = {"Content-Disposition"})
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        return ResponseEntity.ok(orderService.createOrder(order));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order updatedOrder) {
        return ResponseEntity.ok(orderService.updateOrder(id, updatedOrder));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{orderId}/{status}")
    public ResponseEntity<String> updateOrderStatus(
            @PathVariable Long orderId,
            @PathVariable String status) {
        orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok("Order status updated successfully");
    }
}
