package com.poly.viettutor.controller.admin;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.poly.viettutor.model.Course;
import com.poly.viettutor.model.User;
import com.poly.viettutor.repository.CourseRepository;
import com.poly.viettutor.repository.QuizAnswerRepository;
import com.poly.viettutor.service.CourseService;
import com.poly.viettutor.service.UserService;

@Controller
public class AdminThongKeController {

    @Autowired
    private CourseService courseService;
    @Autowired
    private UserService userService;
    @Autowired
    private QuizAnswerRepository quizAnswerRepo;

    @Autowired
    private CourseRepository courseRepo;

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
        List<User> user = userService.findAll();
        model.addAttribute("users", user);
        model.addAttribute("totalUsers", user.size());
        model.addAttribute("totalAdmins", userService.getAllAdmins().size());
        model.addAttribute("totalInstructors", userService.getAllInstructors().size());
        model.addAttribute("totalStudents", userService.getAllStudents().size());
        model.addAttribute("title", "Thống kê tài khoản");
        model.addAttribute("content", "admin/thongKe/tongTaiKhoan");
        model.addAttribute("scripts", "admin/thongKe/tongTaiKhoan");
        return "admin/layout/index";
    }

    @GetMapping("/admin/totalQuiz")
    public String thongKeQuiz(@RequestParam(name = "courseId", required = false) Long courseId,
            Model model) {
        List<Object[]> quizStats = quizAnswerRepo.getQuizStatsByCourseId(courseId);
        List<Course> courses = courseRepo.findAllPublished();

        model.addAttribute("quizStats", quizStats);
        model.addAttribute("courses", courses);
        model.addAttribute("selectedCourseId", courseId);
        model.addAttribute("title", "Thống kê quiz");
        model.addAttribute("content", "admin/thongKe/tyLeQuiz");
        model.addAttribute("scripts", "admin/thongKe/tyLeQuiz");
        return "admin/layout/index";
    }

}
