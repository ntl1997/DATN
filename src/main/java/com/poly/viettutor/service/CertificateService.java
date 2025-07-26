package com.poly.viettutor.service;

import com.poly.viettutor.model.Certificate;
import java.util.List;

public interface CertificateService {

    // CertificateService.java
    List<Certificate> getCertificatesByUserId(Long userId);
    List<Certificate> searchCertificatesByUserAndTitle(Long userId, String keyword);

    Certificate getCertificateById(Integer certificateId);
    void saveCertificate(Certificate certificate);
}
