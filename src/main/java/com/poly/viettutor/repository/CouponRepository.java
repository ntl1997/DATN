package com.poly.viettutor.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poly.viettutor.model.Coupon;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, String> {

    Optional<Coupon> findByCouponCode(String couponCode);

}
