package com.poly.viettutor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.poly.viettutor.model.CourseModule;
import com.poly.viettutor.model.Quiz;
import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    void deleteByModule(CourseModule module);

    List<Quiz> findByModule(CourseModule module);

    @Query(value = """
                SELECT
                    u.full_name,
                    u.email,
                    qz.title,
                    qs.submitted_at,
                    qs.score,
                    qz.total_score,
                    cm.module_title
                FROM quiz_submissions qs
                JOIN quizzes qz ON qs.quiz_id = qz.quiz_id
                JOIN course_modules cm ON qz.module_id = cm.module_id
                JOIN courses c ON cm.course_id = c.course_id
                JOIN users u ON qs.user_id = u.user_id
                WHERE c.title = :courseTitle
                ORDER BY qs.submitted_at DESC
            """, nativeQuery = true)
    List<Object[]> getQuizSubmissionsByCourseTitle(@Param("courseTitle") String courseTitle);

}
