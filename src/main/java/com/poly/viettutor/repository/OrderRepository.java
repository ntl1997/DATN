package com.poly.viettutor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poly.viettutor.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

}
