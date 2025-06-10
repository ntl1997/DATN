package com.poly.viettutor.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ContactInfo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer contactId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String message;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
}
