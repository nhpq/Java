package com.example.Buoi2.services;

import com.example.Buoi2.entity.CartItem;
import com.example.Buoi2.entity.Product;
import com.example.Buoi2.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;

@Service
@SessionScope
public class CartService {
    private List<CartItem> cartItems = new ArrayList<>();

    @Autowired
    private ProductRepository productRepository;

    public void addToCart(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        // Check if there's enough stock
        if (product.getQuality() < quantity) {
            throw new IllegalArgumentException("Not enough stock for product: " + product.getName());
        }

        // Check if the product is already in the cart
        for (CartItem item : cartItems) {
            if (item.getProduct().getId().equals(productId)) {
                int newQuantity = item.getQuantity() + quantity;
                if (newQuantity > product.getQuality()) {
                    throw new IllegalArgumentException("Not enough stock for product: " + product.getName());
                }
                item.setQuantity(newQuantity);
                return;
            }
        }

        // Add new cart item
        cartItems.add(new CartItem(product, quantity));
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void removeFromCart(Long productId) {
        cartItems.removeIf(item -> item.getProduct().getId().equals(productId));
    }

    public void updateCartItem(Long productId, int quantity) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId().equals(productId)) {
                if (quantity > item.getProduct().getQuality()) {
                    throw new IllegalArgumentException("Not enough stock for product: " + item.getProduct().getName());
                }
                item.setQuantity(quantity);
                return;
            }
        }
        throw new IllegalArgumentException("Product not found in cart: " + productId);
    }

    public void clearCart() {
        cartItems.clear();
    }
}
