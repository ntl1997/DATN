package com.poly.viettutor.service;

import com.poly.viettutor.model.QuizSubmission;
import com.poly.viettutor.repository.QuizSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizSubmissionService {

    @Autowired
    private QuizSubmissionRepository quizSubmissionRepository;

    public QuizSubmission save(QuizSubmission submission) {
        return quizSubmissionRepository.save(submission);
    }

    public List<QuizSubmission> findByQuizId(Long quizId) {
        return quizSubmissionRepository.findByQuizQuizId(quizId);
    }
}
