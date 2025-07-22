package com.poly.viettutor.service;

import com.poly.viettutor.model.QuizAnswer;
import com.poly.viettutor.repository.QuizAnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizAnswerService {

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    public QuizAnswer save(QuizAnswer answer) {
        return quizAnswerRepository.save(answer);
    }

    public List<QuizAnswer> findBySubmissionId(Long submissionId) {
        return quizAnswerRepository.findBySubmissionSubmissionId(submissionId);
    }
}
