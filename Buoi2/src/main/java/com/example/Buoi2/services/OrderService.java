package com.example.Buoi2.services;

import com.example.Buoi2.entity.CartItem;
import com.example.Buoi2.entity.Order;
import com.example.Buoi2.entity.OrderDetail;
import com.example.Buoi2.entity.Product;
import com.example.Buoi2.entity.User;
import com.example.Buoi2.repository.OrderDetailRepository;
import com.example.Buoi2.repository.OrderRepository;
import com.example.Buoi2.repository.ProductRepository;
import com.example.Buoi2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CartService cartService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public Order createOrder(String customerName, String shippingAddress, String phoneNumber, String email, String notes, List<CartItem> cartItems, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        Order order = new Order();
        order.setCustomerName(customerName);
        order.setShippingAddress(shippingAddress);
        order.setPhoneNumber(phoneNumber);
        order.setEmail(email);
        order.setNotes(notes);
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now()); // Thiết lập thời điểm tạo đơn hàng

        order = orderRepository.save(order);

        for (CartItem item : cartItems) {
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + item.getProduct().getId()));

            if (item.getQuantity() > product.getQuality()) {
                throw new IllegalArgumentException("Not enough quantity available for product: " + product.getName());
            }

            product.setQuality(product.getQuality() - item.getQuantity());
            productRepository.save(product);

            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(item.getProduct());
            detail.setQuantity(item.getQuantity());
            orderDetailRepository.save(detail);
        }

        cartService.clearCart();
        return order;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public void updateOrder(Order order) {
        orderRepository.save(order);
    }

    public void save(Order order) {
        orderRepository.save(order);
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }
}
