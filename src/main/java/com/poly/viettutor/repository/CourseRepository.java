package com.poly.viettutor.repository;

import com.poly.viettutor.model.Course;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer>, JpaSpecificationExecutor<Course> {

        // Lấy 6 khóa học phổ biến nhất (ví dụ: theo số lượng đăng ký hoặc tiêu chí nào
        // đó)
        @Query("SELECT c FROM Course c WHERE c.status = 'Publish' ORDER BY c.price DESC")
        List<Course> findTop6PopularCourses(Pageable pageable);

        @Query("SELECT COUNT(c) FROM Course c WHERE c.createdBy.id = :userId")
        long countCoursesByUserId(@Param("userId") Long userId);

        @Query(value = "SELECT c.Title, COUNT(DISTINCT e.UserId), AVG(r.Rating) " +
                        "FROM Courses c " +
                        "LEFT JOIN Enrollments e ON e.CourseId = c.CourseId " +
                        "LEFT JOIN Reviews r ON r.CourseId = c.CourseId " +
                        "WHERE c.CreatedBy = :instructorId " +
                        "GROUP BY c.CourseId, c.Title", nativeQuery = true)
        List<Object[]> findCourseSummaryByInstructorNative(@Param("instructorId") Long instructorId);

        List<Course> findByStatus(String status);

        List<Course> findByCreatedByIdAndStatus(Long instructorId, String status);

        @Query(value = """
                        SELECT TOP 5
                            c.Title AS courseTitle,
                            COUNT(e.UserId) AS studentCount,
                            CAST(COUNT(cert.CertificateId) * 1.0 / NULLIF(COUNT(e.UserId), 0) AS FLOAT) AS completionRate,
                            u.FullName AS instructorName
                        FROM Courses c
                        LEFT JOIN Enrollments e ON c.CourseId = e.CourseId
                        LEFT JOIN Certificates cert ON e.CourseId = cert.CourseId AND e.UserId = cert.UserId
                        LEFT JOIN Users u ON c.CreatedBy = u.UserId
                        GROUP BY c.CourseId, c.Title, u.FullName
                        ORDER BY COUNT(e.UserId) DESC
                        """, nativeQuery = true)
        List<Object[]> findTop5CourseStatistics();

        @Query(value = "SELECT TOP 5 " +
                        "c.Title AS courseTitle, " +
                        "COUNT(e.UserId) AS studentCount " +
                        "FROM Courses c " +
                        "LEFT JOIN Enrollments e ON c.CourseId = e.CourseId " +
                        "GROUP BY c.CourseId, c.Title " +
                        "ORDER BY COUNT(e.UserId) DESC", nativeQuery = true)
        List<Object[]> findTop5CoursesByStudentCount();

        @Query("SELECT c FROM Course c WHERE c.status = 'publish'")
        List<Course> findAllPublished();

}
