package com.poly.viettutor.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminDashboardController {

    @GetMapping({ "admin/", "/admin/dashboard" })
    public String adminDashboard(Model model) {
        model.addAttribute("title", "Trang quản trị");
        model.addAttribute("content", "admin/dashboard");
        model.addAttribute("scripts", "admin/dashboard");
        return "admin/layout/index";
    }

}
