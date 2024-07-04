package com.example.Buoi2.services;

import com.example.Buoi2.entity.Role;
import com.example.Buoi2.entity.User;
import com.example.Buoi2.repository.IRoleRepository;
import com.example.Buoi2.repository.IUserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@Transactional
public class UserService implements UserDetailsService {
    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    public void save(@NotNull User user) {
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
    }

    public void setDefaultRole(String username) {
        userRepository.findByUsername(username).ifPresentOrElse(
                user -> {
                    Optional<Role> userRole = roleRepository.findByName("USER");
                    userRole.ifPresent(role -> user.getRoles().add(role)); // Thêm vai trò nếu tồn tại
                    userRepository.save(user);
                },
                () -> {
                    throw new UsernameNotFoundException("User not found");
                }
        );
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getAuthorities())
                .accountExpired(!user.isAccountNonExpired())
                .accountLocked(!user.isAccountNonLocked())
                .credentialsExpired(!user.isCredentialsNonExpired())
                .disabled(!user.isEnabled())
                .build();
    }

    public User updateUser(User updatedUser) {
        User existingUser = userRepository.findById(updatedUser.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setAddress(updatedUser.getAddress());
        existingUser.setDateOfBirth(updatedUser.getDateOfBirth());
        existingUser.setRoles(updatedUser.getRoles()); // Cập nhật vai trò

        return userRepository.save(existingUser);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Xóa các liên kết với Role
        user.getRoles().clear(); // Xóa tất cả các liên kết với Role

        // Tiếp tục xóa người dùng
        userRepository.deleteById(id);
    }

    public void updateUserRoles(Long userId, List<Long> roleIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Set<Role> roles = new HashSet<>();
        for (Long roleId : roleIds) {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));
            roles.add(role);
        }

        user.setRoles(roles);
        userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
