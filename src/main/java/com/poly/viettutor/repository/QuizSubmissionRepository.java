package com.poly.viettutor.repository;

import com.poly.viettutor.model.QuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {
    List<QuizSubmission> findByQuizQuizId(Long quizId);

    List<QuizSubmission> findByUserId(Long userId); // Change 'userId' to 'user.id'
}
