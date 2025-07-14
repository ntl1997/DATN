package com.poly.viettutor.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.poly.viettutor.model.OrderDetail;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    @Query("SELECT COALESCE(SUM(od.price), 0) FROM OrderDetail od WHERE od.course.createdBy.id = :instructorId")
    BigDecimal getTotalRevenueByInstructor(@Param("instructorId") Long instructorId);

}
