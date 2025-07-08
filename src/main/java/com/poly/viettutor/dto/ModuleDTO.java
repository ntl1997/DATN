package com.poly.viettutor.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleDTO {

    private String moduleTitle;
    private List<LectureDTO> lectures;

}
