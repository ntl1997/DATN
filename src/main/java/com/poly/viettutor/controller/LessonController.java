package com.poly.viettutor.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.poly.viettutor.model.Course;
import com.poly.viettutor.service.CourseService;

@Controller
public class LessonController {
    private final CourseService courseService;

    public LessonController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/lesson/{id}")
    public String getById(@PathVariable("id") int id, Model model) {
        Optional<Course> courseOpt = courseService.findById(id);
        if (courseOpt.isEmpty()) {
            return "redirect:/404";
        }
        Course course = courseOpt.get();
        model.addAttribute("title", "Nội dung khóa học"); // tiêu đề trang (title)
        model.addAttribute("content", "client/learning/lesson"); // nội dung trang (phần content)
        model.addAttribute("course", course);
        model.addAttribute("scripts", "client/learning/lesson");
        return "client/layout/index";
    }

}
