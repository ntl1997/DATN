package com.poly.viettutor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

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
        return "client/layout/index";
    }

}
