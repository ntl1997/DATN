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
                    u.fullName,
                    u.email,
                    qz.title,
                    qs.submittedAt,
                    qs.score,
                    qz.totalScore,
                    cm.moduleTitle
                FROM QuizSubmissions qs
                JOIN Quizzes qz ON qs.quizId = qz.quizId
                JOIN CourseModules cm ON qz.moduleId = cm.moduleId
                JOIN Courses c ON cm.courseId = c.courseId
                JOIN Users u ON qs.userId = u.userId
                WHERE c.title = :courseTitle
                ORDER BY qs.submittedAt DESC
            """, nativeQuery = true)
    List<Object[]> getQuizSubmissionsByCourseTitle(@Param("courseTitle") String courseTitle);

}
