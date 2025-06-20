package com.poly.viettutor.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Certificates")
@Data
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
}
