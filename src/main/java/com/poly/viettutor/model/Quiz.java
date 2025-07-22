package com.poly.viettutor.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Quizzes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quizId;

    @ManyToOne
    @JoinColumn(name = "moduleId")
    private CourseModule module;

    private String title;

    private Integer totalScore;

    private Integer timeLimit;

    private String quizType;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @OneToMany
    @JoinColumn(name = "quizId")
    private List<Question> questions;
}
