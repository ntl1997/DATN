package com.poly.viettutor.repository;

import com.poly.viettutor.model.QuizAnswer;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {
    List<QuizAnswer> findBySubmissionSubmissionId(Long submissionId);
}
