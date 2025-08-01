package com.poly.viettutor.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Enrollments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer enrollmentId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "courseOfferingId")
    private CourseOffering courseOffering;

    @Temporal(TemporalType.TIMESTAMP)
    private Date enrolledAt;

    @ManyToOne
    @JoinColumn(name = "EnrolledBy")
    private User enrolledBy;
}
