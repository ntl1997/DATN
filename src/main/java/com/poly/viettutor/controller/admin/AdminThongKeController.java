package com.poly.viettutor.controller.admin;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.poly.viettutor.model.User;
import com.poly.viettutor.service.CourseService;
import com.poly.viettutor.service.UserService;

@Controller
public class AdminThongKeController {

    @Autowired
    private CourseService courseService;
    @Autowired
    private UserService userService;

    @GetMapping("/admin/KhoaHocDuocHocNhieuNhat")
    public String showKhoaHoc(Model model) {
        Map<String, Object> chartData = courseService.getTop5CoursesChartData();

        model.addAttribute("courseLabels", chartData.get("labels"));
        model.addAttribute("courseData", chartData.get("data"));
        model.addAttribute("title", "Danh sách khóa học được học nhiều nhất");
        model.addAttribute("content", "admin/thongKe/khoaHocNhieuNhat");
        model.addAttribute("scripts", "admin/thongKe/khoaHocNhieuNhat");

        List<Map<String, Object>> topCourses = courseService.getTop5PopularCourses();
        model.addAttribute("topCourses", topCourses);
        return "admin/layout/index";
    }

    @GetMapping("/admin/Top5GiangVien")
    public String showGiangVien(Model model) {
        Map<String, Object> chartData = userService.getTop5InstructorsForChart();

        model.addAttribute("teacherNames", chartData.get("labels"));
        model.addAttribute("studentCounts", chartData.get("data"));
        model.addAttribute("title", "Danh sách giảng viên được học nhiều nhất");
        model.addAttribute("content", "admin/thongKe/giangVienDuocHocNhieuNhat");
        model.addAttribute("scripts", "admin/thongKe/giangVienDuocHocNhieuNhat");

        List<Map<String, Object>> topInstructors = userService.getTop5Ints();
        model.addAttribute("topInstructors", topInstructors);
        return "admin/layout/index";
    }

    @GetMapping("/admin/totalUsers")
    public String showTaiKhoan(Model model) {
        List<User> user = userService.getAllInstructorsAndStudents();
        model.addAttribute("users", user);
        model.addAttribute("totalUsers", user.size());
        model.addAttribute("totalAdmins", userService.getAllAdmins().size());
        model.addAttribute("totalInstructors", userService.getAllInstructors().size());
        model.addAttribute("totalStudents", userService.getAllStudents().size());
        model.addAttribute("title", "Tài khoản");
        model.addAttribute("content", "admin/thongKe/tongTaiKhoan");
        model.addAttribute("scripts", "admin/thongKe/tongTaiKhoan");
        return "admin/layout/index";
    }

    @GetMapping("/admin/totalQuiz")
    public String showKqQuiz(Model model) {
        List<User> user = userService.getAllInstructorsAndStudents();
        model.addAttribute("users", user);
        model.addAttribute("totalUsers", user.size());
        model.addAttribute("totalAdmins", userService.getAllAdmins().size());
        model.addAttribute("totalInstructors", userService.getAllInstructors().size());
        model.addAttribute("totalStudents", userService.getAllStudents().size());
        model.addAttribute("title", "Tài khoản");
        model.addAttribute("content", "admin/thongKe/tongTaiKhoan");
        model.addAttribute("scripts", "admin/thongKe/tongTaiKhoan");
        return "admin/layout/index";
    }

}
