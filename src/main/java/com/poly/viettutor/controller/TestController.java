package com.poly.viettutor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping({ "/", "/home" })
    public String homePage(Model model) {
        model.addAttribute("title", "Trang chủ"); // tiêu đề trang (title)
        model.addAttribute("content", "client/home"); // nội dung trang (phần content)
        return "client/layout/index";
    }

}
