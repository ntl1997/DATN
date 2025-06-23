package com.poly.viettutor.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer courseId;

    private String title;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String curriculum;

    private String authorName;

    private Double price;

    private Double discount;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String courseImage;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String demoVideoUrl; // Đường dẫn hoặc URL video demo

    private String status;

    @ManyToOne
    @JoinColumn(name = "createdBy")
    private User createdBy; // Quan hệ @ManyToOne tới User đã đúng, không cần chỉnh sửa

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseModule> modules;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CourseObjective> objectives;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Column(name = "TargetAudience", length = 100)
    private String targetAudience;

    @Column(name = "HasCertificate")
    private Boolean hasCertificate;

    @Column(name = "PassPercentage")
    private int passPercentage;

    @Column(name = "Language", length = 50)
    private String language;
}
