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
public class QuestionDTO {

    @NotBlank(message = "Câu hỏi không được để trống!")
    private String questionText;

    @NotNull(message = "Điểm không được để trống!")
    private Double score;

    @NotNull(message = "Chọn đáp án đúng!")
    private Integer correctOption;

    private List<OptionDTO> options;

}
