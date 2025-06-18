package com.poly.viettutor.service;

import java.util.Date;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.poly.viettutor.dto.RegisterRequest;
import com.poly.viettutor.model.Role;
import com.poly.viettutor.model.User;
import com.poly.viettutor.repository.RoleRepository;
import com.poly.viettutor.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // đăng ký tài khoản
    public void register(RegisterRequest registerRequest) {
        User user = new User();
        user.setFullname(registerRequest.getFullname());
        user.setEmail(registerRequest.getEmail());
        user.setImage("user-icon.png");
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreatedAt(new Date());
        setRoleForUser(user, "STUDENT"); // Gán role mặc định là STUDENT
        userRepository.save(user);
    }

    // Kiểm tra email đã tồn tại hay chưa
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    // Gán role cho user đăng ký
    public void setRoleForUser(User user, String roleName) {
        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException("Role " + roleName + " does not exist"));
        user.getRoles().add(role);
    }

    // Lấy thông tin người dùng hiện đang đăng nhập
    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElse(null);
    }

    // Lưu hoặc cập nhật thông tin user
    public void save(User user) {
        userRepository.save(user);
    }

}
