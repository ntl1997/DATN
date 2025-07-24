package com.poly.viettutor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poly.viettutor.model.CourseModule;
import com.poly.viettutor.model.Lecture;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Integer> {

    void deleteByModule(CourseModule module);

}
