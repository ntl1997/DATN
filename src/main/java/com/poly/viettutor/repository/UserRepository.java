package com.poly.viettutor.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.poly.viettutor.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // Tìm kiếm người dùng theo Role
    // Ví dụ: tìm kiếm người dùng có role là "INSTRUCTOR"
    List<User> findByRolesRoleName(String roleName);

    // Tìm kiếm người dùng theo Role (nhiều role)
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r.roleName IN :roleNames")
    List<User> findByRolesIn(@Param("roleNames") List<String> roleNames);

}
