package com.poly.viettutor.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.poly.viettutor.repository.OrderDetailRepository;
import org.springframework.stereotype.Service;

import com.poly.viettutor.model.OrderDetail;

@Service
public class OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;

    OrderDetailService(OrderDetailRepository orderDetailRepository) {
        this.orderDetailRepository = orderDetailRepository;
    }

    public List<OrderDetail> findAll() {
        return orderDetailRepository.findAll();
    }

    public Optional<OrderDetail> findById(int id) {
        return orderDetailRepository.findById(id);
    }

    public OrderDetail save(OrderDetail orderDetail) {
        return orderDetailRepository.save(orderDetail);
    }

    public void deleteById(int id) {
        orderDetailRepository.deleteById(id);
    }

    public BigDecimal getTotalRevenueByInstructor(Long instructorId) {
        return orderDetailRepository.getTotalRevenueByInstructor(instructorId);
    }

}
