package com.poly.viettutor.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizDTO {

    @NotBlank(message = "Tiêu đề quiz không được để trống!")
    private String title;

    @NotNull(message = "Thời gian làm bài không được để trống!")
    private Integer timeLimit;

    @NotNull(message = "Tổng điểm không được để trống!")
    private Double totalScore;

    private String quizType;

    private List<QuestionDTO> questions;

}
