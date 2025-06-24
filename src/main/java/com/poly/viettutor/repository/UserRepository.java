package com.poly.viettutor.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poly.viettutor.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // Tìm kiếm người dùng theo Role
    // Ví dụ: tìm kiếm người dùng có role là "INSTRUCTOR"
    List<User> findByRolesRoleName(String roleName);

}
