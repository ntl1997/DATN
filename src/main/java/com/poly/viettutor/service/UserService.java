package com.poly.viettutor.service;

import java.util.Date;

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
        setRoleForUser(user);
        userRepository.save(user);
    }

    // Kiểm tra email đã tồn tại hay chưa
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    // Xác nhận mật khẩu khi đăng ký
    public boolean confirmPassword(RegisterRequest registerRequest) {
        return registerRequest.getPassword().equals(registerRequest.getConfirmPassword());
    }

    // Gán role cho user đăng ký
    public void setRoleForUser(User user) {
        Role role = roleRepository.findByRoleName("student")
                .orElseThrow(() -> new RuntimeException("Role 'student' does not exist"));
        user.getRoles().add(role);
    }

}
