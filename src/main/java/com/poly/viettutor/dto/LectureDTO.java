package com.poly.viettutor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LectureDTO {

    private String lectureTitle;
    private String content;
    private String videoUrl;
    private Integer duration;

}
