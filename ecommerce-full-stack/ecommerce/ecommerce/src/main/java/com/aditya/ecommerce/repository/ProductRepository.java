package com.aditya.ecommerce.repository;

import com.aditya.ecommerce.entity.Product;
import com.aditya.ecommerce.entity.User;
import com.aditya.ecommerce.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    List<Product> findBySeller(User seller);

    List<Product> findByCategory(Category category);

    List<Product> findByQuantityGreaterThan(int quantity);
}
