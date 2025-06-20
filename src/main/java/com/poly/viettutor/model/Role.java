package com.poly.viettutor.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "Roles")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RoleId", nullable = false)
    private long id;

    @Column(name = "Role", nullable = false)
    @NotEmpty(message = "Tên vài trò không được để trống")
    private String roleName;

}
