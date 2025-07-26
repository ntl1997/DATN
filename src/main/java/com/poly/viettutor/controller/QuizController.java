package com.poly.viettutor.controller;

import com.poly.viettutor.model.Certificate;
import com.poly.viettutor.model.Option;
import com.poly.viettutor.model.Question;
import com.poly.viettutor.model.Quiz;
import com.poly.viettutor.model.QuizAnswer;
import com.poly.viettutor.model.QuizSubmission;
import com.poly.viettutor.model.User;
import com.poly.viettutor.service.CertificateService;
import com.poly.viettutor.service.OptionService;
import com.poly.viettutor.service.QuestionService;
import com.poly.viettutor.service.QuizService;
import com.poly.viettutor.service.UserService; // Import UserService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private UserService userService; // Inject UserService

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private QuestionService questionService; // Inject QuestionService
    // Inject QuestionService

    @Autowired
    private OptionService optionService; // Inject OptionService

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

        model.addAttribute("quiz", quiz);
        model.addAttribute("quizLimitReached", quizLimitReached); // ✅ truyền ra Thymeleaf để ẩn nút
        model.addAttribute("error", error != null && error); // ✅ để hiển thị lỗi nếu có
        model.addAttribute("title", "Chi tiết Quiz");
        model.addAttribute("content", "client/quiz/quiz");
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
    if (correctAnswers >= (totalScore / 2)) {
    var course = quiz.getModule().getCourse();

    boolean hasCertificate = certificateService.getCertificatesByUserId(user.getId())
            .stream()
            .anyMatch(cert -> cert.getCourse().getCourseId().equals(course.getCourseId()));

    if (!hasCertificate) {
        certificateService.saveCertificate(
                Certificate.builder()
                        .user(user)
                        .course(course)
                        .issuedAt(new Date())
                        .description("Chứng chỉ hoàn thành quiz với kết quả đạt yêu cầu")
                        .build()
        );
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
    model.addAttribute("latestSubmission", latestSubmission);
    model.addAttribute("correctAnswers", correctAnswers);
    model.addAttribute("incorrectAnswers", incorrectAnswers);
    model.addAttribute("certGranted", cert != null && cert); // ✅ Gửi ra để hiển thị thông báo
    model.addAttribute("title", "Chi tiết Quiz");
    model.addAttribute("content", "client/quiz/quiz-result");
    return "client/layout/index";
    }
}
