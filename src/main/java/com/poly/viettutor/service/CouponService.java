package com.poly.viettutor.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.poly.viettutor.model.Coupon;
import com.poly.viettutor.repository.CouponRepository;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public Optional<Coupon> findByCouponCode(String code) {
        return couponRepository.findByCouponCode(code);
    }

}
