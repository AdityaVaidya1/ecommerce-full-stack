package com.aditya.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for filtering products based on optional parameters like name, category, price range, and seller.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilterRequest {
    private String name;         // partial or full product name
    private Long categoryId;     // optional: filter by category
    private Double minPrice;     // optional: inclusive minimum price
    private Double maxPrice;     // optional: inclusive maximum price
    private Long sellerId;       // optional: for seller-specific filtering
}
