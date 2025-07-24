package com.poly.viettutor.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.poly.viettutor.model.Course;
import com.poly.viettutor.model.CourseModule;
import com.poly.viettutor.model.Lecture;
import com.poly.viettutor.model.Option;
import com.poly.viettutor.model.Question;
import com.poly.viettutor.model.Quiz;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDTO {

    private Integer courseId;

    @NotBlank(message = "Tiêu đề không được để trống!")
    private String title;

    @NotBlank(message = "Mô tả không được để trống!")
    private String description;

    @NotBlank(message = "Nội dung chi tiết không được để trống!")
    private String overview;

    // @NotNull(message = "Giá tiền không được để trống!")
    @PositiveOrZero(message = "Giá tiền phải lớn hơn hoặc trống!")
    private Double price;

    @Builder.Default
    @DecimalMin(value = "0.0", inclusive = true, message = "Giảm giá từ 0 - 100!")
    @DecimalMax(value = "100.0", inclusive = true, message = "Giảm giá từ 0 - 100!")
    private Double discount = 0.0;

    private String courseImage;

    @NotBlank(message = "Video giới thiệu không được để trống!")
    private String demoVideoUrl;

    private String status;

    @NotBlank(message = "Cấp độ không được để trống!")
    private String skillLevel;

    @Builder.Default
    private Boolean hasCertificate = false;

    @NotBlank(message = "Ngôn ngữ không được để trống!")
    private String language;

    @NotEmpty(message = "Chọn ít nhất một danh mục!")
    private List<Integer> categoryIds;

    private List<ModuleDTO> modules;

    public CourseDTO toDTO(Course course) {
        return CourseDTO.builder()
                .courseId(course.getCourseId())
                .title(course.getTitle())
                .description(course.getDescription())
                .overview(course.getOverview())
                .price(course.getPrice())
                .discount(course.getDiscount())
                .courseImage(course.getCourseImage())
                .demoVideoUrl(course.getDemoVideoUrl())
                .status(course.getStatus())
                .skillLevel(course.getSkillLevel())
                .hasCertificate(course.getHasCertificate())
                .language(course.getLanguage())
                .categoryIds(course.getCourseCategories()
                        .stream().map(cc -> cc.getCategory().getCategoryId()).collect(Collectors.toList()))
                .modules(course.getModules().stream().map(this::mapModule).toList())
                .build();
    }

    private ModuleDTO mapModule(CourseModule module) {
        return ModuleDTO.builder()
                .moduleId(module.getModuleId())
                .moduleTitle(module.getModuleTitle())
                .lectures(module.getLectures().stream().map(this::mapLecture).toList())
                .quizzes(module.getQuizzes().stream().map(this::mapQuiz).toList())
                .build();
    }

    private LectureDTO mapLecture(Lecture lecture) {
        return LectureDTO.builder()
                .lectureTitle(lecture.getLectureTitle())
                .content(lecture.getContent())
                .videoUrl(lecture.getVideoUrl())
                .duration(lecture.getDuration())
                .build();
    }

    private QuizDTO mapQuiz(Quiz quiz) {
        return QuizDTO.builder()
                .title(quiz.getTitle())
                .timeLimit(quiz.getTimeLimit())
                .totalScore(quiz.getTotalScore().doubleValue())
                .quizType(quiz.getQuizType())
                .questions(quiz.getQuestions().stream().map(this::mapQuestion).toList())
                .build();
    }

    private QuestionDTO mapQuestion(Question question) {
        return QuestionDTO.builder()
                .questionText(question.getQuestionText())
                .score(question.getScore().doubleValue())
                .options(question.getOptions().stream().map(this::mapOption).toList())
                .build();
    }

    private OptionDTO mapOption(Option option) {
        return OptionDTO.builder()
                .optionText(option.getOptionText())
                .isCorrect(option.getIsCorrect())
                .build();
    }

}
