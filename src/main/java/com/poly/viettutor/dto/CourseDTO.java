package com.poly.viettutor.dto;

import java.util.List;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDTO {

    @NotBlank(message = "Tiêu đề không được để trống!")
    private String title;

    @NotBlank(message = "Mô tả không được để trống!")
    private String description;

    @NotBlank(message = "Nội dung chi tiết không được để trống!")
    private String overview;

    @NotNull(message = "Giá tiền không được để trống!")
    @PositiveOrZero(message = "Giá tiền phải lớn hơn hoặc btrống!")
    private Double price;

    @Builder.Default
    @DecimalMin(value = "0.0", inclusive = true, message = "Giảm giá từ 0 - 100!")
    @DecimalMax(value = "100.0", inclusive = true, message = "Giảm giá từ 0 - 100!")
    private Double discount = 0.0;

    private String courseImage;

    @NotBlank(message = "Video giới thiệu không được để trống!")
    private String demoVideoUrl;

    private String status;

    @NotBlank(message = "Cấp độ không được để trống!")
    private String skillLevel;

    @Builder.Default
    private Boolean hasCertificate = false;

    @NotBlank(message = "Ngôn ngữ không được để trống!")
    private String language;

    @NotEmpty(message = "Chọn ít nhất một danh mục!")
    private List<Integer> categoryIds;

    @NotEmpty(message = "Khóa học phải có ít nhất 1 chương!")
    private List<ModuleDTO> modules;

}
