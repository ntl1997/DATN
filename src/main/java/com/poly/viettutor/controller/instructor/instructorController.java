package com.poly.viettutor.controller.instructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.poly.viettutor.model.User;
import com.poly.viettutor.service.CourseService;
import com.poly.viettutor.service.EnrollmentService;
import com.poly.viettutor.service.OrderDetailService;
import com.poly.viettutor.service.UserService;

@Controller
public class instructorController {

    private final UserService userService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final OrderDetailService orderDetailService;

    public instructorController(UserService userService, CourseService courseService,
            EnrollmentService enrollmentService, OrderDetailService orderDetailService) {
        this.userService = userService;
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
        this.orderDetailService = orderDetailService;
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
    public String instructorReviews(Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            model.addAttribute("name", currentUser.getFullname());
        } else {
            model.addAttribute("name", "Unknown");
        }
        model.addAttribute("title", "Đánh giá");
        model.addAttribute("content", "client/instructor/instructor-reviews");
        return "client/layout/index";
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
