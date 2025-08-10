package com.poly.viettutor.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.poly.viettutor.dto.RegisterRequest;
import com.poly.viettutor.dto.UpdateUserInfoDTO;
import com.poly.viettutor.model.Role;
import com.poly.viettutor.model.User;
import com.poly.viettutor.repository.RoleRepository;
import com.poly.viettutor.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Tạo tài khoản hàng loạt bằng excel
    @Async
    public void importUsersFromExcel(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();
        rowIterator.next(); // bỏ dòng tiêu đề

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            String fullName = row.getCell(1).getStringCellValue();
            String email = row.getCell(2).getStringCellValue();
            String phoneNumber = row.getCell(3).getStringCellValue();

            if (!userRepository.existsByEmail(email)) {
                RegisterRequest request = RegisterRequest.builder()
                        .fullname(fullName)
                        .email(email)
                        .phoneNumber(phoneNumber)
                        .password("123456")
                        .build();

                register(request);
            }
        }

        workbook.close();
    }

    // đăng ký tài khoản
    public void register(RegisterRequest registerRequest) {
        User user = new User();
        user.setFullname(registerRequest.getFullname());
        user.setEmail(registerRequest.getEmail());
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setOccupation(registerRequest.getOccupation());
        user.setBiography(registerRequest.getBiography());
        user.setImage(null); // Ảnh sẽ được xử lý sau
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreatedAt(new Date());
        user.setStatus(true);
        setRoleForUser(user, "STUDENT"); // Gán role mặc định là STUDENT
        if (registerRequest.getIsInstructor()) {
            setRoleForUser(user, "INSTRUCTOR");
        }
        userRepository.save(user);
    }

    // Kiểm tra email đã tồn tại hay chưa
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    // Gán role cho user đăng ký
    public void setRoleForUser(User user, String roleName) {
        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException("Role " + roleName + " does not exist"));
        user.getRoles().add(role);
    }

    // Lấy thông tin người dùng hiện đang đăng nhập
    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElse(null);
    }

    // Lưu hoặc cập nhật thông tin user
    public void save(User user) {
        userRepository.save(user);
    }

    // Lấy danh sách tất cả admin
    public List<User> getAllAdmins() {
        return userRepository.findByRolesRoleName("ADMIN");
    }

    // Lấy danh sách tất cả giảng viên
    public List<User> getAllInstructors() {
        return userRepository.findByRolesRoleName("INSTRUCTOR");
    }

    // Lấy danh sách tất cả học viên
    public List<User> getAllStudents() {
        return userRepository.findByRolesRoleName("STUDENT");
    }

    // Lấy danh sách tất cả giảng viên và học viên
    public List<User> getAllInstructorsAndStudents() {
        List<String> roleNames = List.of("INSTRUCTOR", "STUDENT");
        return userRepository.findByRolesIn(roleNames);
    }

    // Kiểm tra role của user
    public boolean hasRole(User user, String roleName) {
        if (user == null || user.getRoles() == null)
            return false;
        return user.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equalsIgnoreCase(roleName));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User updateInfo(long id, UpdateUserInfoDTO userDTO) {
        User user = findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setFullname(userDTO.getFullname());
        user.setEmail(userDTO.getEmail());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setOccupation(userDTO.getOccupation());
        if (userDTO.getIsInstructor()) {
            setRoleForUser(user, "INSTRUCTOR");
        } else {
            user.getRoles().removeIf(role -> role.getRoleName().equals("INSTRUCTOR"));
        }
        return userRepository.save(user);
    }

    public void changeStatus(long id) {
        User user = findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setStatus(!user.getStatus());
        save(user);
    }

    public void resetPassword(long id) {
        User user = findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setPassword(passwordEncoder.encode("123456")); // Đặt lại mật khẩu mặc định: 123456
        save(user);
    }

    public List<Map<String, Object>> getTop5Ints() {
        List<Object[]> rows = userRepository.findTop5InstructorsWithMostStudents();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : rows) {
            Map<String, Object> map = new HashMap<>();
            map.put("ten", row[0]);
            map.put("avatar", row[1]);
            map.put("sdt", row[2]);
            map.put("stcount", row[3]);
            result.add(map);
        }

        return result;
    }

    public Map<String, Object> getTop5InstructorsForChart() {
        List<Object[]> rawResults = userRepository.findTop5InstructorsWithMostStudents();

        List<String> names = new ArrayList<>();
        List<Long> studentCounts = new ArrayList<>();

        for (Object[] row : rawResults) {
            // row[0] = fullName (String)
            // row[3] = studentCount (Number)
            names.add((String) row[0]);
            studentCounts.add(((Number) row[3]).longValue());
        }

        // Trả về Map chứa 2 mảng để đưa lên Thymeleaf
        Map<String, Object> result = new HashMap<>();
        result.put("labels", names);
        result.put("data", studentCounts);
        return result;
    }

}
