package com.poly.viettutor.controller;

import com.poly.viettutor.dto.CourseDTO;
import com.poly.viettutor.model.Category;
import com.poly.viettutor.model.Course;
import com.poly.viettutor.model.User;
import com.poly.viettutor.service.CategoryService;
import com.poly.viettutor.service.CourseService;
import com.poly.viettutor.service.UserService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

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

@Slf4j
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
    public String showCreateCourse(@ModelAttribute("course") CourseDTO courseDTO, Model model) {
        return loadPage(model);
    }

    @PostMapping("/instructor/create-course")
    public String createCourse(@Valid @ModelAttribute("course") CourseDTO courseDTO, BindingResult result,
            @RequestParam(name = "createinputfile", required = false) MultipartFile imageFile,
            @RequestParam(name = "attachments", required = false) MultipartFile[] materialFiles,
            RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            return loadPage(model);
        }

        try {
            User user = userService.getCurrentUser();
            Course savedCourse = courseService.create(user, courseDTO, imageFile);
            courseService.saveCourseCategories(courseDTO, savedCourse);
            courseService.saveCourseModules(courseDTO, savedCourse);
            courseService.saveCourseMaterials(savedCourse, materialFiles);
        } catch (Exception e) {

            log.error("Create course failed", e);
            return "redirect:/instructor/dashboard?createFailed=true";

        }

        return "redirect:/instructor/dashboard?createSuccess=true";
    }

    private String loadPage(Model model) {
        List<Category> categories = categoryService.findAll();
        model.addAttribute("title", "Tạo khóa học");
        model.addAttribute("content", "client/course/create-course");
        model.addAttribute("scripts", "client/course/create-course");
        model.addAttribute("categories", categories);
        return "client/layout/index";
    }

}
