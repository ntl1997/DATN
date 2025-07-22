package com.poly.viettutor.service;

import com.poly.viettutor.model.Quiz;
import com.poly.viettutor.model.Question;
import com.poly.viettutor.model.Option;
import com.poly.viettutor.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    public List<Quiz> findAll() {
        return quizRepository.findAll();
    }

    public Quiz findById(Long id) {
        return quizRepository.findById(id).orElse(null);
    }

    public int evaluateQuiz(Long quizId, Map<String, String> answers) {
        Quiz quiz = findById(quizId);
        if (quiz == null)
            return 0;

        int correctAnswers = 0;
        for (Question question : quiz.getQuestions()) {
            String answerKey = "question-" + question.getQuestionId() + "-option";
            if (answers.containsKey(answerKey)) {
                Long selectedOptionId = Long.parseLong(answers.get(answerKey));
                for (Option option : question.getOptions()) {
                    if (option.getOptionId().equals(selectedOptionId) && option.getIsCorrect()) {
                        correctAnswers++;
                        break;
                    }
                }
            }
        }
        return correctAnswers;
    }

    public int getTotalQuestions(Long quizId) {
        Quiz quiz = findById(quizId);
        return quiz != null ? quiz.getQuestions().size() : 0;
    }
}
