package com.poly.viettutor.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {

    @NotBlank(message = "Câu hỏi không được để trống!")
    private String questionText;

    @NotBlank(message = "Loại câu hỏi không được để trống!")
    private String questionType;

    private List<@Valid OptionDTO> options;

}
