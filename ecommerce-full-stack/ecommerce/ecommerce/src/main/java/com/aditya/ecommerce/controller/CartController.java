package com.aditya.ecommerce.controller;

import com.aditya.ecommerce.dto.CartItemResponse;
import com.aditya.ecommerce.entity.User;
import com.aditya.ecommerce.service.CartService;
import com.aditya.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<CartItemResponse>> getCart(Principal principal) {
        User user = userService.getUserByUsername(principal.getName()); // ✅ FIXED
        return ResponseEntity.ok(cartService.getCartForUser(user));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestParam Long productId,
                                       @RequestParam int quantity,
                                       Principal principal) {
        try {
            User user = userService.getUserByUsername(principal.getName());
            cartService.addToCart(user, productId, quantity);
            return ResponseEntity.ok().build();
        } catch (RuntimeException ex) {
            // Return 400 with error message
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/remove")
    public ResponseEntity<Void> removeFromCart(@RequestParam Long productId,
                                               Principal principal) {
        User user = userService.getUserByUsername(principal.getName()); // ✅ FIXED
        cartService.removeFromCart(user, productId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/clear")
    public ResponseEntity<Void> clearCart(Principal principal) {
        User user = userService.getUserByUsername(principal.getName()); // ✅ FIXED
        cartService.clearCart(user);
        return ResponseEntity.ok().build();
    }
}
