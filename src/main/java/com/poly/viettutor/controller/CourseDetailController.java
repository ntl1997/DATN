package com.poly.viettutor.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.poly.viettutor.model.Course;
import com.poly.viettutor.service.CourseService;

@Controller
public class CourseDetailController {

    private final CourseService courseService;

    public CourseDetailController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/detail/{id}")
    public String getById(@PathVariable("id") int id, Model model) {
        Optional<Course> existingItemOptional = courseService.findById(id);
        if (existingItemOptional.isPresent()) {
            Course course = existingItemOptional.get();
            model.addAttribute("course", course); // Thêm danh sách mục tiêu khóa học vào mô hình
            int totalDuration = courseService.totalDuration(course);
            model.addAttribute("totalDuration", totalDuration); // Tổng thời gian của khóa học
        } else {
            // Xử lý khi không tìm thấy khóa học, ví dụ chuyển hướng hoặc báo lỗi
            return "redirect:/error";
        }
        model.addAttribute("title", "Chi tiết khóa học"); // tiêu đề trang (title)
        model.addAttribute("content", "client/course-detail"); // nội dung trang (phần content)
        model.addAttribute("scripts", "client/course-detail");
        return "client/layout/index";
    }

}
