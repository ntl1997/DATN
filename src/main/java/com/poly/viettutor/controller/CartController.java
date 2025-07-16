package com.poly.viettutor.controller;

import com.poly.viettutor.service.CouponService;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.poly.viettutor.model.Coupon;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CartController {

    private final CouponService couponService;

    CartController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping("/cart")
    public String showCart(Model model) {
        model.addAttribute("title", "giỏ hàng");
        model.addAttribute("content", "client/cart");
        model.addAttribute("scripts", "client/cart");
        return "client/layout/index";
    }

    @PostMapping("/cart")
    public String getCoupon(@RequestParam("couponCode") String couponCode, Model model) {
        Optional<Coupon> couponOption = couponService.findByCouponCode(couponCode);
        if (couponOption.isEmpty()) {
            return "redirect:/cart?notFound";
        }
        model.addAttribute("title", "giỏ hàng");
        model.addAttribute("content", "client/cart");
        model.addAttribute("scripts", "client/cart");
        model.addAttribute("coupon", couponOption.get());
        return "client/layout/index";
    }

}
