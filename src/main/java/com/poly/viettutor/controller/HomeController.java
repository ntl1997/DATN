package com.poly.viettutor.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.poly.viettutor.model.Category;
import com.poly.viettutor.repository.CourseCategoryRepository;
import com.poly.viettutor.service.CategoryService;

@Controller
public class HomeController {

    @Autowired
    private CourseCategoryRepository courseCategoryRepository;

    private final CategoryService categoryService;

    public HomeController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping({ "/", "/home" })
    public String homePage(Model model) {
        model.addAttribute("title", "Trang chủ"); // tiêu đề trang (title)
        List<Category> categories = categoryService.findAll();
        Map<Integer, Long> countCourseMap = new HashMap<>();
        for (Category category : categories) {
            long count = courseCategoryRepository.countByCategory(category);
            countCourseMap.put(category.getCategoryId(), count);
        }
        model.addAttribute("countCourseMap", countCourseMap); // số lượng khóa học theo danh mục
        model.addAttribute("categories", categories); // danh sách danh mục
        model.addAttribute("content", "client/home"); // nội dung trang (phần content)
        return "client/layout/index";
    }

}
