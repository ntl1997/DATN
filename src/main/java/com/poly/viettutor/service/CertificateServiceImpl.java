package com.poly.viettutor.service;

import com.poly.viettutor.model.Certificate;
import com.poly.viettutor.model.Quiz;
import com.poly.viettutor.repository.CertificateRepository;
import com.poly.viettutor.repository.QuizRepository;
import com.poly.viettutor.repository.QuizSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CertificateServiceImpl implements CertificateService {

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizSubmissionRepository quizSubmissionRepository;

    @Autowired
    private QuizService quizService;

    @Override
    public List<Certificate> getCertificatesByUserId(Long userId) {
        List<Certificate> allCertificates = certificateRepository.getCertificatesWithCourseByUserId(userId);
        return allCertificates.stream()
                .filter(cert -> cert.getCourse() != null
                        && quizService.hasCompletedAllQuizzes(cert.getCourse().getCourseId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Certificate> searchCertificatesByUserAndTitle(Long userId, String keyword) {
        List<Certificate> certs = certificateRepository.searchCertificatesWithCourseByUserAndTitle(userId, keyword);
        return certs.stream()
                .filter(cert -> cert.getCourse() != null
                        && quizService.hasCompletedAllQuizzes(cert.getCourse().getCourseId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public Certificate getCertificateById(Integer certificateId) {
        Certificate cert = certificateRepository.findById(certificateId).orElse(null);
        if (cert == null || cert.getCourse() == null || cert.getUser() == null) return null;

        boolean completed = quizService.hasCompletedAllQuizzes(cert.getCourse().getCourseId(), cert.getUser().getId());
        return completed ? cert : null;
    }

    @Override
    public void saveCertificate(Certificate certificate) {
        certificateRepository.save(certificate);
    }

    // ✅ Logic kiểm tra đã hoàn thành tất cả quiz trong khóa học
    private boolean hasCompletedAllQuizzes(Long courseId, Long userId) {
    return quizService.hasCompletedAllQuizzes(courseId.intValue(), userId);
    }
}
