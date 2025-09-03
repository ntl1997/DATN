package com.poly.viettutor.controller;

import com.poly.viettutor.model.*;
import com.poly.viettutor.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private UserService userService;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private OptionService optionService;

    @Autowired
    private EnrollmentService enrollmentService;

    @GetMapping("/{id}")
    public String getQuizById(@PathVariable("id") Long id,
            @RequestParam(value = "error", required = false) Boolean error,
            Model model) {
        Quiz quiz = quizService.findById(id);
        if (quiz == null) {
            return "redirect:/error";
        }

        User user = userService.getCurrentUser();
        int submissionCount = quizService.countSubmissionsByUserAndQuiz(user.getId(), id);
        boolean quizLimitReached = submissionCount >= 3;
        Course course = quiz.getModule().getCourse();
        boolean isEnrolled = enrollmentService.isEnrolled(user, course);

        // ✅ Lấy lần nộp gần nhất của user hiện tại
        QuizSubmission latestSubmission = quiz.getQuizSubmissions().stream()
                .filter(sub -> sub.getUser().getId().equals(user.getId()))
                .max(Comparator.comparing(QuizSubmission::getSubmittedAt))
                .orElse(null);

        model.addAttribute("quiz", quiz);
        model.addAttribute("course", course);
        model.addAttribute("isEnrolled", isEnrolled);
        model.addAttribute("quizLimitReached", quizLimitReached);
        model.addAttribute("latestSubmission", latestSubmission); // ✅ Thêm dòng này
        model.addAttribute("error", error != null && error);
        model.addAttribute("title", "Chi tiết Quiz");
        model.addAttribute("content", "client/learning/quiz");
        return "client/layout/index";
    }

    @PostMapping
    public String submitQuiz(
            @RequestParam("quizId") Long quizId,
            @RequestParam Map<String, String> answers,
            Model model) {

        Quiz quiz = quizService.findById(quizId);
        if (quiz == null) {
            return "redirect:/error";
        }

        User user = userService.getCurrentUser();
        if (user == null) {
            return "redirect:/error";
        }

        int submissionCount = quizService.countSubmissionsByUserAndQuiz(user.getId(), quizId);
        if (submissionCount >= 3) {
            model.addAttribute("error", "Bạn đã đạt giới hạn số lần làm quiz.");
            return "redirect:/quiz/" + quizId + "?error=true";
        }

        QuizSubmission submission = QuizSubmission.builder()
                .quiz(quiz)
                .user(user)
                .submittedAt(new Date())
                .score(0)
                .build();
        quizService.saveQuizSubmission(submission);

        int correctAnswers = 0;
        for (Question question : quiz.getQuestions()) {
            String selectedOptionIdStr = answers.get("question-" + question.getQuestionId() + "-option");
            Long selectedOptionId = selectedOptionIdStr != null ? Long.parseLong(selectedOptionIdStr) : null;

            boolean isCorrect = question.getOptions().stream()
                    .anyMatch(option -> option.getOptionId().equals(selectedOptionId) && option.getIsCorrect());

            if (isCorrect) {
                correctAnswers += question.getScore();
            }

            QuizAnswer answer = QuizAnswer.builder()
                    .submission(submission)
                    .questionId(question.getQuestionId())
                    .selectedOptionId(selectedOptionId)
                    .isCorrect(isCorrect)
                    .build();
            quizService.saveQuizAnswer(answer);
        }

        submission.setScore(correctAnswers);
        quizService.saveQuizSubmission(submission);

        int totalScore = quiz.getQuestions().stream()
                .mapToInt(Question::getScore)
                .sum();

        boolean granted = false;
        var course = quiz.getModule().getCourse();
        boolean passedThisQuiz = correctAnswers >= (totalScore / 2);

        boolean hasCertificate = certificateService.getCertificatesByUserId(user.getId())
                .stream()
                .anyMatch(cert -> cert.getCourse().getCourseId().equals(course.getCourseId()));

        if (passedThisQuiz && !hasCertificate) {
            boolean completedAll = quizService.hasCompletedAllQuizzes(course.getCourseId(), user.getId());
            if (completedAll) {
                certificateService.saveCertificate(
                        Certificate.builder()
                                .user(user)
                                .course(course)
                                .issuedAt(new Date())
                                .description("Hoàn thành toàn bộ quiz trong khóa học")
                                .build());
                granted = true;
            }
        }

        if (granted) {
            return "redirect:/quiz/result/" + quizId + "?cert=true";
        } else {
            return "redirect:/quiz/result/" + quizId;
        }
    }

    @GetMapping("/result/{id}")
    public String getResultQuizById(
            @PathVariable("id") Long id,
            @RequestParam(value = "cert", required = false) Boolean cert,
            Model model) {

        Quiz quiz = quizService.findById(id);
        if (quiz == null) {
            return "redirect:/error";
        }

        QuizSubmission latestSubmission = quiz.getQuizSubmissions().stream()
                .max(Comparator.comparing(QuizSubmission::getSubmittedAt))
                .orElse(null);
        Course course = quiz.getModule().getCourse();
        int correctAnswers = 0;
        int incorrectAnswers = 0;

        if (latestSubmission != null) {
            for (QuizAnswer answer : latestSubmission.getAnswers()) {
                if (answer.getIsCorrect()) {
                    correctAnswers++;
                } else {
                    incorrectAnswers++;
                }
            }
        }

        int totalScore = quiz.getQuestions().stream()
                .mapToInt(Question::getScore)
                .sum();
        model.addAttribute("totalScore", totalScore);

        Map<Long, Question> questionMap = new HashMap<>();
        for (Question q : this.questionService.findAll()) {
            questionMap.put(q.getQuestionId(), q);
        }
        Map<Long, Option> optionMap = new HashMap<>();
        for (Option q : this.optionService.findAll()) {
            optionMap.put(q.getOptionId(), q);
        }

        Map<Long, Option> correctOptionMap = new HashMap<>();
        for (Question question : quiz.getQuestions()) {
            question.getOptions().stream()
                    .filter(Option::getIsCorrect)
                    .findFirst()
                    .ifPresent(opt -> correctOptionMap.put(question.getQuestionId(), opt));
        }

        model.addAttribute("correctOptionMap", correctOptionMap);
        model.addAttribute("optionMap", optionMap);
        model.addAttribute("questionMap", questionMap);
        model.addAttribute("quiz", quiz);
        model.addAttribute("course", course);
        model.addAttribute("latestSubmission", latestSubmission);
        model.addAttribute("correctAnswers", correctAnswers);
        model.addAttribute("incorrectAnswers", incorrectAnswers);
        model.addAttribute("certGranted", cert != null && cert);
        model.addAttribute("title", "Chi tiết Quiz");
        model.addAttribute("content", "client/learning/quiz-result");
        return "client/layout/index";
    }

    @GetMapping("/module/{moduleId}")
    public String viewQuizListByModule(@PathVariable("moduleId") Long moduleId, Model model) {
        List<Quiz> quizzes = quizService.findByModuleId(moduleId);
        User user = userService.getCurrentUser();

        // Map trạng thái hoàn thành từng quiz
        Map<Long, Boolean> quizCompletionMap = new HashMap<>();
        for (Quiz quiz : quizzes) {
            boolean completed = quizService.hasUserCompletedQuiz(quiz.getQuizId(), user.getId());
            quizCompletionMap.put(quiz.getQuizId(), completed);
        }

        Course course = quizzes.isEmpty() ? null : quizzes.get(0).getModule().getCourse();

        model.addAttribute("quizzes", quizzes);
        model.addAttribute("quizCompletionMap", quizCompletionMap);
        model.addAttribute("course", course);
        model.addAttribute("title", "Danh sách Quiz");
        model.addAttribute("content", "client/learning/quiz-list");
        return "client/layout/index";
    }
}
