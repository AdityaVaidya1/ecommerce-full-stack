package com.aditya.ecommerce.service;

import com.aditya.ecommerce.dto.LoginRequest;
import com.aditya.ecommerce.dto.LoginResponse;
import com.aditya.ecommerce.dto.SignupRequest;
import com.aditya.ecommerce.entity.Role;
import com.aditya.ecommerce.entity.User;
import com.aditya.ecommerce.entity.VerificationToken;
import com.aditya.ecommerce.repository.UserRepository;
import com.aditya.ecommerce.repository.VerificationTokenRepository;
import com.aditya.ecommerce.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public String registerUser(SignupRequest request) {
        if (request.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("Cannot create admin account via signup.");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            return "Username is already taken.";
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return "Email is already registered.";
        }

        Role role = request.getRole();

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .enabled(false)
                .approved(role == Role.BUYER)
                .build();

        userRepository.save(user);
        createAndSendVerificationToken(user);

        return "Registration successful. Please check your email to verify your account.";
    }

    private void createAndSendVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(user, token);
        tokenRepository.save(verificationToken);
        emailService.sendVerificationEmail(user.getEmail(), token);
    }

    public LoginResponse loginAndGenerateToken(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());

        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Incorrect password");
        }

        if (!user.isEnabled()) {
            throw new RuntimeException("Email not verified.");
        }

        if (user.getRole() == Role.SELLER && !user.isApproved()) {
            throw new RuntimeException("Seller account not approved by admin.");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return new LoginResponse("Bearer " + token, user.getRole(), user.getUsername());
    }

    public void verifyUser(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        User user = verificationToken.getUser();
        user.setEnabled(true);

        if (user.getRole() == Role.BUYER) {
            user.setApproved(true);
        }

        userRepository.save(user);
        tokenRepository.delete(verificationToken);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }


}
