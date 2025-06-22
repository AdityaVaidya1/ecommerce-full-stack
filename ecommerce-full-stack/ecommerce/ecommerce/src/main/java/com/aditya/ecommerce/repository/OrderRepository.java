package com.aditya.ecommerce.repository;

import com.aditya.ecommerce.entity.Order;
import com.aditya.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}
