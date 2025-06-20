package com.poly.viettutor.repository;

import com.poly.viettutor.model.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Integer> {
    // Thêm các phương thức truy vấn tùy chỉnh nếu cần
}
