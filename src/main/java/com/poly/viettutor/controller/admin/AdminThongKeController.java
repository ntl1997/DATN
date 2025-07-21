package com.poly.viettutor.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminThongKeController {
    @GetMapping("/admin/KhoaHocDuocHocNhieuNhat")
    public String showKhoaHoc(Model model) {
        model.addAttribute("title", "Danh sách khóa học được học nhiều nhất");
        model.addAttribute("content", "admin/thongKe/khoaHocNhieuNhat");
        model.addAttribute("scripts", "admin/thongKe/khoaHocNhieuNhat");
        return "admin/layout/index";
    }

}
