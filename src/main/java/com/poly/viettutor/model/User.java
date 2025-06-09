package com.poly.viettutor.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "users")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Column(name = "UserId", nullable = false)
    private long id;

    @Column(name = "FullName", nullable = false)
    @NotEmpty(message = "Họ tên không được để trống")
    private String fullname;

    @Column(name = "Email", nullable = false, unique = true)
    @NotEmpty(message = "Email không được để trống")
    private String email;

    @Column(name = "Image")
    @NotEmpty(message = "Hình ảnh không được để trống")
    private String image;

    @Column(name = "PasswordHash", nullable = false)
    @NotEmpty(message = "Mật khẩu không được để trống")
    private String password;

    @Column(name = "Role", nullable = false)
    @NotEmpty(message = "Vai trò không được để trống")
    private String role;

    @Column(name = "CreatedAt", nullable = false)
    private Date createdAt = new Date();

}
