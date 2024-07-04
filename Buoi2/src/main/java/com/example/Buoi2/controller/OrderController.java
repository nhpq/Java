package com.example.Buoi2.controller;

import com.example.Buoi2.entity.CartItem;
import com.example.Buoi2.entity.Order;
import com.example.Buoi2.entity.User;
import com.example.Buoi2.services.OrderService;
import com.example.Buoi2.services.CartService;
import com.example.Buoi2.services.UserService;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private CartService cartService;

    @GetMapping("/checkout")
    public String checkout(Model model) {
        List<CartItem> cartItems = cartService.getCartItems();
        model.addAttribute("cartItems", cartItems);
        return "/cart/checkout";
    }

    @PostMapping("/submit")
    public String submitOrder(@ModelAttribute Order order, Model model, Principal principal) {
        // Lấy thông tin người dùng từ Principal
        String username = principal.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        // Gắn user vào đơn hàng
        order.setUser(user);

        // Tính tổng tiền
        double totalAmount = order.getTotalAmount();
        model.addAttribute("order", order);
        model.addAttribute("totalAmount", totalAmount);

        // Lưu đơn hàng vào database và nhận lại đối tượng đã lưu
        Order savedOrder = orderService.createOrder(order.getCustomerName(), order.getShippingAddress(), order.getPhoneNumber(), order.getEmail(), order.getNotes(), cartService.getCartItems(),username);

        // Chuyển hướng đến trang xác nhận đơn hàng với id của đơn hàng vừa lưu
        return "redirect:/order/confirmation/" + savedOrder.getId();
    }

    @GetMapping("/confirmation/{orderId}")
    public String showOrderConfirmation(@PathVariable Long orderId, Model model) {
        // Lấy thông tin đơn hàng từ service dựa trên orderId
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            // Nếu không tìm thấy đơn hàng, chuyển hướng về trang checkout
            return "redirect:/order/checkout";
        }

        // Thêm đối tượng đơn hàng vào model để hiển thị trên template
        model.addAttribute("order", order);
        return "cart/confirmation"; // Đảm bảo template name phù hợp với đường dẫn và tên file thực tế
    }


    @GetMapping("/admin/orders")
    public String listAllOrders(Model model) {
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "orders/list"; // Thymeleaf template name for admin
    }

    @PostMapping("/admin/orders/update")
    public String updateOrderStatus(@RequestParam("id") Long id, @RequestParam("status") String status) {
        try {
            Order order = orderService.getOrderById(id);
            order.setStatus(status);
            orderService.updateOrder(order); // Cập nhật đơn hàng vào cơ sở dữ liệu
            return "redirect:/order/admin/orders";
        } catch (Exception e) {
            return "redirect:/order/admin/orders?error=true";
        }
    }


    @GetMapping("/orders")
    public String getUserOrders(Model model, Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        List<Order> orders = orderService.getOrdersByUserId(user.getId());
        model.addAttribute("orders", orders);

        return "orders/user-orders"; // Đảm bảo đúng tên template và đường dẫn
    }

    // Endpoint để hiển thị chi tiết đơn hàng dựa vào id
    @GetMapping("/orders/{orderId}")
    public String getOrderDetails(@PathVariable Long orderId, Model model) {
        Order order = orderService.getOrderById(orderId);
        model.addAttribute("order", order);
        return "orders/order-details";
    }
}
