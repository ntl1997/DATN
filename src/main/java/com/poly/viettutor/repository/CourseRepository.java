package com.poly.viettutor.repository;

import com.poly.viettutor.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    // Thêm các phương thức truy vấn tùy chỉnh nếu cần
}
