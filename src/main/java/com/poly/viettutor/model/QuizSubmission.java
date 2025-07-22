package com.poly.viettutor.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "QuizSubmissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long submissionId;

    @ManyToOne
    @JoinColumn(name = "quizId")
    private Quiz quiz;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @Temporal(TemporalType.TIMESTAMP)
    private Date submittedAt;

    private Integer score;
}
