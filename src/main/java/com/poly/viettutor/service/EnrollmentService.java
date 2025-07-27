package com.poly.viettutor.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.poly.viettutor.model.Course;
import com.poly.viettutor.model.Enrollment;
import com.poly.viettutor.model.User;
import com.poly.viettutor.repository.EnrollmentRepository;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    public List<Enrollment> findAll() {
        return enrollmentRepository.findAll();
    }

    public Optional<Enrollment> findById(int id) {
        return enrollmentRepository.findById(id);
    }

    public Enrollment save(Enrollment enrollment) {
        return enrollmentRepository.save(enrollment);
    }

    public void deleteById(int id) {
        enrollmentRepository.deleteById(id);
    }

    public long countStudentsByInstructor(User instructor) {
        return enrollmentRepository.countStudentsByInstructor(instructor);
    }

    public boolean isEnrolled(User user, Course course) {
        return enrollmentRepository.existsByUserAndCourse(user, course);
    }

    public Optional<Enrollment> findByUserAndCourse(User user, Course course) {
        return enrollmentRepository.findByUserAndCourse(user, course);
    }

    public Enrollment enrollCourse(User user, Course course) {
        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .course(course)
                .enrolledAt(new Date())
                .build();
        return enrollmentRepository.save(enrollment);
    }

}
