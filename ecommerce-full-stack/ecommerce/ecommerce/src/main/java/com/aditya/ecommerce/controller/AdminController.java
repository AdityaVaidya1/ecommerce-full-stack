package com.aditya.ecommerce.controller;

import com.aditya.ecommerce.dto.CategoryDto;
import com.aditya.ecommerce.entity.Category;
import com.aditya.ecommerce.entity.User;
import com.aditya.ecommerce.service.AdminService;
import com.aditya.ecommerce.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final CategoryService categoryService;

    // ================= Seller Approval ================= //

    @GetMapping("/pending-sellers")
    public ResponseEntity<List<User>> getPendingSellers() {
        return ResponseEntity.ok(adminService.getPendingSellers());
    }

    @PutMapping("/approve-seller/{id}")
    public ResponseEntity<String> approveSeller(@PathVariable Long id) {
        adminService.approveSeller(id);
        return ResponseEntity.ok("Seller approved");
    }

    @DeleteMapping("/reject-seller/{id}")
    public ResponseEntity<String> rejectSeller(@PathVariable Long id) {
        adminService.rejectSeller(id);
        return ResponseEntity.ok("Seller rejected and deleted");
    }

    // ================= Category Management ================= //

    // ✅ Get all categories as DTOs (to avoid lazy loading issues)
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<CategoryDto> dtos = categoryService.getAllCategories()
                .stream()
                .map(CategoryDto::new)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    // ✅ Create category
    @PostMapping("/categories")
    public ResponseEntity<String> createCategory(@RequestBody @Valid Category request) {
        categoryService.createCategory(request.getName());
        return ResponseEntity.ok("Category created successfully");
    }

    // ✅ Delete category and reassign products
    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok("Category deleted and products reassigned");
    }
}
