package com.poly.viettutor.controller;

import com.poly.viettutor.dto.CreateCourseDTO;
import com.poly.viettutor.model.Category;
import com.poly.viettutor.model.User;
import com.poly.viettutor.service.CategoryService;
import com.poly.viettutor.service.CourseService;
import com.poly.viettutor.service.UserService;
import com.poly.viettutor.utils.FileUtils;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CreateCourseController {

    private final CategoryService categoryService;
    private final CourseService courseService;
    private final UserService userService;

    CreateCourseController(CategoryService categoryService,
            CourseService courseService,
            UserService userService) {
        this.categoryService = categoryService;
        this.courseService = courseService;
        this.userService = userService;
    }

    @GetMapping("/instructor/create-course")
    public String showCreateCourse(@ModelAttribute("course") CreateCourseDTO courseDTO, Model model) {
        return loadPage(model);
    }

    @PostMapping("/instructor/create-course")
    public String createCourse(@Valid @ModelAttribute("course") CreateCourseDTO courseDTO, BindingResult result,
            @RequestParam(name = "createinputfile", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            System.out.println(result);
            return loadPage(model);
        }

        try {
            String fileName = null;
            User user = userService.getCurrentUser();
            if (imageFile != null && !imageFile.isEmpty()) {
                fileName = FileUtils.saveImage(imageFile, "uploads/course/");
            }
            courseService.create(user, courseDTO, fileName);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("createError", e.getMessage());
            return "redirect:/student/dashboard";
        }

        return "redirect:/student/dashboard?createSuccess=true";
    }

    private String loadPage(Model model) {
        List<Category> categories = categoryService.findAll();
        model.addAttribute("title", "Tạo khóa học");
        model.addAttribute("content", "client/create-course");
        model.addAttribute("scripts", "client/create-course");
        model.addAttribute("categories", categories);
        return "client/layout/index";
    }

}
