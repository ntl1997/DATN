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

    @Query(value = "SELECT TOP 5 u.fullName AS teacherName, " +
            "u.image AS teacherImage, " +
            "u.phoneNumber AS teacherPhone, " +
            "COUNT(e.userId) AS studentCount " +
            "FROM Users u " +
            "JOIN UserRoles ur ON u.userId = ur.userId " +
            "JOIN Roles r ON ur.roleId = r.roleId " +
            "LEFT JOIN Courses c ON u.userId = c.CreatedBy " +
            "LEFT JOIN Enrollments e ON c.CourseId = e.CourseId " +
            "WHERE r.role = 'INSTRUCTOR' " +
            "GROUP BY u.userId, u.fullName, u.image, u.phoneNumber " +
            "ORDER BY studentCount DESC", nativeQuery = true)
    List<Object[]> findTop5InstructorsWithMostStudents();

}
