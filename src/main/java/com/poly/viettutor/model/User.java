package com.poly.viettutor.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    private String fullName;

    @Column(unique = true)
    private String email;

    private String passwordHash;

    private String role;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
}
