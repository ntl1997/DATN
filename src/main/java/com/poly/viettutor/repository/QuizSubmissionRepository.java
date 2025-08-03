package com.poly.viettutor.repository;

import com.poly.viettutor.model.QuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {

    List<QuizSubmission> findByQuizQuizId(Long quizId);

    // ✅ THÊM: Lấy tất cả submissions của 1 user
    List<QuizSubmission> findByUserId(Long userId);

    // ✅ THÊM: Lấy tất cả submissions của 1 user cho 1 quiz cụ thể
    List<QuizSubmission> findByUserIdAndQuizQuizId(Long userId, Long quizId);
}
