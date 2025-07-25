package com.poly.viettutor.controller;

import com.poly.viettutor.model.Quiz;
import com.poly.viettutor.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/result")
public class ResultController {
    @Autowired
    private QuizService quizService;

    @GetMapping("/course/{courseId}")
    public String getQuizzesByCourseId(@PathVariable Integer courseId, Model model) {
        // Fetch quizzes for the course using QuizService
        List<Quiz> quizzes = quizService.getQuizzesByCourseId(courseId);

        // Add quizzes to the model
        model.addAttribute("quizzes", quizzes);
        model.addAttribute("title", "Danh sách Quiz");
        model.addAttribute("content", "client/quiz/quiz-result");

        return "client/layout/index";
    }
}
