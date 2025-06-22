package com.aditya.ecommerce.service;

import com.aditya.ecommerce.dto.OrderItemResponse;
import com.aditya.ecommerce.dto.OrderResponse;
import com.aditya.ecommerce.entity.CartItem;
import com.aditya.ecommerce.entity.Order;
import com.aditya.ecommerce.entity.OrderItem;
import com.aditya.ecommerce.entity.Product;
import com.aditya.ecommerce.entity.User;
import com.aditya.ecommerce.repository.CartRepository;
import com.aditya.ecommerce.repository.OrderRepository;
import com.aditya.ecommerce.repository.ProductRepository;
import com.aditya.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Transactional
    public OrderResponse placeOrder(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CartItem> cartItems = cartRepository.findByUser(user);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        double totalAmount = 0;

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            if (product.getQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .priceAtPurchase(product.getPrice())
                    .build();

            orderItems.add(orderItem);
            totalAmount += product.getPrice() * cartItem.getQuantity();
        }

        Order order = Order.builder()
                .user(user)
                .items(orderItems)
                .totalAmount(totalAmount)
                .placedAt(LocalDateTime.now())
                .build();

        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }

        orderRepository.save(order);
        cartRepository.deleteByUser(user);

        return OrderResponse.builder()
                .orderId(order.getId())
                .placedAt(order.getPlacedAt())
                .totalAmount(order.getTotalAmount())
                .items(orderItems.stream().map(item -> OrderItemResponse.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .imageUrl(item.getProduct().getImageUrl())
                        .quantity(item.getQuantity())
                        .priceAtPurchase(item.getPriceAtPurchase())
                        .build()).toList())
                .build();
    }

    public List<OrderResponse> getOrdersByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> orders = orderRepository.findByUser(user);

        return orders.stream().map(order ->
                OrderResponse.builder()
                        .orderId(order.getId())
                        .placedAt(order.getPlacedAt())
                        .totalAmount(order.getTotalAmount())
                        .items(order.getItems().stream().map(item -> OrderItemResponse.builder()
                                .productId(item.getProduct().getId())
                                .productName(item.getProduct().getName())
                                .imageUrl(item.getProduct().getImageUrl())
                                .quantity(item.getQuantity())
                                .priceAtPurchase(item.getPriceAtPurchase())
                                .build()).toList())
                        .build()
        ).toList();
    }
}
