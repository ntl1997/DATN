package com.poly.viettutor.controller.admin;

import com.poly.viettutor.model.Course;
import com.poly.viettutor.model.CourseApproval;
import com.poly.viettutor.model.User;
import com.poly.viettutor.service.CourseApprovalService;
import com.poly.viettutor.service.CourseService;
import com.poly.viettutor.service.UserService;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class AdminCourseController {

    private final UserService userService;

    private final CourseApprovalService courseApprovalService;
    private final CourseService courseService;

    AdminCourseController(CourseService courseService, CourseApprovalService courseApprovalService,
            UserService userService) {
        this.courseService = courseService;
        this.courseApprovalService = courseApprovalService;
        this.userService = userService;
    }

    @GetMapping("/admin/courses")
    public String showCourses(Model model) {
        model.addAttribute("title", "Quản lý khóa học");
        model.addAttribute("content", "admin/course/list-course");
        model.addAttribute("scripts", "admin/course/list-course");
        model.addAttribute("courses", courseService.findAll());
        return "admin/layout/index";
    }

    @GetMapping("/admin/course-approvals")
    public String showCourseApprovals(Model model) {
        model.addAttribute("title", "Yêu cầu phê duyệt");
        model.addAttribute("content", "admin/course/list-course-approval");
        model.addAttribute("scripts", "admin/course/list-course-approval");
        model.addAttribute("approvals", courseApprovalService.findAll());
        return "admin/layout/index";
    }

    @PutMapping("/admin/course/changeStatus/{id}")
    public String changeStatusCourse(@PathVariable("id") int id, @RequestParam("status") String status) {
        try {
            Optional<Course> courseOptional = courseService.findById(id);

            if (courseOptional.isEmpty()) {
                return "redirect:/admin/courses?notFound=true";
            }

            Course course = courseOptional.get();
            courseService.updateStatus(course, status);
        } catch (Exception e) {
            return "redirect:/admin/courses?updateError=true";
        }

        return "redirect:/admin/courses?updateSuccess=true";
    }

    @PutMapping("/admin/course-approval/response/{id}")
    public String responseCourseApproval(@PathVariable("id") int id, @RequestParam("status") String status,
            @RequestParam("note") String note) {
        try {
            Optional<CourseApproval> courseApprovalOptional = courseApprovalService.findById(id);

            if (courseApprovalOptional.isEmpty()) {
                return "redirect:/admin/course-approvals?notFound=true";
            }

            User user = userService.getCurrentUser();
            CourseApproval courseApproval = courseApprovalOptional.get();
            courseApprovalService.response(user, courseApproval, status, note);
        } catch (Exception e) {
            return "redirect:/admin/course-approvals?updateError=true";
        }

        return "redirect:/admin/course-approvals?updateSuccess=true";
    }

}
