package com.poly.viettutor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poly.viettutor.model.Option;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {

}
