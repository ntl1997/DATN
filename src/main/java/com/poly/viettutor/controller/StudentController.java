package com.poly.viettutor.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poly.viettutor.model.Order;
import com.poly.viettutor.model.OrderDetail;
import com.poly.viettutor.model.User;
import com.poly.viettutor.service.OrderService;
import com.poly.viettutor.service.UserService;
import com.poly.viettutor.utils.FileUtils;

import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

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

    @PostMapping("/profile/update")
    public String updateProfile(
            @Valid @ModelAttribute("user") User updatedUser,
            BindingResult bindingResult,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", updatedUser);
            model.addAttribute("content", "client/student/student-settings");
            model.addAttribute("title", "Cài đặt tài khoản");
            return "client/layout/index";
        }

        User currentUser = userService.getCurrentUser();

        // Cập nhật các trường cho user hiện tại
        currentUser.setFullname(updatedUser.getFullname());
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
                return "redirect:/student/student-settings";
            }
        }

        userService.save(currentUser);

        redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin thành công!");
        return "redirect:/student/student-settings";
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
            return "redirect:/student/student-settings";
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu hiện tại không đúng!");
            return "redirect:/student/student-settings";
        }

        if (newPassword.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu mới phải có ít nhất 6 ký tự!");
            return "redirect:/student/student-settings";
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu mới và xác nhận không khớp!");
            return "redirect:/student/student-settings";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userService.save(user);

        redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công!");
        return "redirect:/student/student-settings";
    }

    @GetMapping("/student-settings")
    public String showStudentSettings(Model model) {
        User user = userService.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("content", "client/student/student-settings");
        model.addAttribute("scripts", "client/student/student-settings");
        model.addAttribute("title", "Cài đặt tài khoản");
        return "client/layout/index";
    }

    @GetMapping("/student-order-history")
    public String showStudentHistory(Model model) {
        User user = userService.getCurrentUser();
        List<Order> orderList = orderService.findByUser(user);
        List<OrderDetail> orderDetails = orderList.stream()
                .flatMap(order -> order.getOrderDetails().stream()
                        .peek(detail -> detail.setOrder(order))) // đảm bảo order không bị lazy
                .toList();
        model.addAttribute("orderDetails", orderDetails);
        model.addAttribute("content", "client/student/student-history");
        model.addAttribute("title", "Lịch sử đơn hàng");
        return "client/layout/index";
    }

}