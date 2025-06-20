package com.poly.viettutor.model;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "CourseModules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseModule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer moduleId;

    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course course;

    private String moduleTitle;

    private Integer sortOrder;
}
