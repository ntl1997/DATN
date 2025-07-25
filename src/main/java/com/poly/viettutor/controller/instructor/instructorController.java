package com.poly.viettutor.controller.instructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.poly.viettutor.model.Course;
import com.poly.viettutor.model.User;
import com.poly.viettutor.service.CourseService;
import com.poly.viettutor.service.EnrollmentService;
import com.poly.viettutor.service.OrderDetailService;
import com.poly.viettutor.service.QuizService;
import com.poly.viettutor.service.UserService;

@Controller
public class instructorController {

    private final UserService userService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final OrderDetailService orderDetailService;
    private final QuizService quizService;

    public instructorController(UserService userService, CourseService courseService,
            EnrollmentService enrollmentService, OrderDetailService orderDetailService, QuizService quizService) {
        this.userService = userService;
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
        this.orderDetailService = orderDetailService;
        this.quizService = quizService;
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
        List<Course> publishCourses = courseService.findCoursesByInstructorIdAndStatus(currentUser.getId(),
                "publish");
        List<Course> pendingCourses = courseService.findCoursesByInstructorIdAndStatus(currentUser.getId(),
                "pending");
        List<Course> draftCourses = courseService.findCoursesByInstructorIdAndStatus(currentUser.getId(),
                "draft");
        List<Course> hiddenCourses = courseService.findCoursesByInstructorIdAndStatus(currentUser.getId(),
                "hidden");
        model.addAttribute("user", currentUser);
        model.addAttribute("publishCourses", publishCourses);
        model.addAttribute("pendingCourses", pendingCourses);
        model.addAttribute("draftCourses", draftCourses);
        model.addAttribute("hiddenCourses", hiddenCourses);
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

    @GetMapping("/instructor/instructor-quiz-attempts")
    public String instructorQuizAttempts(
            @RequestParam(name = "courseTitle", required = false) String courseTitle,
            Model model) {

        User currentUser = userService.getCurrentUser();

        // Gọi service để lấy dữ liệu theo courseTitle
        List<Map<String, Object>> quizSubmissions = quizService.getQuizSubmissionsByCourseTitle(courseTitle);

        model.addAttribute("user", currentUser);
        model.addAttribute("quizSubmissions", quizSubmissions);
        model.addAttribute("title", "Quiz Attempts");
        model.addAttribute("content", "client/instructor/instructor-quiz-attempts");

        return "client/layout/index";
    }

}
