package com.poly.viettutor.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "BlogPosts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postId;

    private String title;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String content;

    private String imageBlog;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "createdBy")
    private User createdBy;
}
