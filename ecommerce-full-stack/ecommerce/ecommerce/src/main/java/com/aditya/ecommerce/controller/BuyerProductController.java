package com.aditya.ecommerce.controller;

import com.aditya.ecommerce.dto.ProductDto;
import com.aditya.ecommerce.dto.ProductFilterRequest;
import com.aditya.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buyer/products") // 👈 shared buyer URL prefix
@RequiredArgsConstructor
public class BuyerProductController {

    private final ProductService productService;

    // ✅ Public product listing for buyers (must be logged in)
    @GetMapping("/product-list")
    public ResponseEntity<List<ProductDto>> getPublicProducts() {
        List<ProductDto> products = productService.getPublicProducts();
        return ResponseEntity.ok(products);
    }

    // ✅ Filter/Search for buyer-side products
    @PostMapping("/product-filter")
    public ResponseEntity<List<ProductDto>> filterPublicProducts(@RequestBody ProductFilterRequest request) {
        System.out.println("🔍 [BUYER FILTER] Filtering products with request: " + request);
        List<ProductDto> filtered = productService.filterProducts(request);
        return ResponseEntity.ok(filtered);
    }
}
