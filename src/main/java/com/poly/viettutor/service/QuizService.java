package com.poly.viettutor.service;

import com.poly.viettutor.model.*;
import com.poly.viettutor.repository.QuizRepository;
import com.poly.viettutor.repository.QuizAnswerRepository;
import com.poly.viettutor.repository.QuizSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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

    public List<Quiz> findByModuleId(Long moduleId) {
        return quizRepository.findAll().stream()
                .filter(q -> q.getModule() != null && moduleId.equals(q.getModule().getModuleId()))
                .collect(Collectors.toList());
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

            Long questionId = Long.valueOf(question.getQuestionId().longValue());

            QuizAnswer answer = QuizAnswer.builder()
                    .submission(submission)
                    .questionId(questionId)
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
                        && Objects.equals(quiz.getModule().getCourse().getCourseId(), courseId))
                .collect(Collectors.toList());
    }

    public int countSubmissionsByUserAndQuiz(Long userId, Long quizId) {
        List<QuizSubmission> submissions = quizSubmissionRepository.findByUserIdAndQuizQuizId(userId, quizId);
        return submissions != null ? submissions.size() : 0;
    }

    public boolean hasCompletedAllQuizzes(Integer courseId, Long userId) {
        if (userId == null)
            return false;

        List<Quiz> quizzes = getQuizzesByCourseId(courseId);
        if (quizzes.isEmpty())
            return false;

        for (Quiz quiz : quizzes) {
            List<QuizSubmission> submissions = quizSubmissionRepository.findByUserIdAndQuizQuizId(userId,
                    quiz.getQuizId());

            boolean hasPassed = submissions.stream().anyMatch(sub -> {
                List<Question> questions = quiz.getQuestions();
                int totalScore = (questions != null)
                        ? questions.stream().mapToInt(Question::getScore).sum()
                        : 0;
                return sub.getScore() != null && sub.getScore() >= totalScore / 2;
            });

            if (!hasPassed)
                return false;
        }

        return true;
    }

    public List<Map<String, Object>> getQuizSubmissionsByCourseTitle(String courseTitle) {
        List<Object[]> rawResults = quizRepository.getQuizSubmissionsByCourseTitles(courseTitle);
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

    public List<Map<String, Object>> getQuizSubmissionsByInstructorId(Long instructorId) {
        List<Object[]> rawResults = quizRepository.getQuizSubmissionsByInstructorId(instructorId);
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

    public boolean hasUserCompletedQuiz(Long quizId, Long userId) {
        List<QuizSubmission> submissions = quizSubmissionRepository.findByUserIdAndQuizQuizId(userId, quizId);
        Quiz quiz = findById(quizId);
        if (quiz == null)
            return false;

        int totalScore = quiz.getQuestions().stream().mapToInt(Question::getScore).sum();
        return submissions.stream().anyMatch(s -> s.getScore() != null && s.getScore() >= totalScore / 2);
    }

    public List<Object[]> getQuizProgressByCourseTitle(String courseTitle) {
        return quizRepository.findQuizProgressByCourseTitle(courseTitle);
    }

    public List<Object[]> findQuizProgressByCourseTitleAndUserId(String courseTitle, long userId) {
        return quizRepository.findQuizProgressByCourseTitleAndUserId(courseTitle, userId);
    }

}
