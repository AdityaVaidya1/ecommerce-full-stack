package com.aditya.ecommerce.controller;

import com.aditya.ecommerce.dto.CategoryDto;
import com.aditya.ecommerce.entity.Category;
import com.aditya.ecommerce.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()") // ✅ Any logged-in user can access
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        List<CategoryDto> dtos = categories.stream()
                .map(category -> new CategoryDto(category.getId(), category.getName()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}
