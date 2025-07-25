package com.poly.viettutor.service;

import com.poly.viettutor.model.Quiz;
import com.poly.viettutor.model.Question;
import com.poly.viettutor.model.Option;
import com.poly.viettutor.model.QuizAnswer;
import com.poly.viettutor.model.QuizSubmission;
import com.poly.viettutor.repository.QuizRepository;
import com.poly.viettutor.repository.QuizAnswerRepository;
import com.poly.viettutor.repository.QuizSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private QuizSubmissionRepository quizSubmissionRepository;

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

    public QuizSubmission processQuizSubmission(Long quizId, Map<String, String> answers) {
        Quiz quiz = findById(quizId);
        QuizSubmission submission = QuizSubmission.builder()
                .quiz(quiz)
                .submittedAt(new Date())
                .score(0)
                .build();

        int score = 0;
        for (Question question : quiz.getQuestions()) {
            String selectedOptionIdStr = answers.get("question-" + question.getQuestionId() + "-option");
            Long selectedOptionId = selectedOptionIdStr != null ? Long.parseLong(selectedOptionIdStr) : null;

            boolean isCorrect = question.getOptions().stream()
                    .anyMatch(option -> option.getOptionId().equals(selectedOptionId) && option.getIsCorrect());

            if (isCorrect) {
                score += question.getScore();
            }

            QuizAnswer answer = QuizAnswer.builder()
                    .submission(submission)
                    .questionId(question.getQuestionId())
                    .selectedOptionId(selectedOptionId)
                    .isCorrect(isCorrect)
                    .build();
            saveQuizAnswer(answer);
        }

        submission.setScore(score);
        saveQuizSubmission(submission);
        return submission;
    }

    public void saveQuizAnswer(QuizAnswer answer) {
        quizAnswerRepository.save(answer);
    }

    public void saveQuizSubmission(QuizSubmission submission) {
        quizSubmissionRepository.save(submission);
    }

    public List<Quiz> getQuizzesByCourseId(Integer courseId) {
        return quizRepository.findAll().stream()
                .filter(quiz -> quiz.getModule() != null && quiz.getModule().getCourse() != null
                        && quiz.getModule().getCourse().getCourseId().equals(courseId))
                .collect(Collectors.toList());
    }

    public int countSubmissionsByUserAndQuiz(Long userId, Long quizId) {
        return (int) quizSubmissionRepository.findAll().stream()
                .filter(submission -> submission.getUser().getId() == userId
                        && submission.getQuiz().getQuizId().equals(quizId))
                .count();
    }
}
