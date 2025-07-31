package com.poly.viettutor.service;

import com.poly.viettutor.model.Course;
import com.poly.viettutor.model.CourseCategory;
import com.poly.viettutor.model.CourseMaterial;
import com.poly.viettutor.model.CourseModule;
import com.poly.viettutor.model.Lecture;

import com.poly.viettutor.model.Option;
import com.poly.viettutor.model.Question;
import com.poly.viettutor.model.Quiz;

import org.springframework.data.domain.Page;

import com.poly.viettutor.model.User;
import com.poly.viettutor.dto.CourseDTO;
import com.poly.viettutor.dto.ModuleDTO;
import com.poly.viettutor.dto.QuestionDTO;
import com.poly.viettutor.dto.QuizDTO;
import com.poly.viettutor.model.Category;
import com.poly.viettutor.repository.*;
import com.poly.viettutor.utils.FileUtils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Objects;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseCategoryRepository courseCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final CourseModuleRepository courseModuleRepository;
    private final LectureRepository lectureRepository;
    private final CourseMaterialRepository courseMaterialRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final UserRepository userRepository;

    CourseService(CourseRepository courseRepository,
            CourseCategoryRepository courseCategoryRepository,
            CategoryRepository categoryRepository,
            CourseModuleRepository courseModuleRepository,
            LectureRepository lectureRepository,
            CourseMaterialRepository courseMaterialRepository,
            QuizRepository quizRepository,
            QuestionRepository questionRepository,
            OptionRepository optionRepository,
            UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.courseCategoryRepository = courseCategoryRepository;
        this.categoryRepository = categoryRepository;
        this.courseModuleRepository = courseModuleRepository;
        this.lectureRepository = lectureRepository;
        this.courseMaterialRepository = courseMaterialRepository;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
        this.userRepository = userRepository;
    }

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public List<Course> findByStatus(String status) {
        return courseRepository.findByStatus(status);
    }

    public Optional<Course> findById(Integer id) {
        return courseRepository.findById(id);
    }

    public Course create(User user, CourseDTO courseDTO, MultipartFile imageFile, MultipartFile[] materialFiles)
            throws IOException {
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
                .status("draft")
                .skillLevel(courseDTO.getSkillLevel())
                .hasCertificate(courseDTO.getHasCertificate())
                .language(courseDTO.getLanguage())
                .updatedAt(new Date())
                .createdAt(new Date())
                .createdBy(user)
                .build();
        Course savedCourse = courseRepository.save(course);
        saveCourseCategories(courseDTO, savedCourse);
        saveCourseModules(courseDTO, savedCourse);
        saveCourseMaterials(savedCourse, materialFiles);
        return savedCourse;
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
            saveLectures(moduleDTO, savedModule);
            saveQuizzes(moduleDTO, savedModule);
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

    public void saveLectures(ModuleDTO moduleDTO, CourseModule savedModule) {
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
    }

    public void saveQuizzes(ModuleDTO moduleDTO, CourseModule savedModule) {
        moduleDTO.getQuizzes().forEach(quizDTO -> {
            Quiz quiz = Quiz.builder()
                    .module(savedModule)
                    .title(quizDTO.getTitle())
                    .totalScore(quizDTO.getTotalScore().intValue())
                    .timeLimit(quizDTO.getTimeLimit())
                    .quizType(quizDTO.getQuizType())
                    .build();
            Quiz savedQuiz = quizRepository.save(quiz);
            saveQuestions(quizDTO, savedQuiz);
        });
    }

    public void saveQuestions(QuizDTO quizDTO, Quiz savedQuiz) {
        quizDTO.getQuestions().forEach(questionDTO -> {
            Question question = Question.builder()
                    .quiz(savedQuiz)
                    .questionText(questionDTO.getQuestionText())
                    .score(questionDTO.getScore().intValue())
                    .build();
            Question savedQuestion = questionRepository.save(question);
            saveOptions(questionDTO, savedQuestion);
        });
    }

    public void saveOptions(QuestionDTO questionDTO, Question savedQuestion) {
        questionDTO.getOptions().forEach(optionDTO -> {
            Option option = Option.builder()
                    .question(savedQuestion)
                    .optionText(optionDTO.getOptionText())
                    .isCorrect(optionDTO.getIsCorrect())
                    .build();
            optionRepository.save(option);
        });
    }

    @Transactional
    public Course updateCourse(User user, CourseDTO courseDTO, MultipartFile imageFile, MultipartFile[] materialFiles)
            throws IOException {

        Course course = courseRepository.findById(courseDTO.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Cập nhật thông tin cơ bản
        course.setTitle(courseDTO.getTitle());
        course.setDescription(courseDTO.getDescription());
        course.setOverview(courseDTO.getOverview());
        course.setPrice(courseDTO.getPrice());
        course.setDiscount(courseDTO.getDiscount());
        course.setDemoVideoUrl(courseDTO.getDemoVideoUrl());
        course.setSkillLevel(courseDTO.getSkillLevel());
        course.setLanguage(courseDTO.getLanguage());
        course.setHasCertificate(courseDTO.getHasCertificate());
        course.setUpdatedAt(new Date());

        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = FileUtils.saveImage(imageFile, "uploads/course/");
            course.setCourseImage(fileName);
        }

        courseRepository.save(course);

        // Cập nhật categories (xóa cũ → thêm lại)
        courseCategoryRepository.deleteByCourse(course);
        saveCourseCategories(courseDTO, course);

        // Cập nhật modules, lectures, quizzes
        updateCourseModules(courseDTO, course);

        // Cập nhật tài liệu đính kèm (xóa cũ → thêm mới)
        courseMaterialRepository.deleteByCourse(course);
        saveCourseMaterials(course, materialFiles);

        return course;
    }

    public void updateCourseModules(CourseDTO courseDTO, Course course) {
        List<CourseModule> existingModules = courseModuleRepository.findByCourse(course);

        // Map khóa theo Long (moduleId)
        Map<Long, CourseModule> moduleMap = existingModules.stream()
                .filter(m -> m.getModuleId() != null)
                .collect(Collectors.toMap(CourseModule::getModuleId, m -> m));

        // Tập ID module đang update, loại bỏ null
        Set<Long> updatedIds = courseDTO.getModules().stream()
                .map(ModuleDTO::getModuleId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Xóa module không còn trong danh sách update
        existingModules.stream()
                .filter(m -> !updatedIds.contains(m.getModuleId()))
                .forEach(m -> courseModuleRepository.delete(m));

        AtomicInteger moduleIndex = new AtomicInteger(1);

        for (ModuleDTO moduleDTO : courseDTO.getModules()) {
            CourseModule module;

            Long dtoModuleId = moduleDTO.getModuleId();
            if (dtoModuleId != null && moduleMap.containsKey(dtoModuleId)) {
                // Cập nhật module cũ
                module = moduleMap.get(dtoModuleId);
                module.setModuleTitle(moduleDTO.getModuleTitle());
                module.setSortOrder(moduleIndex.getAndIncrement());
                courseModuleRepository.save(module);

                // Xoá bài học & quiz cũ của module này
                lectureRepository.deleteByModule(module);
                quizRepository.deleteByModule(module);

            } else {
                // Tạo mới module
                module = CourseModule.builder()
                        .moduleTitle(moduleDTO.getModuleTitle())
                        .course(course)
                        .sortOrder(moduleIndex.getAndIncrement())
                        .build();
                module = courseModuleRepository.save(module);
            }

            // Thêm lectures mới
            saveLectures(moduleDTO, module);
            // Thêm quizzes mới
            saveQuizzes(moduleDTO, module);
        }
    }

    @Transactional
    public Course cloneCourse(Integer courseIdToClone, User currentInstructor) {
        Course original = courseRepository.findById(courseIdToClone)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // 1. Clone Course (chỉ dữ liệu cơ bản)
        Course cloned = Course.builder()
                .title(original.getTitle() + " - [Copy]")
                .description(original.getDescription())
                .overview(original.getOverview())
                .price(original.getPrice())
                .discount(original.getDiscount())
                .courseImage(original.getCourseImage())
                .demoVideoUrl(original.getDemoVideoUrl())
                .status("draft")
                .skillLevel(original.getSkillLevel())
                .hasCertificate(original.getHasCertificate())
                .language(original.getLanguage())
                .createdAt(new Date())
                .createdBy(currentInstructor)
                .build();

        courseRepository.save(cloned);

        // 2. Clone CourseMaterials
        for (CourseMaterial oldMaterial : original.getMaterials()) {
            CourseMaterial newMaterial = CourseMaterial.builder()
                    .course(cloned)
                    .fileName(oldMaterial.getFileName())
                    .fileUrl(oldMaterial.getFileUrl())
                    .fileType(oldMaterial.getFileType())
                    .uploadedAt(new Date())
                    .build();

            courseMaterialRepository.save(newMaterial);
        }

        // 3. Clone CourseModules
        for (CourseModule oldModule : original.getModules()) {
            CourseModule newModule = CourseModule.builder()
                    .moduleTitle(oldModule.getModuleTitle())
                    .sortOrder(oldModule.getSortOrder())
                    .course(cloned)
                    .build();

            courseModuleRepository.save(newModule);

            // 3. Clone Lectures
            for (Lecture oldLecture : oldModule.getLectures()) {
                Lecture newLecture = Lecture.builder()
                        .lectureTitle(oldLecture.getLectureTitle())
                        .content(oldLecture.getContent())
                        .videoUrl(oldLecture.getVideoUrl())
                        .sortOrder(oldLecture.getSortOrder())
                        .duration(oldLecture.getDuration())
                        .module(newModule)
                        .build();

                lectureRepository.save(newLecture);
            }

            // 4. Clone Quizzes
            for (Quiz oldQuiz : oldModule.getQuizzes()) {
                Quiz newQuiz = Quiz.builder()
                        .title(oldQuiz.getTitle())
                        .totalScore(oldQuiz.getTotalScore())
                        .timeLimit(oldQuiz.getTimeLimit())
                        .quizType(oldQuiz.getQuizType())
                        .createdAt(new Date())
                        .module(newModule)
                        .build();

                quizRepository.save(newQuiz);

                // 5. Clone Questions
                for (Question oldQuestion : oldQuiz.getQuestions()) {
                    Question newQuestion = Question.builder()
                            .questionText(oldQuestion.getQuestionText())
                            .score(oldQuestion.getScore())
                            .quiz(newQuiz)
                            .build();

                    questionRepository.save(newQuestion);

                    // 6. Clone Options
                    for (Option oldOption : oldQuestion.getOptions()) {
                        Option newOption = Option.builder()
                                .optionText(oldOption.getOptionText())
                                .isCorrect(oldOption.getIsCorrect())
                                .question(newQuestion)
                                .build();

                        optionRepository.save(newOption);
                    }
                }
            }
        }

        return cloned;
    }

    public void updateStatus(Course course, String status) {
        course.setStatus(status);
        courseRepository.save(course);
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

    public List<Map<String, Object>> getTop5PopularCourses() {
        List<Object[]> rows = courseRepository.findTop5CourseStatistics();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : rows) {
            Map<String, Object> map = new HashMap<>();
            map.put("courseTitle", row[0]);
            map.put("studentCount", row[1]);
            map.put("completionRate", row[2]);
            map.put("instructorName", row[3]);
            result.add(map);
        }

        return result;
    }

    public Map<String, Object> getTop5CoursesChartData() {
        List<Object[]> rawData = courseRepository.findTop5CoursesByStudentCount();

        List<String> courseTitles = new ArrayList<>();
        List<Long> studentCounts = new ArrayList<>();

        for (Object[] row : rawData) {
            courseTitles.add((String) row[0]);
            studentCounts.add(((Number) row[1]).longValue());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("labels", courseTitles);
        result.put("data", studentCounts);

        return result;
    }

    public void updateCourseStatus(Integer courseId, String status, String note, String email) {
        Optional<Course> optionalCourse = courseRepository.findById(courseId);
        if (optionalCourse.isPresent()) {
            Course course = optionalCourse.get();
            course.setStatus(status);
            course.setNote(note);
            course.setUpdatedAt(new Date());
            // Lấy user thực hiện thao tác
            Optional<User> userOpt = userRepository.findByEmail(email);
            userOpt.ifPresent(user -> course.setApprovedBy(user));
            course.setApprovedAt(new Date());
            courseRepository.save(course);
        }
    }

}
