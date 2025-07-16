package com.poly.viettutor.repository;

import com.poly.viettutor.model.Review;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT r FROM Review r WHERE r.course.createdBy.id = :instructorId")
    List<Review> findAllByInstructorId(@Param("instructorId") Long instructorId);

    @Query("SELECT r FROM Review r WHERE r.user.id = :userId")
    List<Review> findAllByUserId(@Param("userId") Long userId);

    Optional<Review> findById(Long id);

}
