package com.poly.viettutor.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.poly.viettutor.model.Option;
import com.poly.viettutor.repository.OptionRepository;

@Service
public class OptionService {
    @Autowired
    private OptionRepository optionRepository;

    public List<Option> findAll() {
        return optionRepository.findAll();
    }

    public Option save(Option Option) {
        return optionRepository.save(Option);
    }

    public Option findById(Long id) {
        return optionRepository.findById(id).orElse(null);
    }
}
