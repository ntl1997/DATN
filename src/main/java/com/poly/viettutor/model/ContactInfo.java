package com.poly.viettutor.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.Date;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "ContactInfo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer contactId;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 50, message = "Họ tên tối đa 50 ký tự")
    private String name;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 100, message = "Email tối đa 100 ký tự")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Size(max = 15, message = "Số điện thoại tối đa 15 ký tự")
    private String phoneNumber;

    @NotBlank(message = "Lời nhắn không được để trống")
    private String message;

    private boolean isRead;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
}