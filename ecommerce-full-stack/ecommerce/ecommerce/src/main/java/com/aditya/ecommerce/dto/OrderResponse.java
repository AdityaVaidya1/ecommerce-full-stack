package com.aditya.ecommerce.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long orderId;
    private LocalDateTime placedAt;
    private double totalAmount;
    private List<OrderItemResponse> items;
}
