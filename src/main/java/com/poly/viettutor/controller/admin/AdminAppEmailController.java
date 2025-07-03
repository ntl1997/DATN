package com.poly.viettutor.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.poly.viettutor.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
@RequestMapping("/admin/dashboard/application-email")
public class AdminAppEmailController {

    @Autowired
    private ContactService contactService;

    @GetMapping("/email")
    public String adminDashboard(Model model) {
        model.addAttribute("title", "Email");
        model.addAttribute("content", "admin/email/application-email");
        model.addAttribute("styles", "admin/email/application-email");
        model.addAttribute("scripts", "admin/email/application-email");
        model.addAttribute("contacts", contactService.findAll());
        return "admin/layout/index";
    }
}
