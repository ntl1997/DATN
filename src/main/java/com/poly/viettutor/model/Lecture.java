package com.poly.viettutor.model;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "Lectures")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer lectureId;

    @ManyToOne
    @JoinColumn(name = "moduleId")
    private CourseModule module;

    private String lectureTitle;

    private String content;

    @Column(length = 500)
    private String videoUrl;

    private Integer sortOrder;

    @Column(nullable = true)
    private Integer duration; // thời lượng (phút)
}
