package com.poly.viettutor.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

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

    private String moduleTitle;

    private Integer sortOrder;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Lecture> lectures;

    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course course;
}
