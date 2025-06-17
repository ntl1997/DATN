package com.poly.viettutor.repository;

import com.poly.viettutor.model.CourseCategory;
import com.poly.viettutor.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseCategoryRepository extends JpaRepository<CourseCategory, CourseCategory.PK> {
    long countByCategory(Category category);
}
