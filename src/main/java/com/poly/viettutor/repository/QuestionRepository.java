package com.poly.viettutor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poly.viettutor.model.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

}
