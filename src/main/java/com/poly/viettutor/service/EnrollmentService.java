package com.poly.viettutor.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

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

}
