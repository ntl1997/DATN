package com.poly.viettutor.repository;

import com.poly.viettutor.model.QuizAnswer;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {
    List<QuizAnswer> findBySubmissionSubmissionId(Long submissionId);

    @Query("SELECT m.course.title, q.title, COUNT(qa), " +
            "SUM(CASE WHEN qa.isCorrect = true THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN qa.isCorrect = false THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN qa.isCorrect = true THEN 1.0 ELSE 0 END) * 100.0 / COUNT(qa) " +
            "FROM QuizAnswer qa " +
            "JOIN qa.submission s " +
            "JOIN s.quiz q " +
            "JOIN q.module m " +
            "WHERE (:courseId IS NULL OR m.course.courseId = :courseId) " +
            "GROUP BY m.course.title, q.title")
    List<Object[]> getQuizStatsByCourseId(@Param("courseId") Long courseId);
}
