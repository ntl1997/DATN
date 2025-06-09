package com.poly.viettutor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.poly.viettutor.dto.RegisterRequest;
import com.poly.viettutor.service.UserService;

import jakarta.validation.Valid;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLogin(Model model) {
        model.addAttribute("title", "Đăng nhập");
        model.addAttribute("content", "client/auth/login");
        return "client/layout/index";
    }

    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("title", "Đăng ký");
        model.addAttribute("content", "client/auth/register");
        model.addAttribute("registerRequest", new RegisterRequest());
        return "client/layout/index";
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute("registerRequest") RegisterRequest registerRequest,
            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) { // validate form
            model.addAttribute("title", "Đăng ký");
            model.addAttribute("content", "client/auth/register");
            model.addAttribute("registerRequest", registerRequest);
            return "client/layout/index";
        }

        if (userService.isEmailExists(registerRequest.getEmail())) { // kiểm tra email đã tồn tại
            bindingResult.rejectValue("email", "error", "Email đã tồn tại!");
            model.addAttribute("title", "Đăng ký");
            model.addAttribute("content", "client/auth/register");
            model.addAttribute("registerRequest", registerRequest);
            return "client/layout/index";
        }

        if (!userService.confirmPassword(registerRequest)) { // Kiểm tra xác nhận mật khẩu
            bindingResult.rejectValue("confirmPassword", "error", "Xác nhận mật khẩu không khớp!");
            model.addAttribute("title", "Đăng ký");
            model.addAttribute("content", "client/auth/register");
            model.addAttribute("registerRequest", registerRequest);
            return "client/layout/index";
        }

        userService.register(registerRequest);
        return "redirect:/login?RegisterSuccess=true";
    }

}
