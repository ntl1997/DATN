package com.poly.viettutor.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.poly.viettutor.repository.CourseRepository;
import com.poly.viettutor.repository.EnrollmentRepository;
import com.poly.viettutor.repository.OrderDetailRepository;
import com.poly.viettutor.repository.OrderRepository;
import org.springframework.stereotype.Service;

import com.poly.viettutor.model.Course;
import com.poly.viettutor.model.Enrollment;
import com.poly.viettutor.model.Order;
import com.poly.viettutor.model.OrderDetail;
import com.poly.viettutor.model.User;

@Service
public class OrderService {

    private final EnrollmentRepository enrollmentRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CourseRepository courseRepository;

    OrderService(OrderRepository orderRepository,
            OrderDetailRepository orderDetailRepository,
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Optional<Order> findById(int id) {
        return orderRepository.findById(id);
    }

    public Order create(User user, double totalAmount, String couponCode, List<Integer> courseIds) {
        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(totalAmount);
        order.setCouponCode(couponCode);
        order.setStatus("pending");
        order.setCreatedAt(new Date());
        Order savedOrder = orderRepository.save(order);
        courseIds.forEach(id -> {
            Course course = courseRepository.findById(id).orElseThrow(() -> new RuntimeException("Course not found"));
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(savedOrder);
            orderDetail.setCourse(course);
            orderDetail.setPrice(course.getPrice());
            orderDetailRepository.save(orderDetail);
        });
        return savedOrder;
    }

    public void updateStatus(int paymentStatus, int orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.setStatus(paymentStatus == 1 ? "paid" : "canceled");
            orderRepository.save(order);

            if (paymentStatus == 1) {
                order.getOrderDetails().forEach(orderDetail -> {
                    Enrollment enrollment = new Enrollment();
                    enrollment.setUser(order.getUser());
                    enrollment.setCourse(orderDetail.getCourse());
                    enrollment.setEnrolledAt(new Date());
                    enrollmentRepository.save(enrollment);
                });
            }
        }
    }

    public void deleteById(int id) {
        orderRepository.deleteById(id);
    }

    public List<Order> findByUser(User user) {
        return orderRepository.findByUser(user);
    }

    public List<Order> findOrdersByInstructorCoursesPurchasedByOthers(Long instructorId) {
        return orderRepository.findOrdersOfCoursesCreatedByInstructorButPurchasedByOthers(instructorId);
    }

}
