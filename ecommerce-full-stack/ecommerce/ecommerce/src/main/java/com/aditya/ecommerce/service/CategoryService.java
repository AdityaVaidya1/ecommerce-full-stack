package com.aditya.ecommerce.service;

import com.aditya.ecommerce.entity.Category;
import com.aditya.ecommerce.entity.Product;
import com.aditya.ecommerce.repository.CategoryRepository;
import com.aditya.ecommerce.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    private static final String DEFAULT_CATEGORY = "Miscellaneous";

    // ✅ Create a new category (if it doesn't already exist)
    public Category createCategory(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new IllegalArgumentException("Category already exists");
        }
        return categoryRepository.save(Category.builder().name(name).build());
    }

    // ✅ Get all categories
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // ✅ Delete category and reassign products to "Miscellaneous"
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Category fallbackCategory = getOrCreateMiscCategory();

        List<Product> affectedProducts = productRepository.findByCategory(category);
        for (Product product : affectedProducts) {
            product.setCategory(fallbackCategory);
        }
        productRepository.saveAll(affectedProducts);

        categoryRepository.delete(category);
    }

    // ✅ Internal: get or create "Miscellaneous"
    public Category getOrCreateMiscCategory() {
        return categoryRepository.findByName(DEFAULT_CATEGORY)
                .orElseGet(() -> categoryRepository.save(Category.builder().name(DEFAULT_CATEGORY).build()));
    }

    // ✅ Internal: find by name (used when assigning category from frontend)
    public Optional<Category> findByName(String name) {
        return categoryRepository.findByName(name);
    }

    // ✅ Internal: find by ID
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }
}
