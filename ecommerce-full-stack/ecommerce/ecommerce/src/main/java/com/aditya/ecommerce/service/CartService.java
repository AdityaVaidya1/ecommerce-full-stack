package com.aditya.ecommerce.service;

import com.aditya.ecommerce.dto.CartItemResponse;
import com.aditya.ecommerce.entity.CartItem;
import com.aditya.ecommerce.entity.Product;
import com.aditya.ecommerce.entity.User;
import com.aditya.ecommerce.repository.CartRepository;
import com.aditya.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public List<CartItemResponse> getCartForUser(User user) {
        List<CartItem> items = cartRepository.findByUser(user);

        return items.stream().map(item -> {
            Product product = item.getProduct();
            return CartItemResponse.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .imageUrl(product.getImageUrl())
                    .price(product.getPrice())
                    .quantityInCart(item.getQuantity())
                    .quantityAvailable(product.getQuantity())
                    .build();
        }).collect(Collectors.toList());
    }

    public void addToCart(User user, Long productId, int quantityToAdd) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getQuantity() <= 0) {
            throw new RuntimeException("Product is out of stock");
        }

        CartItem cartItem = cartRepository.findByUserAndProduct(user, product)
                .orElse(CartItem.builder()
                        .user(user)
                        .product(product)
                        .quantity(0)
                        .build());

        int currentQuantityInCart = cartItem.getQuantity();
        int newTotalQuantity = currentQuantityInCart + quantityToAdd;

        if (newTotalQuantity > product.getQuantity()) {
            throw new RuntimeException("Cannot add more than available stock");
        }

        cartItem.setQuantity(newTotalQuantity);
        cartRepository.save(cartItem);
    }

    public void removeFromCart(User user, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        cartRepository.findByUserAndProduct(user, product)
                .ifPresent(cartRepository::delete);
    }

    @Transactional
    public void clearCart(User user) {
        cartRepository.deleteByUser(user);
    }
}
