package com.poly.viettutor.controller.admin;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.poly.viettutor.dto.RegisterRequest;
import com.poly.viettutor.dto.UpdateUserInfoDTO;
import com.poly.viettutor.model.User;
import com.poly.viettutor.service.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AdminAccountController {

    private final UserService userService;

    public AdminAccountController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin/account/admins")
    public String showAdmins(Model model) {
        model.addAttribute("users", userService.getAllAdmins());
        model.addAttribute("title", "Danh sách quản trị viên");
        model.addAttribute("content", "admin/account/list-admin");
        model.addAttribute("scripts", "admin/account/list-admin");
        return "admin/layout/index";
    }

    @GetMapping("/admin/account/users")
    public String showUsers(Model model) {
        List<User> user = userService.getAllInstructorsAndStudents();
        model.addAttribute("users", user);
        model.addAttribute("title", "Danh sách người dùng");
        model.addAttribute("content", "admin/account/list-user");
        model.addAttribute("scripts", "admin/account/list-user");
        return "admin/layout/index";
    }

    @GetMapping("/admin/account/user/new")
    public String newAccount(@ModelAttribute("user") RegisterRequest request, Model model) {
        model.addAttribute("title", "Thêm người dùng mới");
        model.addAttribute("content", "admin/account/new-user");
        model.addAttribute("user", request);
        return "admin/layout/index";
    }

    @PostMapping("/admin/account/user/create")
    public String createUser(@Valid @ModelAttribute("user") RegisterRequest request,
            BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("title", "Thêm người dùng mới");
            model.addAttribute("content", "admin/account/new-user");
            model.addAttribute("user", request);
            return "admin/layout/index";
        }

        try {
            userService.register(request);
        } catch (Exception e) {
            return "redirect:/admin/account/users?createError=true";
        }

        return "redirect:/admin/account/users?createSuccess=true";
    }

    @GetMapping("/admin/account/user/edit/{id}")
    public String editAccount(@PathVariable("id") long id, Model model) {
        Optional<User> userOptional = userService.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            UpdateUserInfoDTO userDTO = new UpdateUserInfoDTO().toDTO(user);
            model.addAttribute("title", "Chỉnh sửa người dùng");
            model.addAttribute("content", "admin/account/edit-user");
            model.addAttribute("user", userDTO);
            return "admin/layout/index";
        } else {
            return "redirect:/admin/account/users?notFound=true";
        }
    }

    @PutMapping("/admin/account/user/update/{id}")
    public String updateUser(@Valid @ModelAttribute("user") UpdateUserInfoDTO userDTO,
            BindingResult result, @PathVariable("id") long id, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("title", "Chỉnh sửa người dùng");
            model.addAttribute("content", "admin/account/edit-user");
            model.addAttribute("user", userDTO);
            return "admin/layout/index";
        }

        try {
            userService.updateInfo(id, userDTO);
        } catch (Exception e) {
            return "redirect:/admin/account/users?updateError=true";
        }

        return "redirect:/admin/account/users?updateSuccess=true";
    }

    @PutMapping("/admin/account/user/changeStatus/{id}")
    public String updateUser(@PathVariable("id") long id, Model model) {
        try {
            userService.changeStatus(id);
        } catch (Exception e) {
            return "redirect:/admin/account/users?updateError=true";
        }

        return "redirect:/admin/account/users?updateSuccess=true";
    }

    @PutMapping("/admin/account/user/resetPassword/{id}")
    public String resetPassword(@PathVariable("id") long id, Model model) {
        try {
            userService.resetPassword(id);
        } catch (Exception e) {
            return "redirect:/admin/account/users?updateError=true";
        }

        return "redirect:/admin/account/users?updateSuccess=true";
    }

}
