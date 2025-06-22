package com.aditya.ecommerce.dto;

import com.aditya.ecommerce.entity.Product;
import lombok.Data;

@Data
public class ProductDto {

    private Long id;
    private String name;
    private String description;
    private double price;
    private int quantity;
    private String imageUrl;
    private Long categoryId;
    private String categoryName;

    public ProductDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.quantity = product.getQuantity();
        this.imageUrl = product.getImageUrl();
        this.categoryId = product.getCategory().getId();
        this.categoryName = product.getCategory().getName();
    }
}
