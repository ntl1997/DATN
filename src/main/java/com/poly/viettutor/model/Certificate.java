package com.poly.viettutor.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Certificates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer certificateId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course course;

    @Temporal(TemporalType.TIMESTAMP)
    private Date issuedAt;

    private String description;

    // Getter tùy chỉnh
    public String getTitle() {
        return course != null ? course.getTitle() : "Không rõ khóa học";
    }

    public Date getIssueDate() {
        return issuedAt;
    }

    public Integer getId() {
        return certificateId;
    }
}
