package com.poly.viettutor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BlogController {
    @GetMapping("/blog-list")
    public String showBlog(Model model) {
        model.addAttribute("content", "client/layout/blog-list");
        model.addAttribute("title", "Bài viết");
        return "client/layout/index";
    }

}
