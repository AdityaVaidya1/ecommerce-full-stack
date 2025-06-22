package com.aditya.ecommerce.repository;

import com.aditya.ecommerce.entity.CartItem;
import com.aditya.ecommerce.entity.User;
import com.aditya.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUser(User user);

    Optional<CartItem> findByUserAndProduct(User user, Product product);

    void deleteByUser(User user);
}
