package com.poly.viettutor.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.poly.viettutor.repository.QuizRepository;

@Service
public class QuizService {
    @Autowired
    private QuizRepository quizRepository;

    public List<Map<String, Object>> getQuizSubmissionsByCourseTitle(String courseTitle) {
        List<Object[]> rawResults = quizRepository.getQuizSubmissionsByCourseTitle(courseTitle);

        List<Map<String, Object>> formattedResults = new ArrayList<>();

        for (Object[] row : rawResults) {
            Map<String, Object> map = new HashMap<>();
            map.put("fullName", row[0]);
            map.put("email", row[1]);
            map.put("quizTitle", row[2]);
            map.put("submittedAt", row[3]);
            map.put("score", row[4]);
            map.put("totalScore", row[5]);
            map.put("moduleTitle", row[6]);
            formattedResults.add(map);
        }

        return formattedResults;
    }

}
