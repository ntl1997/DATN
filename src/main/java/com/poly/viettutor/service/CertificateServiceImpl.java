package com.poly.viettutor.service;

import com.poly.viettutor.model.Certificate;
import com.poly.viettutor.model.Course;
import com.poly.viettutor.model.User;
import com.poly.viettutor.repository.CertificateRepository;
import com.poly.viettutor.repository.CourseRepository;
import com.poly.viettutor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CertificateServiceImpl implements CertificateService {

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public List<Certificate> getCertificatesByUserId(Long userId) {
        return certificateRepository.getCertificatesWithCourseByUserId(userId);
    }

    @Override
    public List<Certificate> searchCertificatesByUserAndTitle(Long userId, String keyword) {
        return certificateRepository.searchCertificatesWithCourseByUserAndTitle(userId, keyword);
    }

    @Override
    public Certificate getCertificateById(Integer certificateId) {
        return certificateRepository.findById(certificateId).orElse(null);
    }

    @Override
    public void issueCertificateIfEligible(Long userId, Long courseId, String quizTitle, int score, int totalScore) {
        if (score >= totalScore * 0.5) {
            User user = userRepository.findById(userId).orElse(null);
            Course course = courseRepository.findById(courseId).orElse(null);
            if (user == null || course == null) return;

            boolean alreadyIssued = certificateRepository.existsByUserAndCourse(user, course);
            if (!alreadyIssued) {
                Certificate certificate = Certificate.builder()
                        .user(user)
                        .course(course)
                        .issuedAt(new Date())
                        .description("Đạt yêu cầu quiz: " + quizTitle)
                        .build();
                certificateRepository.save(certificate);
            }
        }
    }
}
