package com.raulb.coffee_shop_be.service;

import com.raulb.coffee_shop_be.domain.Order;
import com.raulb.coffee_shop_be.domain.OrderItem;
import com.raulb.coffee_shop_be.dto.OrderDto;
import com.raulb.coffee_shop_be.dto.OrderItemDto;
import com.raulb.coffee_shop_be.repository.OrderItemRepository;
import com.raulb.coffee_shop_be.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

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

    @Transactional
    public OrderDto createOrder(OrderDto orderDto) {
        Order order = Order.builder()
                .customerName(orderDto.getCustomerName())
                .phone(orderDto.getPhone())
                .address(orderDto.getAddress())
                .total(orderDto.getTotal())
                .status("pending")
                .createdAt(new Date())
                .build();

        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemDto itemDto : orderDto.getItems()) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .quantity(itemDto.getQuantity())
                    .price(itemDto.getPrice())
                    .name(itemDto.getName())
                    .build();
            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);
        order = orderRepository.save(order);
        return mapToOrderDto(order);
    }

    public Order updateOrder(Long id, Order updatedOrder) {
        Order existingOrder = getOrderById(id);
        existingOrder.setStatus(updatedOrder.getStatus());
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