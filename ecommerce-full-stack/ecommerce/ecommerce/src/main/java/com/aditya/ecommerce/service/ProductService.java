package com.aditya.ecommerce.service;

import com.aditya.ecommerce.dto.ProductDto;
import com.aditya.ecommerce.dto.ProductFilterRequest;
import com.aditya.ecommerce.entity.Category;
import com.aditya.ecommerce.entity.Product;
import com.aditya.ecommerce.entity.User;
import com.aditya.ecommerce.repository.CategoryRepository;
import com.aditya.ecommerce.repository.ProductRepository;
import com.aditya.ecommerce.repository.UserRepository;
import com.aditya.ecommerce.specification.ProductSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public void addProduct(String name, String description, double price, int quantity,
                           MultipartFile image, String username, Long categoryId) {

        User seller = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        String imageUrl = (image != null && !image.isEmpty()) ? saveImage(image) : null;

        Product product = Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .quantity(quantity)
                .imageUrl(imageUrl)
                .seller(seller)
                .category(category)
                .build();

        productRepository.save(product);
    }

    public List<ProductDto> getSellerProducts(String username) {
        User seller = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return productRepository.findBySeller(seller)
                .stream()
                .map(ProductDto::new)
                .toList();
    }

    private String saveImage(MultipartFile image) {
        try {
            String originalFilename = image.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            String filename = UUID.randomUUID() + extension;

            Path uploadDir = Paths.get("C:/Users/Admin/Desktop/projects_for_SAP/uploads");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            Path filePath = uploadDir.resolve(filename);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("Saved product image: " + filePath.toAbsolutePath());

            return "/images/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
        }
    }

    public void deleteProduct(Long productId, String username) {
        User seller = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new RuntimeException("You are not authorized to delete this product");
        }

        String imageUrl = product.getImageUrl();
        if (imageUrl != null && imageUrl.startsWith("/images/")) {
            String filename = imageUrl.replace("/images/", "");
            Path imagePath = Paths.get("C:/Users/Admin/Desktop/projects_for_SAP/uploads", filename);
            try {
                Files.deleteIfExists(imagePath);
            } catch (IOException e) {
                System.err.println("Failed to delete image: " + imagePath);
            }
        }

        productRepository.delete(product);
    }

    public void updateProduct(Long productId, String name, String description,
                              double price, int quantity, MultipartFile image,
                              String username, Long categoryId) {

        User seller = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new RuntimeException("You are not authorized to update this product");
        }

        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setQuantity(quantity);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        product.setCategory(category);

        if (image != null && !image.isEmpty()) {
            String oldImage = product.getImageUrl();
            if (oldImage != null && oldImage.startsWith("/images/")) {
                String filename = oldImage.replace("/images/", "");
                Path oldPath = Paths.get("C:/Users/Admin/Desktop/projects_for_SAP/uploads", filename);
                try {
                    Files.deleteIfExists(oldPath);
                } catch (IOException e) {
                    System.err.println("Failed to delete old image: " + oldPath);
                }
            }

            try {
                String originalFilename = image.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
                String filename = UUID.randomUUID() + extension;

                Path uploadDir = Paths.get("C:/Users/Admin/Desktop/projects_for_SAP/uploads");
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                Path filePath = uploadDir.resolve(filename);
                Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                product.setImageUrl("/images/" + filename);

            } catch (IOException e) {
                throw new RuntimeException("Failed to upload new image", e);
            }
        }

        productRepository.save(product);
    }

    public ProductDto getProductById(Long productId, String username) {
        User seller = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new RuntimeException("You are not authorized to view this product");
        }

        return new ProductDto(product);
    }

    public List<ProductDto> filterSellerProducts(String username, ProductFilterRequest filterRequest) {
        User seller = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Specification<Product> spec = ProductSpecifications.withFilters(filterRequest)
                .and((root, query, cb) -> cb.equal(root.get("seller"), seller));

        return productRepository.findAll(spec)
                .stream()
                .map(ProductDto::new)
                .toList();
    }

    public List<ProductDto> filterProducts(ProductFilterRequest request) {
        Specification<Product> spec = ProductSpecifications.withFilters(request);
        return productRepository.findAll(spec)
                .stream()
                .map(ProductDto::new)
                .toList();
    }

    public List<ProductDto> getPublicProducts() {
        return productRepository.findByQuantityGreaterThan(0)
                .stream()
                .map(ProductDto::new)
                .toList();
    }
}
