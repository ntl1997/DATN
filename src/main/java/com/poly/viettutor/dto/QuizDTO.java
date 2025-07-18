package com.poly.viettutor.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizDTO {

    @NotBlank(message = "Tiêu đề quiz không được để trống!")
    private String title;

    @NotNull(message = "Thời gian làm bài không được để trống!")
    private Integer timeLimit;

    private List<@Valid QuestionDTO> questions;

}
