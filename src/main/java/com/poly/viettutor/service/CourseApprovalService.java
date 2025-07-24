package com.poly.viettutor.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import com.poly.viettutor.repository.CourseRepository;
import org.springframework.stereotype.Service;

import com.poly.viettutor.model.Course;
import com.poly.viettutor.model.CourseApproval;
import com.poly.viettutor.model.User;
import com.poly.viettutor.repository.CourseApprovalRepository;

@Service
public class CourseApprovalService {

    private final CourseRepository courseRepository;

    private final CourseApprovalRepository courseApprovalRepository;

    public CourseApprovalService(CourseApprovalRepository courseApprovalRepository, CourseRepository courseRepository) {
        this.courseApprovalRepository = courseApprovalRepository;
        this.courseRepository = courseRepository;
    }

    public List<CourseApproval> findAll() {
        return courseApprovalRepository.findAll();
    }

    public Optional<CourseApproval> findById(int id) {
        return courseApprovalRepository.findById(id);
    }

    public CourseApproval save(CourseApproval courseApproval) {
        return courseApprovalRepository.save(courseApproval);
    }

    public void deleteById(int id) {
        courseApprovalRepository.deleteById(id);
    }

    public CourseApproval create(User user, Course course) {
        CourseApproval courseApproval = CourseApproval.builder()
                .course(course)
                .requestedBy(user)
                .status("pending")
                .requestedAt(new Date())
                .build();
        return courseApprovalRepository.save(courseApproval);
    }

    public CourseApproval response(User user, CourseApproval approval, String status, String note) {
        approval.setApprovedBy(user);
        approval.setStatus(status);
        approval.setNote(note);
        approval.setRespondedAt(new Date());

        Course course = approval.getCourse();
        if (status.equals("approved")) {
            course.setStatus("publish");
        } else if (status.equals("rejected")) {
            course.setStatus("draft");
        }

        courseRepository.save(course);
        return courseApprovalRepository.save(approval);
    }

}
