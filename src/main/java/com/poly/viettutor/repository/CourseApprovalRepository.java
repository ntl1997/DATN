package com.poly.viettutor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poly.viettutor.model.CourseApproval;

@Repository
public interface CourseApprovalRepository extends JpaRepository<CourseApproval, Integer> {

}
