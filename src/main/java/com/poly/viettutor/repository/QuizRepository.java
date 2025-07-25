package com.poly.viettutor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poly.viettutor.model.CourseModule;
import com.poly.viettutor.model.Quiz;
import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    void deleteByModule(CourseModule module);

    List<Quiz> findByModule(CourseModule module);

}
