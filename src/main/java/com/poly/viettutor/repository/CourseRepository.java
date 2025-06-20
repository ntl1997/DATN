package com.poly.viettutor.repository;

import com.poly.viettutor.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    // Lấy 6 khóa học phổ biến nhất (ví dụ: theo số lượng đăng ký hoặc tiêu chí nào
    // đó)
    @Query("SELECT c FROM Course c ORDER BY c.price DESC") // Thay c.price bằng trường phổ biến nếu có
    List<Course> findTop6PopularCourses(Pageable pageable);
}
