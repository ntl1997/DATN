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

    private String description;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String overview;

    private Double price;

    private Double discount;

    private String courseImage;

    @Column(length = 1000)
    private String demoVideoUrl; // Đường dẫn hoặc URL video demo

    private String status;

    private String skillLevel;

    private Boolean hasCertificate;

    private String language;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @ManyToOne
    @JoinColumn(name = "createdBy")
    private User createdBy;

    private String note; // Lý do (nếu có)

    @ManyToOne
    @JoinColumn(name = "approvedBy")
    private User approvedBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date approvedAt;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseModule> modules;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseMaterial> materials;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseCategory> courseCategories;

    @Transient
    public int getReviewCount() {
        return reviews != null ? reviews.size() : 0;
    }

    @Transient
    private int rating;
}
