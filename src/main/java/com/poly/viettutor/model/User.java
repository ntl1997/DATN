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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

@Table(name = "Users")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserId", nullable = false)
    private Long id;

    @Column(name = "FullName", nullable = false)
    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
    private String fullname;

    @Column(name = "Email", nullable = false, unique = true)
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @Column(name = "Image")
    private String image;

    @Column(name = "PasswordHash", nullable = false)
    private String password;

    @Builder.Default
    @Column(name = "CreatedAt", nullable = false)
    private Date createdAt = new Date();

    @Column(name = "PhoneNumber")
    @Size(max = 20, message = "Số điện thoại tối đa 20 ký tự")
    private String phoneNumber;

    @Column(name = "Occupation")
    @Size(max = 100, message = "Nghề nghiệp tối đa 100 ký tự")
    private String occupation;

    @Column(name = "Biography")
    @Size(max = 1000, message = "Tiểu sử tối đa 1000 ký tự")
    private String biography;

    @Column(name = "Status")
    private Boolean status;

    @ManyToMany(fetch = FetchType.EAGER) // EAGER để nạp dữ liệu role ngay khi nạp user
    @JoinTable(name = "UserRoles", // Tên bảng trung gian
            joinColumns = @JoinColumn(name = "UserId"), // FK đến bảng User
            inverseJoinColumns = @JoinColumn(name = "RoleId") // FK đến bảng Role
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "createdBy")
    @JsonIgnore
    private List<BlogPost> blogPosts;

    @OneToMany(mappedBy = "createdBy")
    private List<Course> courses;

    @OneToMany(mappedBy = "user")
    private List<Enrollment> enrollments;

    @OneToMany(mappedBy = "user")
    private List<QuizSubmission> quizSubmissions;

}
