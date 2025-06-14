package com.poly.viettutor.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "CourseMaterials")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer materialId;

    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course course;

    private String fileName;

    @Column(length = 500)
    private String fileUrl;

    private String fileType;

    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadedAt;
}
