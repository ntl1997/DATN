package com.poly.viettutor.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.poly.viettutor.model.Category;
import com.poly.viettutor.model.Course;
import com.poly.viettutor.model.User;
import com.poly.viettutor.service.CategoryService;
import com.poly.viettutor.service.CourseService;
import com.poly.viettutor.service.UserService;

@Controller
public class CourseController {
    private final CourseService courseService;
    private final CategoryService categoryService;
    private final UserService userService;

    public CourseController(CourseService courseService, CategoryService categoryService, UserService userService) {
        this.courseService = courseService;
        this.categoryService = categoryService;
        this.userService = userService;
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
        model.addAttribute("content", "client/courses");
        // Truyền lại các filter để giữ trạng thái trên giao diện
        model.addAttribute("selectedCategories", categories);
        model.addAttribute("selectedRatings", ratings);
        model.addAttribute("selectedInstructor", instructor);
        model.addAttribute("selectedPriceType", priceType);
        model.addAttribute("keyword", keyword);
        return "client/layout/index";
    }

}
