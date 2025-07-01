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

import com.poly.viettutor.model.Certificate;
import com.poly.viettutor.model.User;
import com.poly.viettutor.service.CertificateService;
import com.poly.viettutor.service.UserService;
import com.poly.viettutor.utils.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private UserService UserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    @GetMapping("/student-dashboard")
    public String showDashboard(Model model) {
        User user = userService.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("content", "client/student/student-dashboard");
        model.addAttribute("title", "Bảng Điều Khiển");
        return "client/layout/index";
    }
    
    @GetMapping("/student-enrolled-courses")
    public String showEnrolledCourses(Model model) {
        User user = userService.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("content", "client/student/student-enrolled-courses");
        model.addAttribute("title", "Các khóa học đã đăng ký");
        return "client/layout/index";
    }

    @GetMapping("/student-certificate")
    public String showCertificates(@RequestParam(value = "query", required = false) String query, Model model) {
    User user = userService.getCurrentUser();

    List<Certificate> certificates = (query != null && !query.isBlank())
            ? certificateService.searchCertificatesByUserAndTitle(user.getId(), query)
            : certificateService.getCertificatesByUserId(user.getId());

    logger.info("Found {} certificates for user ID: {}", certificates.size(), user.getId()); // 👈 log size

    model.addAttribute("certificates", certificates);
    model.addAttribute("user", user);
    model.addAttribute("content", "client/student/student-certificate");
    model.addAttribute("title", "Chứng chỉ");

    return "client/layout/index";
}


    // Hiển thị chi tiết chứng chỉ theo ID
    @GetMapping("/student-certificate/{id}")
    public String showCertificateDetail(@PathVariable("id") Integer id, Model model) {
        Certificate certificate = certificateService.getCertificateById(id);

        model.addAttribute("certificate", certificate);
        model.addAttribute("content", "client/student/student-certificate-detail");
        model.addAttribute("title", "Chi tiết chứng chỉ");

        return "client/layout/index";
    }

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
            @ModelAttribute("user") User updatedUser,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {

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

        // So sánh mật khẩu hiện tại (đã mã hóa)
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu hiện tại không đúng!");
            return "redirect:/student/student-settings";
        }

        // Kiểm tra xác nhận mật khẩu mới
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu mới và xác nhận không khớp!");
            return "redirect:/student/student-settings";
        }

        // Cập nhật mật khẩu mới (mã hóa)
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
}