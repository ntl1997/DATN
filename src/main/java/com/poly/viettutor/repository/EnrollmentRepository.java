package com.poly.viettutor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.poly.viettutor.model.Enrollment;
import com.poly.viettutor.model.User;
import com.poly.viettutor.model.Course;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {

    @Query("SELECT COUNT(DISTINCT e.user) FROM Enrollment e WHERE e.course.createdBy = :instructor")
    long countStudentsByInstructor(@Param("instructor") User instructor);

    boolean existsByUserAndCourse(User user, Course course);

    Optional<Enrollment> findByUserAndCourse(User user, Course course);

}
