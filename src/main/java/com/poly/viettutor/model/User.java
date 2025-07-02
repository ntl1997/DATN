package com.poly.viettutor.model;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "Users")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserId", nullable = false)
    private long id;

    @Column(name = "FullName", nullable = false)
    @NotEmpty(message = "Họ tên không được để trống")
    private String fullname;

    @Column(name = "Email", nullable = false, unique = true)
    @NotEmpty(message = "Email không được để trống")
    private String email;

    @Column(name = "Image")
    private String image;

    @Column(name = "PasswordHash", nullable = false)
    @NotEmpty(message = "Mật khẩu không được để trống")
    private String password;

    @Column(name = "CreatedAt", nullable = false)
    private Date createdAt = new Date();

    @Column(name = "PhoneNumber")
    private String phoneNumber;

    @Column(name = "Occupation")
    private String occupation;

    @Column(name = "Biography")
    private String biography;

    @ManyToMany(fetch = FetchType.EAGER) // EAGER để nạp dữ liệu role ngay khi nạp user
    @JoinTable(name = "UserRoles", // Tên bảng trung gian
            joinColumns = @JoinColumn(name = "UserId"), // FK đến bảng User
            inverseJoinColumns = @JoinColumn(name = "RoleId") // FK đến bảng Role
    )
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "createdBy")
    @JsonIgnore
    private List<BlogPost> blogPosts;

    @OneToMany(mappedBy = "createdBy")
    private List<Course> courses;

    @OneToMany(mappedBy = "user")
    private List<Enrollment> enrollments;
}
