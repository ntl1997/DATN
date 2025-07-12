package com.poly.viettutor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poly.viettutor.model.Course;
import com.poly.viettutor.model.User;
import com.poly.viettutor.model.Wishlist;

@Repository
public interface WishListRepository extends JpaRepository<Wishlist, Integer> {
    List<Wishlist> findByUser(User user);

    Wishlist findByUserAndCourse(User user, Course course);
}
