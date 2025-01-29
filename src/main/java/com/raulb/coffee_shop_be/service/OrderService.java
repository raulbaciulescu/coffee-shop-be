package com.raulb.coffee_shop_be.service;

import com.raulb.coffee_shop_be.domain.Order;
import com.raulb.coffee_shop_be.dto.OrderDto;
import com.raulb.coffee_shop_be.dto.OrderItemDto;
import com.raulb.coffee_shop_be.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public List<OrderDto> getAllOrders() {
        // Fetch all orders from the repository
        List<Order> orders = orderRepository.findAll();

        // Map entities to DTOs
        return orders.stream()
                .map(this::mapToOrderDto)
                .collect(Collectors.toList());
    }

    private OrderDto mapToOrderDto(Order order) {
        // Map OrderItem to OrderItemDto
        List<OrderItemDto> orderItems = order.getOrderItems().stream()
                .map(item -> OrderItemDto.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .build())
                .collect(Collectors.toList());

        // Map Order to OrderDto
        return OrderDto.builder()
                .id(order.getId())
                .customerName(order.getCustomerName())
                .address(order.getAddress())
                .phone(order.getPhone())
                .total(order.getTotal())
                .createdAt(order.getCreatedAt())
                .status(order.getStatus())
                .items(orderItems)
                .build();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }
    public Order updateOrder(Long id, Order updatedOrder) {
        Order existingOrder = getOrderById(id);
        existingOrder.setStatus(updatedOrder.getStatus());
//        existingOrder.setCustomer(updatedOrder.getCustomer());
        existingOrder.setOrderItems(updatedOrder.getOrderItems());
        return orderRepository.save(existingOrder);
    }

    public void deleteOrder(Long id) {
        Order existingOrder = getOrderById(id);
        orderRepository.delete(existingOrder);
    }

    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        order.setStatus(status);

        orderRepository.save(order);
    }
}