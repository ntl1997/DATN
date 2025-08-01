package com.poly.viettutor.controller.admin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import com.poly.viettutor.service.EnrollmentService;
import com.poly.viettutor.service.UserService;

import lombok.extern.slf4j.Slf4j;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.poly.viettutor.model.CourseOffering;
import com.poly.viettutor.model.Enrollment;
import com.poly.viettutor.model.User;
import com.poly.viettutor.service.CourseOfferingService;

@Slf4j
@Controller
public class AdminEnrollController {

    private final CourseOfferingService courseOfferingService;
    private final EnrollmentService enrollmentService;
    private final UserService userService;

    public AdminEnrollController(EnrollmentService enrollmentService, UserService userService,
            CourseOfferingService courseOfferingService) {
        this.enrollmentService = enrollmentService;
        this.userService = userService;
        this.courseOfferingService = courseOfferingService;
    }

    @GetMapping("/admin/course-enroll")
    public String showEnrollCourses(Model model) {
        model.addAttribute("title", "Thêm vào khóa học");
        model.addAttribute("content", "admin/enroll/course-enroll");
        model.addAttribute("scripts", "admin/enroll/course-enroll");
        model.addAttribute("courseOfferings", courseOfferingService.findAll());
        return "admin/layout/index";
    }

    @GetMapping("/admin/course-enroll/{id}")
    public String showEnrollCourseUsers(@PathVariable("id") int offeringId, Model model) {
        CourseOffering courseOffering = courseOfferingService.findById(offeringId)
                .orElseThrow(() -> new RuntimeException("Course offering not found"));
        List<User> users = userService.getAllStudents();
        model.addAttribute("title", "Danh sách học viên");
        model.addAttribute("content", "admin/enroll/course-enroll-user");
        model.addAttribute("scripts", "admin/enroll/course-enroll-user");
        model.addAttribute("courseOffering", courseOffering);
        model.addAttribute("users", users);
        return "admin/layout/index";
    }

    @PostMapping("/admin/add-users-to-course")
    public String addUsersToCourse(@RequestParam int offeringId, @RequestParam int[] userIds) {
        CourseOffering courseOffering = courseOfferingService.findById(offeringId)
                .orElseThrow(() -> new RuntimeException("Course offering not found"));
        for (int userId : userIds) {
            User user = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            Optional<Enrollment> existingEnrollment = enrollmentService.findByUserAndCourseOffering(user,
                    courseOffering);

            // Nếu người dùng chưa được thêm vào khóa học, thêm vào bảng Enrollments
            if (!existingEnrollment.isPresent()) {
                Enrollment enrollment = new Enrollment();
                enrollment.setUser(user);
                enrollment.setCourseOffering(courseOffering);
                enrollment.setEnrolledAt(new Date());
                enrollment.setEnrolledBy(userService.getCurrentUser()); // Gán người admin đã thêm
                enrollmentService.save(enrollment);
            }
        }

        return "redirect:/admin/course-enroll/" + offeringId; // Redirect về danh sách học viên của khóa học
    }

    @DeleteMapping("/admin/course-enroll/delete")
    public String deleteUserFormCourse(@RequestParam int enrollmentId, @RequestParam int offeringId) {
        Optional<Enrollment> existingEnrollment = enrollmentService.findById(enrollmentId);
        if (existingEnrollment.isPresent()) {
            enrollmentService.deleteById(enrollmentId);
        }
        return "redirect:/admin/course-enroll/" + offeringId;
    }

    @PostMapping("/admin/import-excel-users-to-course")
    public String bulkAddUsersToCourse(@RequestParam("file") MultipartFile file, @RequestParam int offeringId) {
        try {
            CourseOffering courseOffering = courseOfferingService.findById(offeringId)
                    .orElseThrow(() -> new RuntimeException("Course offering not found"));

            // Đọc dữ liệu từ file Excel
            InputStream inputStream = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            // Duyệt qua các dòng trong sheet
            Iterator<Row> rowIterator = sheet.iterator();
            rowIterator.next(); // Bỏ qua dòng tiêu đề

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                String email = row.getCell(2).getStringCellValue();
                User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
                Optional<Enrollment> existingEnrollment = enrollmentService.findByUserAndCourseOffering(user,
                        courseOffering);

                // Nếu chưa có, thêm vào bảng Enrollments
                if (!existingEnrollment.isPresent()) {
                    Enrollment enrollment = new Enrollment();
                    enrollment.setUser(user);
                    enrollment.setCourseOffering(courseOffering);
                    enrollment.setEnrolledAt(new Date());
                    enrollment.setEnrolledBy(userService.getCurrentUser());
                    enrollmentService.save(enrollment);
                }
            }

            workbook.close();
            return "redirect:/admin/course-enroll/" + offeringId;
        } catch (Exception e) {
            log.error("add user by excel fail", e);
            return "redirect:/admin/course-enroll/" + offeringId + "?error=true";
        }
    }

    @GetMapping("/admin/download-excel-enrolled-users/{id}")
    public ResponseEntity<InputStreamResource> downloadExcelTemplate(@PathVariable("id") int offeringId)
            throws IOException {
        CourseOffering courseOffering = courseOfferingService.findById(offeringId)
                .orElseThrow(() -> new RuntimeException("Course offering not found"));

        // Tạo Workbook và Sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Học viên");

        // Tạo dòng đầu tiên cho các tiêu đề cột
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("STT");
        headerRow.createCell(1).setCellValue("Họ tên");
        headerRow.createCell(2).setCellValue("Email");
        headerRow.createCell(3).setCellValue("SĐT");
        headerRow.createCell(4).setCellValue("Học kỳ");
        headerRow.createCell(5).setCellValue("Lớp");

        // Tạo dữ liệu mẫu (có thể bỏ qua hoặc thay thế nếu cần)
        List<Enrollment> enrollments = courseOffering.getEnrollments();
        AtomicInteger rowIndex = new AtomicInteger(1);
        for (Enrollment enrollment : enrollments) {
            Row row = sheet.createRow(rowIndex.getAndIncrement());
            row.createCell(0).setCellValue(enrollment.getUser().getId());
            row.createCell(1).setCellValue(enrollment.getUser().getFullname());
            row.createCell(2).setCellValue(enrollment.getUser().getEmail());
            row.createCell(3).setCellValue(enrollment.getUser().getPhoneNumber());
            row.createCell(4).setCellValue(courseOffering.getSemester());
            row.createCell(5).setCellValue(courseOffering.getClassName());
        }

        // Ghi file ra ByteArrayOutputStream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);
        workbook.close();

        // Đưa file về cho người dùng tải xuống
        InputStreamResource resource = new InputStreamResource(
                new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=danh_sach_sinh_vien.xlsx")
                .contentType(org.springframework.http.MediaType
                        .parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
    }

}
