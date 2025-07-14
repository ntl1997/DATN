package com.poly.viettutor.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.poly.viettutor.model.ContactInfo;
import com.poly.viettutor.repository.ContactRepository;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    public List<ContactInfo> findAll() {
        return contactRepository.findAll();
    }

    public ContactInfo save(ContactInfo contactInfo) {
        return contactRepository.save(contactInfo);
    }
}