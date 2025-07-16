package com.poly.viettutor.controller.instructor;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.poly.viettutor.model.Course;
import com.poly.viettutor.model.Review;
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

        courseCount = courseService.countCoursesByUser(currentUser);
        studentCount = enrollmentService.countStudentsByInstructor(currentUser);
        totalRevenue = orderDetailService.getTotalRevenueByInstructor(currentUser.getId());
        model.addAttribute("user", currentUser);
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

    @GetMapping("/instructor/courses")
    public String instructorCourses(Model model) {
        User currentUser = userService.getCurrentUser();

        if (currentUser != null) {
            model.addAttribute("name", currentUser.getFullname());

            List<Course> approvedCourses = courseService.findCoursesByInstructorIdAndStatus(currentUser.getId(),
                    "approved");
            List<Course> pendingCourses = courseService.findCoursesByInstructorIdAndStatus(currentUser.getId(),
                    "pending");

            // Tính review count và rating cho approvedCourses
            for (Course course : approvedCourses) {
                List<Review> reviews = course.getReviews();
                int reviewCount = reviews.size();
                double avgRating = reviewCount > 0
                        ? reviews.stream().mapToInt(Review::getRating).average().orElse(0)
                        : 0;
                course.setReviewCount(reviewCount);
                course.setRating((int) avgRating);
            }

            // Tính review count và rating cho pendingCourses
            for (Course course : pendingCourses) {
                List<Review> reviews = course.getReviews();
                int reviewCount = reviews.size();
                double avgRating = reviewCount > 0
                        ? reviews.stream().mapToInt(Review::getRating).average().orElse(0)
                        : 0;
                course.setReviewCount(reviewCount);
                course.setRating((int) avgRating);
            }
            model.addAttribute("user", currentUser);
            model.addAttribute("approvedCourses", approvedCourses);
            model.addAttribute("pendingCourses", pendingCourses);
        } else {
            model.addAttribute("name", "Unknown");
            model.addAttribute("approvedCourses", Collections.emptyList());
            model.addAttribute("pendingCourses", Collections.emptyList());
        }

        model.addAttribute("title", "My Courses");
        model.addAttribute("content", "client/instructor/instructor-course");
        return "client/layout/index";
    }

    @GetMapping("/instructor/announcements")
    public String instructorAnnouncements(Model model) {
        User currentUser = userService.getCurrentUser();

        model.addAttribute("user", currentUser);
        model.addAttribute("title", "Announcements");
        model.addAttribute("content", "client/instructor/instructor-announcements");

        return "client/layout/index";
    }

}
