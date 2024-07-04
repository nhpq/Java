package com.example.Buoi2.controller;

import com.example.Buoi2.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") Long productId, @RequestParam("quantity") int quantity) {
        cartService.addToCart(productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/update")
    @ResponseBody
    public String updateCart(@RequestParam("productId") Long productId, @RequestParam("quantity") int quantity) {
        try {
            cartService.updateCartItem(productId, quantity);
            return "{\"status\":\"success\"}";
        } catch (Exception e) {
            return "{\"status\":\"error\", \"message\":\"" + e.getMessage() + "\"}";
        }
    }

    @GetMapping
    public String showCart(Model model) {
        model.addAttribute("cartItems", cartService.getCartItems());
        return "/cart/cart";
    }

    @GetMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable Long productId) {
        cartService.removeFromCart(productId);
        return "redirect:/cart";
    }

    @GetMapping("/clear")
    public String clearCart() {
        cartService.clearCart();
        return "redirect:/cart";
    }
}
