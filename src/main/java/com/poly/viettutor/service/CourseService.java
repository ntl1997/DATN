package com.poly.viettutor.service;

import com.poly.viettutor.model.Course;
import com.poly.viettutor.model.Lecture;
import com.poly.viettutor.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public Optional<Course> findById(Integer id) {
        return courseRepository.findById(id);
    }

    public Course save(Course course) {
        return courseRepository.save(course);
    }

    public void deleteById(Integer id) {
        courseRepository.deleteById(id);
    }

    public List<Course> getTop6PopularCourses() {
        return courseRepository.findTop6PopularCourses(PageRequest.of(0, 6));
    }

    public int totalDuration(Course course) {
        return course.getModules().stream()
                .flatMap(module -> module.getLectures().stream())
                .mapToInt(Lecture::getDuration)
                .sum();
    }
}
