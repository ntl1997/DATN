package com.poly.viettutor.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.poly.viettutor.model.CourseOffering;
import com.poly.viettutor.repository.CourseOfferingRepository;

@Service
public class CourseOfferingService {

    private final CourseOfferingRepository courseOfferingRepository;

    public CourseOfferingService(CourseOfferingRepository courseOfferingRepository) {
        this.courseOfferingRepository = courseOfferingRepository;
    }

    public List<CourseOffering> findAll() {
        return courseOfferingRepository.findAll();
    }

    public Optional<CourseOffering> findById(long id) {
        return courseOfferingRepository.findById(id);
    }

    public CourseOffering save(CourseOffering courseOffering) {
        return courseOfferingRepository.save(courseOffering);
    }

    public void deleteById(long id) {
        courseOfferingRepository.deleteById(id);
    }

}
