package com.poly.viettutor.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.poly.viettutor.model.Order;
import com.poly.viettutor.model.User;
import com.poly.viettutor.service.OrderService;
import com.poly.viettutor.service.UserService;
import com.poly.viettutor.service.VNPAYService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CheckoutController {

    private final OrderService orderService;
    private final UserService userService;
    private final VNPAYService vnPayService;

    CheckoutController(OrderService orderService,
            UserService userService,
            VNPAYService vnpayService) {
        this.orderService = orderService;
        this.userService = userService;
        this.vnPayService = vnpayService;
    }

    @PostMapping("/vnpay-checkout")
    public String submitOrder(
            @RequestParam double totalAmount,
            @RequestParam String couponCode,
            @RequestParam List<Integer> courseIds,
            HttpServletRequest request) {
        try {
            // Bước 1: Lưu đơn hàng với trạng thái "pending"
            User user = userService.getCurrentUser();
            Order order = orderService.create(user, totalAmount, couponCode, courseIds);

            // Bước 2: Gọi VNPAY để lấy link thanh toán
            String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
            String vnpayUrl = vnPayService.createOrder(
                    request,
                    (int) totalAmount, // VNPAY nhận đơn vị là đồng x 100
                    String.valueOf(order.getOrderId()), // orderInfo = ID đơn hàng
                    baseUrl);

            // Bước 3: Redirect người dùng sang cổng thanh toán
            return "redirect:" + vnpayUrl;

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/cart?checkoutError=true";
        }
    }

    @GetMapping("/vnpay-payment-return")
    public String paymentCompleted(HttpServletRequest request, Model model) {
        int paymentStatus = vnPayService.orderReturn(request);

        String orderInfo = request.getParameter("vnp_OrderInfo"); // orderId kiểu String
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
        String totalPrice = request.getParameter("vnp_Amount");
        String title = paymentStatus == 1 ? "Thanh toán thành công" : "Thanh toán thất bại";
        String content = "client/order-result";
        Boolean success = paymentStatus == 1 ? true : false;

        // Cập nhật trạng thái đơn hàng
        int orderId = Integer.parseInt(orderInfo);
        orderService.updateStatus(paymentStatus, orderId);

        // Hiển thị kết quả thanh toán
        model.addAttribute("title", title);
        model.addAttribute("content", content);
        model.addAttribute("success", success);
        model.addAttribute("orderId", orderInfo);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("paymentTime", paymentTime);
        model.addAttribute("transactionId", transactionId);
        return "client/layout/index";
    }

}
