package com.poly.viettutor.model;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "QuizAnswers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answerId;

    @ManyToOne
    @JoinColumn(name = "submissionId")
    private QuizSubmission submission;

    private Long questionId;

    private Long selectedOptionId;

    private Boolean isCorrect;
}
