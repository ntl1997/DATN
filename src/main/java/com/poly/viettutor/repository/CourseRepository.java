package com.poly.viettutor.repository;

import com.poly.viettutor.model.Course;
import com.poly.viettutor.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    // Lấy 6 khóa học phổ biến nhất (ví dụ: theo số lượng đăng ký hoặc tiêu chí nào
    // đó)
    @Query("SELECT c FROM Course c ORDER BY c.price DESC") // Thay c.price bằng trường phổ biến nếu có
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

    List<Course> findByCreatedByIdAndStatus(Long instructorId, String status);
}
