package com.poly.viettutor.service;

import com.poly.viettutor.model.Certificate;
import com.poly.viettutor.repository.CertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CertificateServiceImpl implements CertificateService {

    @Autowired
    private CertificateRepository certificateRepository;

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
    public void saveCertificate(Certificate certificate) {
        certificateRepository.save(certificate);
    }
}
