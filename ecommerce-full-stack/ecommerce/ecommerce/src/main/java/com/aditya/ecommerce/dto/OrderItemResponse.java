package com.aditya.ecommerce.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemResponse {
    private Long productId;
    private String productName;
    private String imageUrl;
    private int quantity;
    private double priceAtPurchase;
}
