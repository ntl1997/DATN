package com.poly.viettutor.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminLoginController {

    @GetMapping({ "/admin", "/admin/login" })
    public String adminLogin(Model model) {
        model.addAttribute("title", "Admin đăng nhập");
        return "admin/login";
    }

}
