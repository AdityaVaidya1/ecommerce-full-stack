package com.aditya.ecommerce.controller;

import com.aditya.ecommerce.dto.ProductDto;
import com.aditya.ecommerce.dto.ProductFilterRequest;
import com.aditya.ecommerce.service.ProductService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/seller/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // ✅ Add a product (with optional image)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addProduct(
            @RequestParam @NotBlank String name,
            @RequestParam(required = false) String description,
            @RequestParam @NotNull Double price,
            @RequestParam @NotNull Integer quantity,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam @NotNull Long categoryId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        System.out.println("📥 [ADD] Product request received from: " + (userDetails != null ? userDetails.getUsername() : "null"));

        productService.addProduct(
                name,
                description,
                price,
                quantity,
                image,
                userDetails.getUsername(),
                categoryId
        );
        return ResponseEntity.ok("Product added successfully");
    }

    // ✅ Get all products by current seller (returns DTOs)
    @GetMapping
    public ResponseEntity<List<ProductDto>> getSellerProducts(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        System.out.println("📦 [GET] Seller products requested by: " + (userDetails != null ? userDetails.getUsername() : "null"));

        List<ProductDto> products = productService.getSellerProducts(userDetails.getUsername());
        return ResponseEntity.ok(products);
    }

    // ✅ Deletes product
    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        System.out.println("❌ [DELETE] Product ID: " + productId + " requested by: " + (userDetails != null ? userDetails.getUsername() : "null"));

        productService.deleteProduct(productId, userDetails.getUsername());
        return ResponseEntity.ok("Product deleted successfully");
    }

    // ✅ Updates product
    @PutMapping("/{productId}")
    public ResponseEntity<String> updateProduct(
            @PathVariable Long productId,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") double price,
            @RequestParam("quantity") int quantity,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("categoryId") Long categoryId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        System.out.println("✏️ [UPDATE] Product ID: " + productId + " by: " + (userDetails != null ? userDetails.getUsername() : "null"));

        productService.updateProduct(
                productId,
                name,
                description,
                price,
                quantity,
                image,
                userDetails.getUsername(),
                categoryId
        );
        return ResponseEntity.ok("Product updated successfully");
    }

    // ✅ Get a single product by ID (returns DTO)
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProduct(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        System.out.println("🔍 [FETCH] Product ID: " + productId + " by: " + (userDetails != null ? userDetails.getUsername() : "null"));

        ProductDto product = productService.getProductById(productId, userDetails.getUsername());
        return ResponseEntity.ok(product);
    }

    // ✅ New: Filter/Search Products (for seller, returns DTOs)
    @PostMapping("/filter")
    public ResponseEntity<List<ProductDto>> filterSellerProducts(
            @RequestBody ProductFilterRequest filterRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        System.out.println("🔎 [FILTER] Products by: " + (userDetails != null ? userDetails.getUsername() : "null"));

        List<ProductDto> products = productService.filterSellerProducts(userDetails.getUsername(), filterRequest);
        return ResponseEntity.ok(products);
    }
}
