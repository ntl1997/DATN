package com.poly.viettutor.service;

import java.util.Date;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.poly.viettutor.dto.RegisterRequest;
import com.poly.viettutor.model.User;
import com.poly.viettutor.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // đăng ký tài khoản
    public void register(RegisterRequest registerRequest) {
        User user = new User();
        user.setFullname(registerRequest.getFullname());
        user.setEmail(registerRequest.getEmail());
        user.setImage("user-icon.png");
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole("Student");
        user.setCreatedAt(new Date());
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

}
