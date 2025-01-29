package com.raulb.coffee_shop_be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    private Long id;
    private String customerName;
    private String address;
    private String phone;
    private String total;
    private Date createdAt;
    private String status;
    private List<OrderItemDto> items;
}
