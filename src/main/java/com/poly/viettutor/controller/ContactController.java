package com.poly.viettutor.controller;

import com.poly.viettutor.model.ContactInfo;
import com.poly.viettutor.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import java.util.Date;

@Controller
public class ContactController {

    @Autowired
    private ContactService contactService;

    @GetMapping("/contact")
    public String showContactForm(Model model) {
        model.addAttribute("contactInfo", new ContactInfo());
        model.addAttribute("content", "client/contact");
        model.addAttribute("title", "Liên hệ");
        return "client/layout/index";
    }

    @PostMapping("/contact")
    public String submitContact(
            @Valid @ModelAttribute("contactInfo") ContactInfo contactInfo,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("content", "client/contact");
            model.addAttribute("title", "Liên hệ");
            return "client/layout/index";
        }
        try {
            contactInfo.setCreatedAt(new Date());
            contactService.save(contactInfo);
            model.addAttribute("success",
                    "Gửi thông tin liên hệ thành công! Chúng tôi sẽ liên hệ lại với bạn sớm nhất.");
        } catch (Exception e) {
            model.addAttribute("error", "Gửi thông tin thất bại. Vui lòng thử lại sau!");
        }
        model.addAttribute("contactInfo", new ContactInfo());
        model.addAttribute("content", "client/contact");
        model.addAttribute("title", "Liên hệ");
        return "client/layout/index";
    }
}