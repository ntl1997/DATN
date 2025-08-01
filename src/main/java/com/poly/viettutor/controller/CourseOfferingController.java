package com.poly.viettutor.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.poly.viettutor.model.Category;
import com.poly.viettutor.model.Course;
import com.poly.viettutor.model.CourseOffering;
import com.poly.viettutor.model.User;
import com.poly.viettutor.service.*;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CourseOfferingController {

    private final CourseOfferingService courseOfferingService;

    private final EnrollmentService enrollmentService;
    private final CourseService courseService;
    private final CategoryService categoryService;
    private final UserService userService;

    public CourseOfferingController(CourseService courseService, CategoryService categoryService,
            UserService userService, EnrollmentService enrollmentService, CourseOfferingService courseOfferingService) {
        this.courseService = courseService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.enrollmentService = enrollmentService;
        this.courseOfferingService = courseOfferingService;
    }

    // Hàm phân trang
    @GetMapping("/courses")
    public String listCoursesPage(
            Model model,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<String> categories,
            @RequestParam(required = false) List<Integer> ratings,
            @RequestParam(required = false) List<String> instructor,
            @RequestParam(required = false) String priceType) {
        Page<Course> courses = courseService.searchCourses(
                keyword, categories, ratings,
                (instructor != null && !instructor.isEmpty()) ? instructor.get(0) : null,
                priceType, PageRequest.of(page - 1, size));
        List<Category> categoryList = categoryService.findAll();
        List<User> instructors = userService.getAllInstructors();
        model.addAttribute("instructors", instructors);
        model.addAttribute("categories", categoryList);
        model.addAttribute("courses", courses);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", courses.getTotalPages());
        model.addAttribute("title", "Danh sách khóa học");
        model.addAttribute("content", "client/course/courses");
        // Truyền lại các filter để giữ trạng thái trên giao diện
        model.addAttribute("selectedCategories", categories);
        model.addAttribute("selectedRatings", ratings);
        model.addAttribute("selectedInstructor", instructor);
        model.addAttribute("selectedPriceType", priceType);
        model.addAttribute("keyword", keyword);
        return "client/layout/index";
    }

    @GetMapping("/course-details/{id}")
    public String getById(@PathVariable("id") int id, HttpServletRequest request, Model model) {
        Optional<CourseOffering> courseOfferingOpt = courseOfferingService.findById(id);
        // Xử lý khi không tìm thấy khóa học, chuyển hướng hoặc báo lỗi
        if (courseOfferingOpt.isEmpty()) {
            request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 404);
            return "forward:/error";
        }

        CourseOffering courseOffering = courseOfferingOpt.get();
        User user = userService.getCurrentUser();
        Course course = courseOffering.getCourse();

        int totalDuration = courseService.totalDuration(course);
        boolean isEnrolled = enrollmentService.isEnrolled(user, courseOffering);

        model.addAttribute("course", course); // Thêm danh sách mục tiêu khóa học vào mô hình
        model.addAttribute("totalDuration", totalDuration); // Tổng thời gian của khóa học
        model.addAttribute("isEnrolled", isEnrolled); // Kiểm tra đã tham gia khóa học chưa
        model.addAttribute("title", "Chi tiết khóa học"); // tiêu đề trang (title)
        model.addAttribute("content", "client/course/course-detail"); // nội dung trang (phần content)
        model.addAttribute("scripts", "client/course/course-detail");
        return "client/layout/index";
    }

    @GetMapping("/enroll-course/{id}")
    public String getMethodName(@PathVariable("id") int id, HttpServletRequest request, Model model) {
        Optional<CourseOffering> courseOfferingOpt = courseOfferingService.findById(id);

        // Xử lý khi không tìm thấy khóa học, chuyển hướng hoặc báo lỗi
        if (courseOfferingOpt.isEmpty()) {
            request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 404);
            return "forward:/error";
        }

        CourseOffering courseOffering = courseOfferingOpt.get();
        User user = userService.getCurrentUser();

        enrollmentService.enrollCourse(user, courseOffering);
        return "redirect:/course-details/" + id;
    }

}
