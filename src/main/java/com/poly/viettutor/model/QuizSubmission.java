package com.poly.viettutor.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

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

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizAnswer> answers;

}
