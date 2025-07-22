package com.poly.viettutor.repository;

import com.poly.viettutor.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    // Add custom query methods if needed
}
