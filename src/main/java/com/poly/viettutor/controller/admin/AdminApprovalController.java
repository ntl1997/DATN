package com.poly.viettutor.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.poly.viettutor.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
@RequestMapping("/admin/function")
public class AdminApprovalController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/course-approval")
    public String adminDashboard(Model model) {
        model.addAttribute("title", "Phê duyệt khóa học");
        model.addAttribute("content", "admin/approval/course-approval");
        model.addAttribute("scripts", "admin/approval/course-approval");
        // Lấy danh sách khoá học trạng thái "pending"
        model.addAttribute("courses", courseService.findAll());
        return "admin/layout/index";
    }

    @PostMapping("/course-hide")
    public String hideCourse(@RequestParam Integer courseId, @RequestParam String note) {
        courseService.updateCourseStatus(courseId, "hidden", note);
        return "redirect:/admin/function/course-approval";
    }

    @PostMapping("/course-publish")
    public String publishCourse(@RequestParam Integer courseId) {
        courseService.updateCourseStatus(courseId, "publish", null);
        return "redirect:/admin/function/course-approval";
    }
}
