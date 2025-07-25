package com.poly.viettutor.repository;

import com.poly.viettutor.model.QuizSubmission;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {
    List<QuizSubmission> findByQuizQuizId(Long quizId);
}
