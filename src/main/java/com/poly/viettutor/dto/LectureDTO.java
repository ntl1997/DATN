package com.poly.viettutor.dto;

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
public class LectureDTO {

    @NotBlank(message = "Tiêu đề bài học không được để trống!")
    private String lectureTitle;

    @NotBlank(message = "Nội dung bài học không được để trống!")
    private String content;

    @NotBlank(message = "Đường link video không được để trống!")
    private String videoUrl;

    @NotNull(message = "Thời lượng video không được để trống!")
    private Integer duration;

}
