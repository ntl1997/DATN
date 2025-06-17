package com.poly.viettutor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.poly.viettutor.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Thêm các phương thức truy vấn tùy chỉnh nếu cần
}
