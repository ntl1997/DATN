package com.poly.viettutor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StudentController {
    @GetMapping("/student-profile")
    public String showStudentProfile(Model model) {
        model.addAttribute("content", "client/layout/student-profile");
        model.addAttribute("title", "Thông tin cá nhân");
        return "client/layout/index";
    }

}
