package com.poly.viettutor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterialDTO {

    private String fileName;
    private String fileUrl;
    private String fileType;

}
