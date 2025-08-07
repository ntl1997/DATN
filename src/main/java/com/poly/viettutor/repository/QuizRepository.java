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

    // ✅ THÊM: Tìm tất cả quiz trong 1 khóa học
    @Query("""
                SELECT q FROM Quiz q
                WHERE q.module.course.courseId = :courseId
            """)
    List<Quiz> findByCourseId(@Param("courseId") Long courseId);

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

    @Query(value = """
            WITH TotalQuizzesCTE AS (
                SELECT c.CourseId, COUNT(q.QuizId) AS TotalQuizzes
                FROM Courses c
                JOIN CourseModules cm ON cm.CourseId = c.CourseId
                JOIN Quizzes q ON q.ModuleId = cm.ModuleId
                WHERE c.Title = :courseTitle
                GROUP BY c.CourseId
            ),
            PassedQuizzesCTE AS (
                SELECT DISTINCT qs.UserId, qs.QuizId
                FROM QuizSubmissions qs
                JOIN Quizzes q ON q.QuizId = qs.QuizId
                JOIN CourseModules cm ON cm.ModuleId = q.ModuleId
                JOIN Courses c ON c.CourseId = cm.CourseId
                WHERE c.Title = :courseTitle
                  AND qs.Score >= 0.5 * q.TotalScore
            )
            SELECT
                u.FullName AS fullName,
                u.Email AS email,
                CONCAT(COUNT(DISTINCT pq.QuizId), '/', tq.TotalQuizzes) AS quizzesProgress,
                CONCAT(CAST(COUNT(DISTINCT pq.QuizId) * 100.0 / tq.TotalQuizzes AS INT), '%') AS completionPercentage,
                MAX(qs.SubmittedAt) AS lastSubmittedAt
            FROM Courses c
            JOIN TotalQuizzesCTE tq ON tq.CourseId = c.CourseId
            JOIN Enrollments e ON e.CourseId = c.CourseId
            JOIN Users u ON u.UserId = e.UserId
            LEFT JOIN PassedQuizzesCTE pq ON pq.UserId = u.UserId
            LEFT JOIN QuizSubmissions qs ON qs.UserId = u.UserId
            WHERE c.Title = :courseTitle
            GROUP BY u.FullName, u.Email, tq.TotalQuizzes
            ORDER BY u.FullName;
            """, nativeQuery = true)
    List<Object[]> findQuizProgressByCourseTitle(@Param("courseTitle") String courseTitle);

    @Query(value = """
            WITH TotalQuizzesCTE AS (
                SELECT e.UserId, c.CourseId, c.Title, COUNT(q.QuizId) AS TotalQuizzes
                FROM Enrollments e
                JOIN Courses c ON c.CourseId = e.CourseId
                JOIN CourseModules cm ON cm.CourseId = c.CourseId
                JOIN Quizzes q ON q.ModuleId = cm.ModuleId
                WHERE e.UserId = :userId
                  AND c.Title LIKE CONCAT('%', :courseTitle, '%')
                GROUP BY e.UserId, c.CourseId, c.Title
            ),
            PassedQuizzesCTE AS (
                SELECT DISTINCT qs.UserId, qs.QuizId
                FROM QuizSubmissions qs
                JOIN Quizzes q ON q.QuizId = qs.QuizId
                JOIN CourseModules cm ON cm.ModuleId = q.ModuleId
                JOIN Courses c ON c.CourseId = cm.CourseId
                WHERE qs.UserId = :userId
                  AND qs.Score >= 0.5 * q.TotalScore
            )
            SELECT
                tq.Title AS title,
                CONCAT(COUNT(DISTINCT pq.QuizId), '/', tq.TotalQuizzes) AS quizzesProgress,
                CONCAT(CAST(COUNT(DISTINCT pq.QuizId) * 100.0 / tq.TotalQuizzes AS INT), '%') AS completionPercentage,
                MAX(qs.SubmittedAt) AS lastSubmittedAt
            FROM TotalQuizzesCTE tq
            JOIN CourseModules cm ON cm.CourseId = tq.CourseId
            JOIN Quizzes q ON q.ModuleId = cm.ModuleId
            LEFT JOIN PassedQuizzesCTE pq ON pq.QuizId = q.QuizId AND pq.UserId = :userId
            LEFT JOIN QuizSubmissions qs ON qs.QuizId = q.QuizId AND qs.UserId = :userId
            GROUP BY tq.Title, tq.TotalQuizzes
            ORDER BY tq.Title
            """, nativeQuery = true)
    List<Object[]> findQuizProgressByCourseTitleAndUserId(@Param("courseTitle") String courseTitle,
            @Param("userId") long userId);

}
