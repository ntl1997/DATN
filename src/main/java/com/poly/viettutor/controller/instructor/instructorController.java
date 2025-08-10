package com.poly.viettutor.controller.instructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.poly.viettutor.model.Course;
import com.poly.viettutor.model.User;
import com.poly.viettutor.service.CourseService;
import com.poly.viettutor.service.EnrollmentService;
import com.poly.viettutor.service.QuizService;
import com.poly.viettutor.service.UserService;

@Controller
public class instructorController {

    private final UserService userService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final QuizService quizService;

    public instructorController(UserService userService, CourseService courseService,
            EnrollmentService enrollmentService, QuizService quizService) {
        this.userService = userService;
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
        this.quizService = quizService;
    }

    @GetMapping("/instructor/dashboard")
    public String instructorDashboard(Model model) {
        User currentUser = userService.getCurrentUser();

        long courseCount = 0L;
        long studentCount = 0L;

        courseCount = courseService.countCoursesByUser(currentUser);
        studentCount = enrollmentService.countStudentsByInstructor(currentUser);
        model.addAttribute("user", currentUser);
        Long instructorId = currentUser.getId();
        List<Object[]> courseSummary = courseService.getCourseSummaryByInstructor(instructorId);
        model.addAttribute("title", "Trang giảng viên");
        model.addAttribute("courseCount", courseCount);
        model.addAttribute("courseSummary", courseSummary);
        model.addAttribute("studentCount", studentCount);
        model.addAttribute("content", "client/instructor/instructor-dashboard");

        return "client/layout/index";
    }

    @GetMapping("/instructor/courses")
    public String instructorCourses(Model model) {
        User currentUser = userService.getCurrentUser();
        List<Course> publishCourses = courseService.findCoursesByInstructorIdAndStatus(currentUser.getId(),
                "publish");
        List<Course> pendingCourses = courseService.findCoursesByInstructorIdAndStatus(currentUser.getId(),
                "pending");
        List<Course> draftCourses = courseService.findCoursesByInstructorIdAndStatus(currentUser.getId(),
                "draft");
        List<Course> hiddenCourses = courseService.findCoursesByInstructorIdAndStatus(currentUser.getId(),
                "hidden");
        model.addAttribute("user", currentUser);
        model.addAttribute("publishCourses", publishCourses);
        model.addAttribute("pendingCourses", pendingCourses);
        model.addAttribute("draftCourses", draftCourses);
        model.addAttribute("hiddenCourses", hiddenCourses);
        model.addAttribute("title", "Khóa học của tôi");
        model.addAttribute("content", "client/instructor/instructor-course");
        model.addAttribute("scripts", "client/instructor/instructor-course");
        return "client/layout/index";
    }

    @GetMapping("/instructor/announcements")
    public String instructorAnnouncements(Model model) {
        User currentUser = userService.getCurrentUser();

        model.addAttribute("user", currentUser);
        model.addAttribute("title", "Thông báo");
        model.addAttribute("content", "client/instructor/instructor-announcements");

        return "client/layout/index";
    }

    @GetMapping("/instructor/instructor-quiz-attempts")
    public String instructorQuizAttempts(
            @RequestParam(name = "courseTitle", required = false) String courseTitle,
            Model model) {

        // System.out.println("Course Title = " + courseTitle);

        User currentUser = userService.getCurrentUser();

        // Lấy danh sách khóa học đã publish của instructor
        List<Course> courses = courseService.findCoursesByInstructorIdAndStatus(currentUser.getId(), "Publish");

        // for (Course course : courses) {
        // System.out.println("Course: " + course.getTitle());
        // }

        model.addAttribute("courses", courses);
        model.addAttribute("user", currentUser);

        List<Map<String, Object>> quizSubmissions;

        // Lấy quiz submissions dựa trên instructorId, không cần điều kiện courseTitles
        if (courseTitle != null && !courseTitle.isEmpty()) {
            quizSubmissions = quizService.getQuizSubmissionsByCourseTitle(courseTitle);
        } else {
            // Nếu không chọn gì, lấy toàn bộ quiz submissions theo instructorId
            quizSubmissions = quizService.getQuizSubmissionsByInstructorId(currentUser.getId());
        }

        // if (quizSubmissions.isEmpty()) {
        // System.out.println("Quiz submissions list is empty");
        // } else {
        // quizSubmissions.forEach(submission -> System.out.println("Submission: " +
        // submission));
        // }
        model.addAttribute("courseTitles", courseTitle);
        model.addAttribute("quizSubmissions", quizSubmissions);
        model.addAttribute("title", "Lịch sử Quizz của học sinh");
        model.addAttribute("content", "client/instructor/instructor-quiz-attempts");

        return "client/layout/index";
    }

    @GetMapping("/instructor/thongKeQuizz")
    public String instructorThongKeQuizz(
            @RequestParam(value = "courseTitle", required = false) String courseTitle,
            Model model) {

        User currentUser = userService.getCurrentUser();
        List<Course> courses = courseService.findCoursesByInstructorIdAndStatus(currentUser.getId(), "Publish");

        model.addAttribute("user", currentUser);
        model.addAttribute("courses", courses);
        model.addAttribute("courseTitle", courseTitle); // Truyền param lên view

        if (courseTitle != null && !courseTitle.isEmpty()) {
            List<Object[]> quizProgressList = quizService.getQuizProgressByCourseTitle(courseTitle);
            model.addAttribute("quizProgressList", quizProgressList);
        } else {
            model.addAttribute("quizProgressList", new ArrayList<>());
        }

        model.addAttribute("title", "Thống kê Quizz");
        model.addAttribute("content", "client/instructor/thongKeQuizz");

        return "client/layout/index";
    }

}
