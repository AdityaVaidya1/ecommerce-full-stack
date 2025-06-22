package com.aditya.ecommerce.config;

import com.aditya.ecommerce.entity.Category;
import com.aditya.ecommerce.repository.CategoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final CategoryRepository categoryRepository;

    @PostConstruct
    public void setupDefaultCategory() {
        if (!categoryRepository.existsByName("Miscellaneous")) {
            Category defaultCategory = Category.builder()
                    .name("Miscellaneous")
                    .build();

            categoryRepository.save(defaultCategory);
            System.out.println("✅ 'Miscellaneous' category created");
        }
    }
}
