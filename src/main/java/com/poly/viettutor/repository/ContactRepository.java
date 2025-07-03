package com.poly.viettutor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poly.viettutor.model.ContactInfo;

@Repository
public interface ContactRepository extends JpaRepository<ContactInfo, Integer> {
    // Thêm các phương thức truy vấn tùy chỉnh nếu cần
}
