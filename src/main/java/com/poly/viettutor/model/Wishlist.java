package com.poly.viettutor.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Wishlist")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wishlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer wishlistId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course course;

    @Temporal(TemporalType.TIMESTAMP)
    private Date addedAt;
}
