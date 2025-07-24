package com.poly.viettutor.dto;

import com.poly.viettutor.model.User;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserInfoDTO {

    private long id;

    @NotEmpty(message = "Họ tên không được để trống")
    private String fullname;

    @NotEmpty(message = "Email không được để trống")
    private String email;

    @NotEmpty(message = "Số điện thoại không được để trống")
    private String phoneNumber;

    @NotEmpty(message = "Chức vụ không được để trống")
    private String occupation;

    Boolean isInstructor = false;

    public UpdateUserInfoDTO toDTO(User user) {
        UpdateUserInfoDTO dto = new UpdateUserInfoDTO();
        dto.setId(user.getId());
        dto.setFullname(user.getFullname());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setOccupation(user.getOccupation());
        dto.setIsInstructor(user.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equals("INSTRUCTOR")));
        return dto;
    }

}
