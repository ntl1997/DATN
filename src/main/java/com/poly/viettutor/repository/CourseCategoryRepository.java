package com.poly.viettutor.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poly.viettutor.model.CourseCategory;

public interface CourseCategoryRepository extends JpaRepository<CourseCategory, CourseCategory.PK> {

}
