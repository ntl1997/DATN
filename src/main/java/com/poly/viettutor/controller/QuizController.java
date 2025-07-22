package com.poly.viettutor.controller;

import com.poly.viettutor.model.Quiz;
import com.poly.viettutor.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @GetMapping("/{id}")
    public String getQuizById(@PathVariable("id") Long id, Model model) {
        Quiz quiz = quizService.findById(id);
        if (quiz == null) {
            return "redirect:/error";
        }
        model.addAttribute("quiz", quiz);
        model.addAttribute("title", "Chi tiết Quiz");
        // model.addAttribute("content", "client/quiz/detail");
        return "client/layout/index";
    }

    @PostMapping("/submit")
    public String submitQuiz(
            @RequestParam("quizId") Long quizId,
            @RequestParam Map<String, String> answers,
            Model model) {
        int correctAnswers = quizService.evaluateQuiz(quizId, answers);
        int totalQuestions = quizService.getTotalQuestions(quizId);

        model.addAttribute("correctAnswers", correctAnswers);
        model.addAttribute("totalQuestions", totalQuestions);
        model.addAttribute("title", "Kết quả Quiz");
        // model.addAttribute("content", "client/quiz/result");
        return "client/layout/index";
    }
}
