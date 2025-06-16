package com.poly.viettutor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping({ "/dashboard" })
    public String adminDashboard(Model model) {
        model.addAttribute("title", "Trang quản trị"); // tiêu đề trang (title)
        model.addAttribute("content", "admin/dashboard"); // nội dung trang (phần content)
        return "admin/layout/index";
    }

}
