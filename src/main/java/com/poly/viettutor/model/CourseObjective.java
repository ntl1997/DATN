package com.poly.viettutor.model;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "CourseObjectives")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseObjective {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer objectiveId;

    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course course;

    @Column(length = 500)
    private String objectiveText;
}
