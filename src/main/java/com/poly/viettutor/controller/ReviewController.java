package com.poly.viettutor.controller;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poly.viettutor.model.Review;
import com.poly.viettutor.model.User;
import com.poly.viettutor.service.ReviewService;
import com.poly.viettutor.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ReviewController {

    private final UserService userService;
    private final ReviewService reviewService;

    public ReviewController(UserService userService, ReviewService reviewService) {
        this.userService = userService;
        this.reviewService = reviewService;
    }

    @GetMapping("/reviews")
    public String instructorReviews(Model model, HttpServletRequest request) {
        User currentUser = userService.getCurrentUser();
        model.addAttribute("user", currentUser);
        List<Review> reviews = reviewService.getReviewsByInstructor(currentUser.getId());
        List<Review> reviewsGive = reviewService.getReviewsWrittenByInstructor(currentUser.getId());
        model.addAttribute("reviews", reviews);
        model.addAttribute("reviewsGive", reviewsGive);

        model.addAttribute("reviews", List.of());
        model.addAttribute("title", "Đánh giá");
        model.addAttribute("content", "client/instructor/instructor-reviews");
        return "client/layout/index";
    }

    @PostMapping("/review/update")
    public String updateReview(@RequestParam Long reviewId,
            @RequestParam int rating,
            @RequestParam String comment,
            RedirectAttributes redirectAttributes) {
        Review review = reviewService.getReviewById(reviewId);
        if (review != null) {
            review.setRating(rating);
            review.setComment(comment.trim());
            review.setReviewedAt(new Date());
            reviewService.updateReview(review);
            redirectAttributes.addFlashAttribute("success", "Cập nhật review thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Cập nhập không thành công");
        }
        return "redirect:/reviews";
    }

    @PostMapping("/review/delete/{id}")
    public String deleteReview(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            reviewService.deleteReviewById(id);
            redirectAttributes.addFlashAttribute("success", "Xóa review thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa review: " + e.getMessage());
        }
        return "redirect:/reviews";
    }

}
