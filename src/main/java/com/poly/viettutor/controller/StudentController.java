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
import com.poly.viettutor.model.Enrollment;
import com.poly.viettutor.model.User;
import com.poly.viettutor.service.CertificateService;
import com.poly.viettutor.model.Order;
import com.poly.viettutor.model.OrderDetail;
import com.poly.viettutor.model.Wishlist;
import com.poly.viettutor.service.OrderService;
import com.poly.viettutor.service.UserService;
import com.poly.viettutor.service.WishListService;
import com.poly.viettutor.utils.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private WishListService wishListService;

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
        int enrolledCourseCount = user.getEnrollments() != null ? user.getEnrollments().size() : 0;

        model.addAttribute("user", user);
        model.addAttribute("enrolledCourseCount", enrolledCourseCount);
        model.addAttribute("content", "client/student/student-dashboard");
        model.addAttribute("title", "Bảng Điều Khiển");

        return "client/layout/index";
    }

    @GetMapping("/student-enrolled-courses")
    public String showEnrolledCourses(Model model) {
        User user = userService.getCurrentUser();

        // Lấy danh sách enrollments của user
        List<Enrollment> enrollments = user.getEnrollments();

        model.addAttribute("user", user);
        model.addAttribute("enrollments", enrollments); // Đẩy danh sách lên view
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

        if (certificate == null || certificate.getUser() == null || certificate.getCourse() == null) {
            model.addAttribute("errorMessage", "Không tìm thấy chứng chỉ với ID: " + id);
            model.addAttribute("content", "client/error");
            model.addAttribute("title", "Lỗi");
            return "client/layout/index";
        }

        model.addAttribute("certificate", certificate);
        model.addAttribute("content", "client/student/student-certificate-detail");
        model.addAttribute("title", "Chi tiết chứng chỉ");

        // ✅ Nhúng style fragment từ file chứng chỉ
        model.addAttribute("styles", "client/student/student-certificate-detail");

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
        model.addAttribute("user", user);
        model.addAttribute("orderDetails", orderDetails);
        model.addAttribute("content", "client/student/student-history");
        model.addAttribute("title", "Lịch sử đơn hàng");
        return "client/layout/index";
    }

    @GetMapping("/student-wishlist")
    public String showStudentWishlist(Model model) {
        User user = userService.getCurrentUser();
        List<Wishlist> wishlist = wishListService.getWishlistByUser(user);
        model.addAttribute("user", user);
        model.addAttribute("wishlist", wishlist);
        model.addAttribute("content", "client/student/student-wishlist");
        model.addAttribute("title", "Danh sách yêu thích");
        return "client/layout/index";
    }

    @PostMapping("/wishlist/delete/{id}")
    public String deleteWishlist(@PathVariable("id") Integer wishlistId, RedirectAttributes redirectAttributes) {
        User user = userService.getCurrentUser();
        wishListService.deleteByIdAndUser(wishlistId, user);
        redirectAttributes.addFlashAttribute("success", "Đã xóa khỏi danh sách yêu thích!");
        return "redirect:/student/student-wishlist";
    }

    @PostMapping("/wishlist/add/{courseId}")
    public String addWishlist(@PathVariable("courseId") Integer courseId, RedirectAttributes redirectAttributes) {
        User user = userService.getCurrentUser();
        wishListService.addWishlist(user, courseId);
        redirectAttributes.addFlashAttribute("success", "Đã thêm vào danh sách yêu thích!");
        return "redirect:" + getReferer();
    }

    // Lấy URL trang trước đó
    private String getReferer() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            String referer = attrs.getRequest().getHeader("Referer");
            return referer != null ? referer : "/";
        }
        return "/";
    }
}