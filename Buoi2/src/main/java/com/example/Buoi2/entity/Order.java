package com.example.Buoi2.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private String shippingAddress;
    private String phoneNumber;
    private String email;
    private String notes;
    private String paymentMethod;
    private String status;
    private LocalDateTime orderDate = LocalDateTime.now(); // Khởi tạo mặc định là thời điểm hiện tại

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Phương thức tính tổng tiền hóa đơn
    public double getTotalAmount() {
        return orderDetails.stream()
                .mapToDouble(detail -> detail.getProduct().getPrice() * detail.getQuantity())
                .sum();
    }
}
