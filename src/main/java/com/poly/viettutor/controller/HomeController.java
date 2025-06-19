package com.poly.viettutor.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.poly.viettutor.model.BlogPost;
import com.poly.viettutor.model.Category;
import com.poly.viettutor.model.Course;
import com.poly.viettutor.service.BlogPostService;
import com.poly.viettutor.service.CategoryService;
import com.poly.viettutor.service.CourseService;

@Controller
public class HomeController {

    private final CategoryService categoryService;
    private final CourseService courseService;
    private final BlogPostService blogPostService;

    public HomeController(CategoryService categoryService, CourseService courseService,
            BlogPostService blogPostService) {
        this.categoryService = categoryService;
        this.courseService = courseService;
        this.blogPostService = blogPostService;
    }

    @GetMapping({ "/", "/home" })
    public String homePage(Model model) {
        model.addAttribute("title", "Trang chủ"); // tiêu đề trang (title)
        List<Category> categories = categoryService.findAll();
        List<Course> courses = courseService.getTop6PopularCourses(); // lấy danh sách khóa học từ service
        List<BlogPost> blogPosts = blogPostService.findAll(); // lấy danh sách bài viết từ service
        model.addAttribute("categories", categories); // danh sách danh mục
        model.addAttribute("courses", courses); // danh sách khóa học
        model.addAttribute("blogPosts", blogPosts); // danh sách bài viết
        model.addAttribute("content", "client/home"); // nội dung trang (phần content)
        return "client/layout/index";
    }

}
