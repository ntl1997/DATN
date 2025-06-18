package com.poly.viettutor.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poly.viettutor.model.User;
import com.poly.viettutor.service.UserService;
import com.poly.viettutor.utils.FileUtils;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/student-profile")
    public String showStudentProfile(Model model) {
        User user = userService.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("content", "client/student/student-profile");
        model.addAttribute("title", "Thông tin cá nhân");
        return "client/layout/index";
    }

    @GetMapping("/profile/edit")
    public String editProfile(Model model) {
        User user = userService.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("content", "client/student/editProfile");
        model.addAttribute("title", "Sửa thông tin cá nhân");
        return "client/layout/index";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @ModelAttribute("user") User updatedUser,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {

        User currentUser = userService.getCurrentUser();

        // Cập nhật các trường cho user hiện tại
        currentUser.setFullname(updatedUser.getFullname());
        currentUser.setEmail(updatedUser.getEmail());
        currentUser.setPhoneNumber(updatedUser.getPhoneNumber());
        currentUser.setOccupation(updatedUser.getOccupation());
        currentUser.setBiography(updatedUser.getBiography());

        // Xử lý upload ảnh nếu có file mới
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // Xóa ảnh cũ nếu không phải ảnh mặc định
                FileUtils.deleteImageIfExists(currentUser.getImage(), "uploads/users/");
                // Lưu ảnh mới
                String fileName = FileUtils.saveImage(imageFile, "uploads/users/");
                currentUser.setImage(fileName);
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Lỗi khi tải ảnh lên!");
                return "redirect:/student/profile/edit";
            }
        }

        userService.save(currentUser);

        redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin thành công!");
        return "redirect:/student/student-profile";
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm(Model model) {
        User user = userService.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("content", "client/student/changePassword");
        model.addAttribute("title", "Đổi mật khẩu");
        return "client/layout/index";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes,
            Model model) {

        User user = userService.getCurrentUser();
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Bạn cần đăng nhập để thực hiện chức năng này!");
            return "redirect:/student/change-password";
        }

        // So sánh mật khẩu hiện tại (đã mã hóa)
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu hiện tại không đúng!");
            return "redirect:/student/change-password";
        }

        // Kiểm tra xác nhận mật khẩu mới
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu mới và xác nhận không khớp!");
            return "redirect:/student/change-password";
        }

        // Cập nhật mật khẩu mới (mã hóa)
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.save(user);

        redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công!");
        return "redirect:/student/student-profile";
    }
}