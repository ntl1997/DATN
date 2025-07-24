package com.poly.viettutor.repository;

import com.poly.viettutor.model.Category;
import com.poly.viettutor.model.Course;
import com.poly.viettutor.model.CourseCategory;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.util.List;

public class CourseSpecification {

    public static Specification<Course> filterCourses(
            String keyword,
            List<String> categories,
            List<Integer> ratings,
            String instructor,
            String priceType,
            boolean showAllStatuses) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            // ✅ Chỉ lọc status = "Publish" nếu không phải admin
            if (!showAllStatuses) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), "Publish"));
            }

            if (keyword != null && !keyword.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("title"), "%" + keyword + "%"));
            }
            if (categories != null && !categories.isEmpty()) {
                Join<Course, CourseCategory> ccJoin = root.join("courseCategories");
                Join<CourseCategory, Category> categoryJoin = ccJoin.join("category");
                predicate = cb.and(predicate, categoryJoin.get("name").in(categories));
            }

            if (ratings != null && !ratings.isEmpty()) {
                Join<Object, Object> reviewJoin = root.join("reviews");
                predicate = cb.and(predicate, reviewJoin.get("rating").in(ratings));
            }
            if (instructor != null && !instructor.isEmpty()) {
                Join<Object, Object> instructorJoin = root.join("createdBy");
                predicate = cb.and(predicate, cb.equal(instructorJoin.get("fullname"), instructor));
            }
            if (priceType != null) {
                if (priceType.equals("free")) {
                    predicate = cb.and(predicate, cb.equal(root.get("price"), 0));
                } else if (priceType.equals("paid")) {
                    predicate = cb.and(predicate, cb.greaterThan(root.get("price"), 0));
                }
            }
            return predicate;
        };
    }

    public static Specification<Course> filterCourses(
            String keyword,
            List<String> categories,
            List<Integer> ratings,
            String instructor,
            String priceType) {
        return filterCourses(keyword, categories, ratings, instructor, priceType, false);
    }

}
