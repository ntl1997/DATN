package com.poly.viettutor.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotEmpty(message = "Họ tên không được để trống")
    private String fullname;

    @NotEmpty(message = "Email không được để trống")
    private String email;

    @NotEmpty(message = "Số điện thoại không được để trống")
    private String phoneNumber;

    @NotEmpty(message = "Chức vụ không được để trống")
    private String occupation;

    @NotEmpty(message = "Mật khẩu không được để trống")
    private String password;

    @NotEmpty(message = "Xác nhận mật khẩu không được để trống")
    private String confirmPassword;

    Boolean isInstructor = false; // Mặc định là false, có thể được cập nhật khi tạo tài khoản

}
