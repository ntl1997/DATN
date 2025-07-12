package com.poly.viettutor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.poly.viettutor.repository.WishListRepository;
import com.poly.viettutor.model.Course;
import com.poly.viettutor.model.User;
import com.poly.viettutor.model.Wishlist;
import com.poly.viettutor.repository.CourseRepository;
import java.util.List;

@Service
public class WishListService {

    @Autowired
    private WishListRepository wishListRepository;

    @Autowired
    private CourseRepository courseRepository;

    public List<Wishlist> getWishlistByUser(User user) {
        return wishListRepository.findByUser(user);
    }

    public void deleteByIdAndUser(Integer wishlistId, User user) {
        Wishlist wishlist = wishListRepository.findById(wishlistId).orElse(null);
        if (wishlist != null && wishlist.getUser().getId() == user.getId()) {
            wishListRepository.deleteById(wishlistId);
        }
    }

    public void addWishlist(User user, Integer courseId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null)
            return;
        // Kiểm tra đã có trong wishlist chưa
        if (wishListRepository.findByUserAndCourse(user, course) != null)
            return;
        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .course(course)
                .addedAt(new java.util.Date())
                .build();
        wishListRepository.save(wishlist);
    }
}
