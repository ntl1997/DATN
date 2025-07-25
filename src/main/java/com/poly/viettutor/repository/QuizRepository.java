package com.poly.viettutor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.poly.viettutor.model.CourseModule;
import com.poly.viettutor.model.Quiz;
import java.util.List;
import java.util.Map;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    void deleteByModule(CourseModule module);

    List<Quiz> findByModule(CourseModule module);

    @Query(value = """
            SELECT
                u.FullName,
                u.Email,
                qz.Title AS QuizTitle,
                qs.SubmittedAt,
                qs.Score,
                qz.TotalScore,
                cm.ModuleTitle
            FROM QuizSubmissions qs
            JOIN Quizzes qz ON qs.QuizId = qz.QuizId
            JOIN CourseModules cm ON qz.ModuleId = cm.ModuleId
            JOIN Courses c ON cm.CourseId = c.CourseId
            JOIN Users u ON qs.UserId = u.UserId
            WHERE c.Title = :courseTitle
            ORDER BY qs.SubmittedAt DESC
            """, nativeQuery = true)
    List<Object[]> getQuizSubmissionsByCourseTitles(@Param("courseTitle") String courseTitle);

    @Query(value = """
            SELECT
                u.FullName,
                u.Email,
                qz.Title AS QuizTitle,
                qs.SubmittedAt,
                qs.Score,
                qz.TotalScore,
                cm.ModuleTitle
            FROM QuizSubmissions qs
            JOIN Quizzes qz ON qs.QuizId = qz.QuizId
            JOIN CourseModules cm ON qz.ModuleId = cm.ModuleId
            JOIN Courses c ON cm.CourseId = c.CourseId
            JOIN Users u ON qs.UserId = u.UserId
            JOIN UserRoles ur ON ur.UserId = c.CreatedBy
            JOIN Roles r ON r.RoleId = ur.RoleId
            WHERE c.CreatedBy = :instructorId AND r.Role = 'Instructor'
            ORDER BY qs.SubmittedAt DESC
            """, nativeQuery = true)
    List<Object[]> getQuizSubmissionsByInstructorId(@Param("instructorId") Long instructorId);

}
