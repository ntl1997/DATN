package com.poly.viettutor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.poly.viettutor.model.Order;

import com.poly.viettutor.model.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query("SELECT DISTINCT o FROM Order o " +
            "JOIN o.orderDetails od " +
            "JOIN od.course c " +
            "WHERE c.createdBy.id = :instructorId " +
            "AND o.user.id <> :instructorId")
    List<Order> findOrdersOfCoursesCreatedByInstructorButPurchasedByOthers(@Param("instructorId") Long instructorId);

    List<Order> findByUser(User user);
}
