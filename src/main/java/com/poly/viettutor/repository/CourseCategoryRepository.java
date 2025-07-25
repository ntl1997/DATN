package com.poly.viettutor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poly.viettutor.model.Course;
import com.poly.viettutor.model.CourseCategory;

@Repository
public interface CourseCategoryRepository extends JpaRepository<CourseCategory, CourseCategory.PK> {

    void deleteByCourse(Course course);

}
