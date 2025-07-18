package com.poly.viettutor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionDTO {

    @NotBlank(message = "Câu trả lời không được để trống!")
    private String optionText;

    @Builder.Default
    @NotBlank(message = "Trạng thái không được để trống!")
    private Boolean isCorrect = false;

}
