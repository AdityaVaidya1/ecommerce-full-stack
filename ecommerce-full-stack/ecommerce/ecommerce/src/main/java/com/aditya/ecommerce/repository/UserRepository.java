package com.aditya.ecommerce.repository;

import com.aditya.ecommerce.entity.User;
import com.aditya.ecommerce.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByRoleAndEnabledTrueAndApprovedFalse(Role role);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
