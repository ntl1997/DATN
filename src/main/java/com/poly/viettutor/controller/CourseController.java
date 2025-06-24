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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size) {
        Page<Course> courses = courseService.findAll(PageRequest.of(page, size));
        List<Category> categories = categoryService.findAll();
        List<User> instructors = userService.getAllInstructors();
        model.addAttribute("instructors", instructors);
        model.addAttribute("categories", categories);
        model.addAttribute("courses", courses);
        model.addAttribute("title", "Danh sách khóa học");
        model.addAttribute("content", "client/courses");
        return "client/layout/index";
    }

}
