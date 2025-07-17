package com.poly.viettutor.service;

import com.poly.viettutor.model.Course;
import com.poly.viettutor.model.CourseCategory;
import com.poly.viettutor.model.CourseMaterial;
import com.poly.viettutor.model.CourseModule;
import com.poly.viettutor.model.Lecture;
import com.poly.viettutor.repository.CourseRepository;
import com.poly.viettutor.repository.CourseSpecification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import com.poly.viettutor.model.User;
import com.poly.viettutor.dto.CourseDTO;
import com.poly.viettutor.model.Category;
import com.poly.viettutor.repository.*;
import com.poly.viettutor.utils.FileUtils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseCategoryRepository courseCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final CourseModuleRepository courseModuleRepository;
    private final LectureRepository lectureRepository;
    private final CourseMaterialRepository courseMaterialRepository;

    CourseService(CourseRepository courseRepository,
            CourseCategoryRepository courseCategoryRepository,
            CategoryRepository categoryRepository,
            CourseModuleRepository courseModuleRepository,
            LectureRepository lectureRepository,
            CourseMaterialRepository courseMaterialRepository) {
        this.courseRepository = courseRepository;
        this.courseCategoryRepository = courseCategoryRepository;
        this.categoryRepository = categoryRepository;
        this.courseModuleRepository = courseModuleRepository;
        this.lectureRepository = lectureRepository;
        this.courseMaterialRepository = courseMaterialRepository;
    }

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public Optional<Course> findById(Integer id) {
        return courseRepository.findById(id);
    }

    public Course create(User user, CourseDTO courseDTO, MultipartFile imageFile) throws IOException {
        String fileName = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            fileName = FileUtils.saveImage(imageFile, "uploads/course/");
        }

        Course course = Course.builder()
                .title(courseDTO.getTitle())
                .description(courseDTO.getDescription())
                .overview(courseDTO.getOverview())
                .price(courseDTO.getPrice())
                .discount(courseDTO.getDiscount())
                .courseImage(fileName)
                .demoVideoUrl(courseDTO.getDemoVideoUrl())
                .status("pending")
                .skillLevel(courseDTO.getSkillLevel())
                .hasCertificate(courseDTO.getHasCertificate())
                .language(courseDTO.getLanguage())
                .updatedAt(new Date())
                .createdAt(new Date())
                .createdBy(user)
                .build();
        return courseRepository.save(course);
    }

    public void saveCourseCategories(CourseDTO courseDTO, Course savedCourse) {
        courseDTO.getCategoryIds().forEach(id -> {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            CourseCategory courseCategory = CourseCategory.builder()
                    .category(category)
                    .course(savedCourse)
                    .build();
            courseCategoryRepository.save(courseCategory);
        });
    }

    public void saveCourseModules(CourseDTO courseDTO, Course savedCourse) {
        // Lưu các chương của khóa học
        AtomicInteger moduleIndex = new AtomicInteger(1);
        courseDTO.getModules().forEach(moduleDTO -> {
            CourseModule module = CourseModule.builder()
                    .moduleTitle(moduleDTO.getModuleTitle())
                    .sortOrder(moduleIndex.getAndIncrement())
                    .course(savedCourse)
                    .build();
            CourseModule savedModule = courseModuleRepository.save(module);

            // Lưu các bài giảng của chương
            AtomicInteger lectureIndex = new AtomicInteger(1);
            moduleDTO.getLectures().forEach(lectureDTO -> {
                Lecture lecture = Lecture.builder()
                        .lectureTitle(lectureDTO.getLectureTitle())
                        .content(lectureDTO.getContent())
                        .videoUrl(lectureDTO.getVideoUrl())
                        .duration(lectureDTO.getDuration())
                        .sortOrder(lectureIndex.getAndIncrement())
                        .module(savedModule)
                        .build();
                lectureRepository.save(lecture);
            });
        });
    }

    public void saveCourseMaterials(Course savedCourse, MultipartFile[] materialFiles) throws IOException {
        if (materialFiles != null) {
            for (MultipartFile file : materialFiles) {
                if (!file.isEmpty()) {
                    String fileName = FileUtils.saveFile(file, "uploads/course-materials/");
                    CourseMaterial material = CourseMaterial.builder()
                            .course(savedCourse)
                            .fileName(fileName)
                            .fileUrl("/uploads/course-materials/" + fileName)
                            .fileType(file.getContentType())
                            .uploadedAt(new Date())
                            .build();
                    courseMaterialRepository.save(material);
                }
            }
        }
    }

    public void deleteById(Integer id) {
        courseRepository.deleteById(id);
    }

    public List<Course> getTop6PopularCourses() {
        return courseRepository.findTop6PopularCourses(PageRequest.of(0, 6));
    }

    // Lấy dữ liệu Course phân trang
    public Page<Course> findAll(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }

    // Tính tổng thời gian của tất cả các bài giảng trong khóa học
    public int totalDuration(Course course) {
        if (course.getModules() == null) {
            return 0;
        }
        return course.getModules().stream()
                .filter(module -> module.getLectures() != null)
                .flatMap(module -> module.getLectures().stream())
                .mapToInt(Lecture::getDuration)
                .sum();
    }

    public Page<Course> searchCourses(
            String keyword,
            List<String> categories,
            List<Integer> ratings,
            String instructor,
            String priceType,
            Pageable pageable) {
        return courseRepository.findAll(
                CourseSpecification.filterCourses(keyword, categories, ratings, instructor, priceType),
                pageable);
    }

    public long countCoursesByUser(User user) {
        return courseRepository.countCoursesByUserId(user.getId());
    }

    public List<Object[]> getCourseSummaryByInstructor(Long instructorId) {
        return courseRepository.findCourseSummaryByInstructorNative(instructorId);
    }

    public List<Course> findCoursesByInstructorIdAndStatus(Long instructorId, String status) {
        return courseRepository.findByCreatedByIdAndStatus(instructorId, status);
    }

}
