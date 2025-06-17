package com.poly.viettutor.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer notificationId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    private String title;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String message;

    private Boolean isRead;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
}
