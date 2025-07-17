package com.poly.viettutor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AboutController {

    @GetMapping("/about")
    public String showAboutPage(Model model) {
        model.addAttribute("title", "Giới thiệu");
        model.addAttribute("content", "client/about");
        return "client/layout/index";
    }

}
