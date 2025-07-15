package com.poly.viettutor.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.poly.viettutor.model.Review;
import com.poly.viettutor.repository.ReviewRepository;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public List<Review> getReviewsByInstructor(Long instructorId) {
        return reviewRepository.findAllByInstructorId(instructorId);
    }

    public List<Review> getReviewsWrittenByInstructor(Long instructorUserId) {
        return reviewRepository.findAllByUserId(instructorUserId);
    }

    public Review getReviewById(Long id) {
        return reviewRepository.findById(id).orElse(null);
    }

    public void updateReview(Review review) {
        reviewRepository.save(review);
    }

    public void deleteReviewById(Long id) {
        if (reviewRepository.existsById(id)) {
            reviewRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Không tìm thấy review với ID: " + id);
        }
    }

}
