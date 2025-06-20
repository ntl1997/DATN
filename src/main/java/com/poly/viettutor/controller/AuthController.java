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
        return loadPage(model, "Đăng nhập", "client/auth/login");
    }

    @GetMapping("/register")
    public String showRegister(@ModelAttribute("registerRequest") RegisterRequest registerRequest, Model model) {
        return loadPage(model, "Đăng ký", "client/auth/register");
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute("registerRequest") RegisterRequest registerRequest,
            BindingResult bindingResult, Model model) {
        // validate form
        if (bindingResult.hasErrors()) {
            return loadPage(model, "Đăng ký", "client/auth/register");
        }

        // kiểm tra email đã tồn tại
        if (userService.isEmailExists(registerRequest.getEmail())) {
            bindingResult.rejectValue("email", null, "Email đã tồn tại");
            return loadPage(model, "Đăng ký", "client/auth/register");
        }

        // Kiểm tra xác nhận mật khẩu
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", null, "Mật khẩu không khớp");
            return loadPage(model, "Đăng ký", "client/auth/register");
        }

        userService.register(registerRequest);
        return "redirect:/login?RegisterSuccess=true";
    }

    // Tải trang với tiêu đề và nội dung (viewPath là đường dẫn đến file template)
    private String loadPage(Model model, String title, String viewPath) {
        model.addAttribute("title", title);
        model.addAttribute("content", viewPath);
        return "client/layout/index";
    }

}
