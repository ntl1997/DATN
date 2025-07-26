package com.poly.viettutor.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleDTO {

    private Long moduleId;

    @NotBlank(message = "Tiêu đề chương không được để trống!")
    private String moduleTitle;

    @NotEmpty(message = "Mỗi chương phải có ít nhất 1 bài học!")
    private List<LectureDTO> lectures;

    private List<QuizDTO> quizzes;

}
