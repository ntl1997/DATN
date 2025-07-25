package com.poly.viettutor.controller;

import com.poly.viettutor.model.Course;
import com.poly.viettutor.model.Option;
import com.poly.viettutor.model.Question;
import com.poly.viettutor.model.Quiz;
import com.poly.viettutor.model.QuizAnswer;
import com.poly.viettutor.model.QuizSubmission;
import com.poly.viettutor.model.User;
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
        Course course = quiz.getModule().getCourse();

        model.addAttribute("quiz", quiz);
        model.addAttribute("course", course);
        model.addAttribute("quizLimitReached", quizLimitReached); // ✅ truyền ra Thymeleaf để ẩn nút
        model.addAttribute("error", error != null && error); // ✅ để hiển thị lỗi nếu có
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

        // Lấy thông tin người dùng hiện tại
        User user = userService.getCurrentUser();
        if (user == null) {
            return "redirect:/error"; // Xử lý nếu không tìm thấy người dùng hiện tại
        }

        // Kiểm tra số lần làm quiz
        int submissionCount = quizService.countSubmissionsByUserAndQuiz(user.getId(), quizId);
        if (submissionCount >= 3) {
            model.addAttribute("error", "Bạn đã đạt giới hạn số lần làm quiz.");
            return "redirect:/quiz/" + quizId + "?error=true";
        }

        // Tạo QuizSubmission
        QuizSubmission submission = QuizSubmission.builder()
                .quiz(quiz)
                .user(user) // Sử dụng User lấy từ hàm getCurrentUser
                .submittedAt(new Date())
                .score(0) // Điểm sẽ được tính sau
                .build();

        // Lưu QuizSubmission vào cơ sở dữ liệu
        quizService.saveQuizSubmission(submission);

        int correctAnswers = 0;
        for (Question question : quiz.getQuestions()) {
            String selectedOptionIdStr = answers.get("question-" + question.getQuestionId() + "-option");
            Long selectedOptionId = selectedOptionIdStr != null ? Long.parseLong(selectedOptionIdStr) : null;

            boolean isCorrect = question.getOptions().stream()
                    .anyMatch(option -> option.getOptionId().equals(selectedOptionId) && option.getIsCorrect());

            if (isCorrect) {
                correctAnswers += question.getScore(); // Cộng điểm nếu đúng
            }

            // Lưu từng câu trả lời vào QuizAnswer
            QuizAnswer answer = QuizAnswer.builder()
                    .submission(submission)
                    .questionId(question.getQuestionId())
                    .selectedOptionId(selectedOptionId)
                    .isCorrect(isCorrect)
                    .build();
            quizService.saveQuizAnswer(answer); // Gọi service để lưu QuizAnswer
        }

        // Cập nhật điểm cho QuizSubmission
        submission.setScore(correctAnswers);
        quizService.saveQuizSubmission(submission); // Gọi service để lưu QuizSubmission

        model.addAttribute("quiz", quiz);
        model.addAttribute("correctAnswers", correctAnswers);
        model.addAttribute("totalQuestions", quiz.getQuestions().size());
        // model.addAttribute("content", "client/quiz/quiz-result");
        // return "client/layout/index";
        return "redirect:/quiz/" + quizId;
    }

    @GetMapping("/result/{id}")
    public String getResultQuizById(@PathVariable("id") Long id, Model model) {
        Quiz quiz = quizService.findById(id);
        if (quiz == null) {
            return "redirect:/error";
        }

        // Lấy lần submit cuối cùng
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

        Map<Long, Question> questionMap = new HashMap<>();
        for (Question q : this.questionService.findAll()) {
            questionMap.put(q.getQuestionId(), q);
        }
        Map<Long, Option> optionMap = new HashMap<>();
        for (Option q : this.optionService.findAll()) {
            optionMap.put(q.getOptionId(), q);
        }
        // Tạo map chứa đáp án đúng của từng câu hỏi
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
        model.addAttribute("title", "Chi tiết Quiz");
        model.addAttribute("content", "client/learning/quiz-result");
        return "client/layout/index";
    }
}
