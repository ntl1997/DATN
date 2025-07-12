package com.poly.viettutor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poly.viettutor.model.CourseModule;

@Repository
public interface CourseModuleRepository extends JpaRepository<CourseModule, Integer> {

}
