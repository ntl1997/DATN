package com.poly.viettutor.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleDTO {

    @NotBlank(message = "Tiêu đề chương không được để trống!")
    private String moduleTitle;

    private List<LectureDTO> lectures;

}
