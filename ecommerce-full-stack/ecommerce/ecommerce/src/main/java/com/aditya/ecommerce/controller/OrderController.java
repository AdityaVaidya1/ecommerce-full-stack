package com.aditya.ecommerce.controller;

import com.aditya.ecommerce.dto.OrderResponse;
import com.aditya.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/place")
    public ResponseEntity<OrderResponse> placeOrder(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        OrderResponse response = orderService.placeOrder(username);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<OrderResponse>> getMyOrders(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        List<OrderResponse> orders = orderService.getOrdersByUsername(username);
        return ResponseEntity.ok(orders);
    }
}
