package com.example.Buoi2.controller;

import com.example.Buoi2.entity.Role;
import com.example.Buoi2.entity.User;
import com.example.Buoi2.services.RoleService;
import com.example.Buoi2.services.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final RoleService roleService;
    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "users/login";
    }

    @GetMapping("/register")
    public String register(@NotNull Model model) {
        model.addAttribute("user", new User());
        return "users/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user,
                           @RequestParam("confirmPassword") String confirmPassword,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "users/register";
        }

        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("passwordMismatch", "Passwords do not match");
            return "users/register";
        }

        userService.save(user);
        userService.setDefaultRole(user.getUsername());
        redirectAttributes.addFlashAttribute("successMessage", "User registered successfully!");
        return "redirect:/login";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "users/list";
    }

    @GetMapping("/users/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        List<Role> roles = roleService.getAllRoles();
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        return "users/edit";
    }

    @PostMapping("/users/edit/{id}")
    public String editUser(@PathVariable Long id,
                           @RequestParam("username") String username,
                           @RequestParam("email") String email,
                           @RequestParam("phone") String phone,
                           @RequestParam("address") String address,
                           @RequestParam("dateOfBirth") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfBirth,
                           @RequestParam("roles") List<Long> roleIds,
                           RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUserById(id);
            user.setUsername(username);
            user.setEmail(email);
            user.setPhone(phone);
            user.setAddress(address);
            user.setDateOfBirth(dateOfBirth);

            // Cập nhật roles
            List<Role> roles = roleService.getRolesByIds(roleIds);
            user.setRoles(new HashSet<>(roles));

            userService.updateUser(user);

            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating user. Please try again.");
        }
        return "redirect:/users";
    }



    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteUserById(id);
        redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully!");
        return "redirect:/users";
    }



}
