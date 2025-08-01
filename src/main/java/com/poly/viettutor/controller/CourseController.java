package com.poly.viettutor.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poly.viettutor.dto.CourseDTO;
import com.poly.viettutor.model.Category;
import com.poly.viettutor.model.Course;
import com.poly.viettutor.model.User;
import com.poly.viettutor.service.CategoryService;
import com.poly.viettutor.service.CourseService;
import com.poly.viettutor.service.UserService;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class CourseController {

    private final CourseService courseService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final Validator validator;

    public CourseController(CourseService courseService, CategoryService categoryService, UserService userService,
            Validator validator) {
        this.courseService = courseService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.validator = validator;
    }

    @GetMapping("/instructor/create-course")
    public String showCreateCourse(@ModelAttribute("course") CourseDTO courseDTO, Model model) {
        return loadPage(model, "Tạo khóa học", "client/course/create-course");
    }

    @PostMapping("/instructor/create-course")
    public String createCourse(@RequestParam("courseJson") String courseJson,
            @RequestParam(name = "createinputfile", required = false) MultipartFile imageFile,
            @RequestParam(name = "attachments", required = false) MultipartFile[] materialFiles,
            Model model) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            CourseDTO courseDTO = mapper.readValue(courseJson, CourseDTO.class);

            // Validate DTO
            DataBinder binder = new DataBinder(courseDTO);
            binder.setValidator(validator);
            binder.validate();
            BindingResult result = binder.getBindingResult();

            if (result.hasErrors()) {
                model.addAttribute("org.springframework.validation.BindingResult.course", result);
                model.addAttribute("course", courseDTO);
                return loadPage(model, "Tạo khóa học", "client/course/create-course");
            }

            User user = userService.getCurrentUser();
            courseService.create(user, courseDTO, imageFile, materialFiles);
        } catch (Exception e) {
            log.error("Create course failed", e);
            return "redirect:/instructor/dashboard?createFailed=true";
        }

        return "redirect:/instructor/dashboard?createSuccess=true";
    }

    @GetMapping("/instructor/edit-course/{id}")
    public String showEditCourse(@PathVariable Integer id, HttpServletRequest request, Model model) {
        Optional<Course> existingItemOptional = courseService.findById(id);

        // Xử lý khi không tìm thấy khóa học
        if (existingItemOptional.isEmpty()) {
            request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 404);
            return "forward:/error";
        }

        Course course = existingItemOptional.get();
        User user = userService.getCurrentUser();

        // CHẶN nếu không phải chủ sở hữu hoặc admin
        if (!isOwnerOrADmin(user, course)) {
            request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 403);
            return "forward:/error";
        }

        CourseDTO courseDTO = new CourseDTO().toDTO(course);
        model.addAttribute("course", courseDTO);
        return loadPage(model, "Chỉnh sửa khóa học", "client/course/course-edit");
    }

    @GetMapping("/instructor/request-approve-course/{id}")
    public String requestApproveCourse(@PathVariable Integer id, HttpServletRequest request, Model model) {
        Optional<Course> existingItemOptional = courseService.findById(id);

        // Xử lý khi không tìm thấy khóa học
        if (existingItemOptional.isEmpty()) {
            request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 404);
            return "forward:/error";
        }

        Course course = existingItemOptional.get();
        User user = userService.getCurrentUser();

        // CHẶN nếu không phải chủ sở hữu hoặc admin
        if (!isOwnerOrADmin(user, course)) {
            request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 403);
            return "forward:/error";
        }

        courseService.updateStatus(course, "pending");
        return "redirect:/instructor/courses";
    }

    @PutMapping("/instructor/update-course")
    public String updateCourse(
            @RequestParam("courseJson") String courseJson, HttpServletRequest request,
            @RequestParam(name = "createinputfile", required = false) MultipartFile imageFile,
            @RequestParam(name = "attachments", required = false) MultipartFile[] materialFiles,
            Model model) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            CourseDTO courseDTO = mapper.readValue(courseJson, CourseDTO.class);
            Optional<Course> existingItemOptional = courseService.findById(courseDTO.getCourseId());

            // Xử lý khi không tìm thấy khóa học
            if (existingItemOptional.isEmpty()) {
                request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 404);
                return "forward:/error";
            }

            Course course = existingItemOptional.get();
            User user = userService.getCurrentUser();

            // CHẶN nếu không phải chủ sở hữu hoặc admin
            if (!isOwnerOrADmin(user, course)) {
                request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 403);
                return "forward:/error";
            }

            // Không cho cập nhật khóa học khi đã công khai
            if (course.getStatus().equalsIgnoreCase("publish")) {
                request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 403);
                return "forward:/error";
            }

            // Validate DTO
            DataBinder binder = new DataBinder(courseDTO);
            binder.setValidator(validator);
            binder.validate();
            BindingResult result = binder.getBindingResult();

            if (result.hasErrors()) {
                model.addAttribute("org.springframework.validation.BindingResult.course", result);
                model.addAttribute("course", courseDTO);
                return loadPage(model, "Chỉnh sửa khóa học", "client/course/course-edit");
            }

            courseService.updateCourse(user, courseDTO, imageFile, materialFiles);
        } catch (Exception e) {
            log.error("Update course failed", e);
            return "redirect:/instructor/dashboard?updateFailed=true";
        }

        return "redirect:/instructor/dashboard?updateSuccess=true";
    }

    @PostMapping("/instructor/clone-course")
    public String cloneCourse(@RequestParam int courseId, Model model) {
        try {
            User user = userService.getCurrentUser();
            int newCourseId = courseService.cloneCourse(courseId, user).getCourseId();
            return "redirect:/instructor/edit-course/" + newCourseId;
        } catch (Exception e) {
            log.error("Clone course failed", e);
            return "redirect:/instructor/dashboard?cloneFailed=true";
        }
    }

    private boolean isOwnerOrADmin(User user, Course course) {
        boolean isAdmin = userService.hasRole(user, "ADMIN");
        boolean isOwner = user.getId() == course.getCreatedBy().getId();
        return isOwner || isAdmin;
    }

    private String loadPage(Model model, String title, String viewPath) {
        List<Category> categories = categoryService.findAll();
        model.addAttribute("title", title);
        model.addAttribute("content", viewPath);
        model.addAttribute("scripts", viewPath);
        model.addAttribute("categories", categories);
        return "client/layout/index";
    }

}
