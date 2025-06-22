package com.aditya.ecommerce.service;

import com.aditya.ecommerce.entity.Role;
import com.aditya.ecommerce.entity.User;
import com.aditya.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public List<User> getPendingSellers() {
        return userRepository.findByRoleAndEnabledTrueAndApprovedFalse(Role.SELLER);
    }

    public void approveSeller(Long sellerId) {
        User user = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));
        user.setApproved(true);
        userRepository.save(user);
    }

    public void rejectSeller(Long sellerId) {
        User user = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        // Optional: add more checks here if needed (role = SELLER, etc.)
        userRepository.delete(user);
    }

}
