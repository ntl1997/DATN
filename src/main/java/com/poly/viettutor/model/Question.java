package com.poly.viettutor.model;

import lombok.*;

import java.util.List;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "Questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @ManyToOne
    @JoinColumn(name = "quizId")
    private Quiz quiz;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String questionText;

    private Integer score;
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Option> options;

    @Transient
    private Long selectedOptionId;
}
