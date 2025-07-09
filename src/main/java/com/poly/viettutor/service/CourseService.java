package com.poly.viettutor.service;

import com.poly.viettutor.model.Course;
import com.poly.viettutor.model.CourseCategory;
import com.poly.viettutor.model.CourseModule;
import com.poly.viettutor.model.Lecture;
import com.poly.viettutor.model.User;
import com.poly.viettutor.dto.CreateCourseDTO;
import com.poly.viettutor.model.Category;
import com.poly.viettutor.repository.CategoryRepository;
import com.poly.viettutor.repository.CourseCategoryRepository;
import com.poly.viettutor.repository.CourseModuleRepository;
import com.poly.viettutor.repository.CourseRepository;
import com.poly.viettutor.repository.LectureRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

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

    CourseService(CourseRepository courseRepository,
            CourseCategoryRepository courseCategoryRepository,
            CategoryRepository categoryRepository,
            CourseModuleRepository courseModuleRepository,
            LectureRepository lectureRepository) {
        this.courseRepository = courseRepository;
        this.courseCategoryRepository = courseCategoryRepository;
        this.categoryRepository = categoryRepository;
        this.courseModuleRepository = courseModuleRepository;
        this.lectureRepository = lectureRepository;
    }

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public Optional<Course> findById(Integer id) {
        return courseRepository.findById(id);
    }

    public Course create(User user, CreateCourseDTO courseDTO, String fileName) {
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

        // Lưu khóa học
        Course savedCourse = courseRepository.save(course);

        // Lưu các danh mục của khóa học
        courseDTO.getCategoryIds().forEach(id -> {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            CourseCategory courseCategory = new CourseCategory();
            courseCategory.setCategory(category);
            courseCategory.setCourse(savedCourse);
            courseCategoryRepository.save(courseCategory);
        });

        // Lưu các chương của khóa học
        AtomicInteger moduleIndex = new AtomicInteger(1);
        courseDTO.getModules().forEach(moduleDTO -> {
            CourseModule module = new CourseModule();
            module.setModuleTitle(moduleDTO.getModuleTitle());
            module.setSortOrder(moduleIndex.getAndIncrement());
            module.setCourse(savedCourse);
            CourseModule savedModule = courseModuleRepository.save(module);

            // Lưu các bài giảng của chương
            AtomicInteger lectureIndex = new AtomicInteger(1);
            moduleDTO.getLectures().forEach(lectureDTO -> {
                Lecture lecture = new Lecture();
                lecture.setLectureTitle(lectureDTO.getLectureTitle());
                lecture.setContent(lectureDTO.getContent());
                lecture.setVideoUrl(lectureDTO.getVideoUrl());
                lecture.setDuration(lectureDTO.getDuration());
                lecture.setSortOrder(lectureIndex.getAndIncrement());
                lecture.setModule(savedModule);
                lectureRepository.save(lecture);
            });
        });

        return savedCourse;
    }

    public void deleteById(Integer id) {
        courseRepository.deleteById(id);
    }

    public List<Course> getTop6PopularCourses() {
        return courseRepository.findTop6PopularCourses(PageRequest.of(0, 6));
    }

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
}
