package com.poly.viettutor.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CourseOffering")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseOffering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseOfferingId;

    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course course;

    private String semester;

    private String className;

    @ManyToOne
    @JoinColumn(name = "instructorId")
    private User instructor;

    private LocalDate startDate;

    private LocalDate endDate;

    @Builder.Default
    private String status = "open"; // (open, closed, in_progress, finished)

    @OneToMany(mappedBy = "courseOffering", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments;

}
