package com.poly.viettutor.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "CourseApprovals")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer approvalId;

    @ManyToOne
    @JoinColumn(name = "CourseId")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "RequestedBy")
    private User requestedBy;

    @ManyToOne
    @JoinColumn(name = "ApprovedBy")
    private User approvedBy;

    private String status; // "pending", "approved", "rejected"

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String note;

    @Temporal(TemporalType.TIMESTAMP)
    private Date requestedAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date respondedAt;

}
