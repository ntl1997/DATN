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

    private String status;

    @ManyToOne
    @JoinColumn(name = "createdBy")
    private User createdBy;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseModule> modules;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
}
