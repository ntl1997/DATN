package com.poly.viettutor.repository;

import com.poly.viettutor.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CertificateRepository extends JpaRepository<Certificate, Integer> {

    @Query("SELECT c FROM Certificate c JOIN FETCH c.course WHERE c.user.id = :userId")
    List<Certificate> getCertificatesWithCourseByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM Certificate c JOIN FETCH c.course WHERE c.user.id = :userId AND LOWER(c.course.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Certificate> searchCertificatesWithCourseByUserAndTitle(@Param("userId") Long userId, @Param("keyword") String keyword);

    List<Certificate> findByUser_Id(Long userId);
    List<Certificate> findByUser_IdAndCourse_TitleContainingIgnoreCase(Long userId, String keyword);
}
