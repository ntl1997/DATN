package com.poly.viettutor.model;

import lombok.*;
import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "CourseCategories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(CourseCategory.PK.class)
public class CourseCategory {
    @Id
    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course course;

    @Id
    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category category;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PK implements Serializable {
        private Integer course;
        private Integer category;
    }
}
