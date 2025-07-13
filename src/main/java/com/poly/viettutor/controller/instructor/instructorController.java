package com.poly.viettutor.controller.instructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poly.viettutor.model.Review;
import com.poly.viettutor.model.User;
import com.poly.viettutor.service.CourseService;
import com.poly.viettutor.service.EnrollmentService;
import com.poly.viettutor.service.OrderDetailService;
import com.poly.viettutor.service.OrderService;
import com.poly.viettutor.service.ReviewService;
import com.poly.viettutor.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class instructorController {

    private final UserService userService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final OrderDetailService orderDetailService;
    private final ReviewService reviewService;
    private final OrderService orderService;

    public instructorController(UserService userService, CourseService courseService,
            EnrollmentService enrollmentService, OrderDetailService orderDetailService, ReviewService reviewService) {
        this.userService = userService;
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
        this.orderDetailService = orderDetailService;
        this.reviewService = reviewService;
        this.orderService = orderService;
    }

    @GetMapping("/instructor/dashboard")
    public String instructorDashboard(Model model) {
        User currentUser = userService.getCurrentUser();

        long courseCount = 0L;
        long studentCount = 0L;
        BigDecimal totalRevenue = BigDecimal.ZERO;

        if (currentUser != null) {
            model.addAttribute("name", currentUser.getFullname());
            courseCount = courseService.countCoursesByUser(currentUser);
            studentCount = enrollmentService.countStudentsByInstructor(currentUser);
            totalRevenue = orderDetailService.getTotalRevenueByInstructor(currentUser.getId());
        } else {
            model.addAttribute("name", "Unknown");
        }

        Long instructorId = currentUser.getId();
        List<Object[]> courseSummary = courseService.getCourseSummaryByInstructor(instructorId);
        model.addAttribute("title", "Trang giảng viên");
        model.addAttribute("courseCount", courseCount);
        model.addAttribute("courseSummary", courseSummary);
        model.addAttribute("studentCount", studentCount);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("content", "client/instructor/instructor-dashboard");

        return "client/layout/index";
    }

    @GetMapping("/instructor/profile")
    public String instructorProfile(Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            model.addAttribute("user", currentUser);

            String roleName = currentUser.getRoles().stream()
                    .findFirst()
                    .map(r -> r.getRoleName())
                    .orElse("No Role");

            model.addAttribute("role", roleName);

            model.addAttribute("title", "Hồ sơ giảng viên");
            model.addAttribute("content", "client/instructor/instructor-profile");
        } else {
            model.addAttribute("error", "User not found");
        }
        return "client/layout/index";
    }

    @GetMapping("/instructor/reviews")
    public String instructorReviews(Model model, HttpServletRequest request) {
        User currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            model.addAttribute("name", currentUser.getFullname());
            List<Review> reviews = reviewService.getReviewsByInstructor(currentUser.getId());
            List<Review> reviewsGive = reviewService.getReviewsWrittenByInstructor(currentUser.getId());
            model.addAttribute("reviews", reviews);
            model.addAttribute("reviewsGive", reviewsGive);
        } else {
            model.addAttribute("name", "Unknown");
            model.addAttribute("reviews", List.of());
        }
        model.addAttribute("title", "Đánh giá");
        model.addAttribute("content", "client/instructor/instructor-reviews");
        return "client/layout/index";
    }

    @PostMapping("/review/update")
    public String updateReview(@RequestParam Long reviewId,
            @RequestParam int rating,
            @RequestParam String comment,
            RedirectAttributes redirectAttributes) {
        Review review = reviewService.getReviewById(reviewId);
        if (review != null) {
            review.setRating(rating);
            review.setComment(comment.trim());
            review.setReviewedAt(new Date());
            reviewService.updateReview(review);
            redirectAttributes.addFlashAttribute("success", "Cập nhật review thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Cập nhập không thành công");
        }
        return "redirect:/instructor/reviews";
    }

    @PostMapping("/review/delete/{id}")
    public String deleteReview(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            reviewService.deleteReviewById(id);
            redirectAttributes.addFlashAttribute("success", "Xóa review thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa review: " + e.getMessage());
        }
        return "redirect:/instructor/reviews";
    }

    @GetMapping("/instructor/order-history")
    public String instructorOrderHistory(Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            model.addAttribute("name", currentUser.getFullname());
        } else {
            model.addAttribute("name", "Unknown");
        }
        model.addAttribute("title", "Lịch sử đơn hàng");
        model.addAttribute("content", "client/instructor/instructor-order-history");
        return "client/layout/index";
    }

    @GetMapping("/instructor/courses")
    public String instructorCourses(Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            model.addAttribute("name", currentUser.getFullname());
        } else {
            model.addAttribute("name", "Unknown");
        }
        model.addAttribute("title", "My Courses");
        model.addAttribute("content", "client/instructor/instructor-course");
        return "client/layout/index";
    }

    @GetMapping("/instructor/announcements")
    public String instructorAnnouncements(Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            model.addAttribute("name", currentUser.getFullname());
        } else {
            model.addAttribute("name", "Unknown");
        }

        model.addAttribute("title", "Announcements");
        model.addAttribute("content", "client/instructor/instructor-announcements");

        return "client/layout/index";
    }

    @GetMapping("/instructor/quiz-attempts")
    public String instructorQuizAttempts(Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            model.addAttribute("name", currentUser.getFullname());
        } else {
            model.addAttribute("name", "Unknown");
        }
        model.addAttribute("title", "Quiz Attempts");
        model.addAttribute("content", "client/instructor/instructor-quiz-attempts");
        return "client/layout/index";
    }

    @GetMapping("/instructor/assignments")
    public String instructorAssignments(Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            model.addAttribute("name", currentUser.getFullname());
        } else {
            model.addAttribute("name", "Unknown");
        }
        model.addAttribute("title", "Assignments");
        model.addAttribute("content", "client/instructor/instructor-assignments");
        return "client/layout/index";
    }

    @GetMapping("/instructor/settings")
    public String instructorSettings(Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            model.addAttribute("name", currentUser.getFullname());
        } else {
            model.addAttribute("name", "Unknown");
        }
        model.addAttribute("title", "Settings");
        model.addAttribute("content", "client/instructor/instructor-settings");
        return "client/layout/index";
    }
}
