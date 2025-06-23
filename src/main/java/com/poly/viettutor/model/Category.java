package com.poly.viettutor.model;

import lombok.*;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "Categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer categoryId;

    private String name;

    private String imageUrl;

    private int level; // danh mục có 3 cấp 1, 2, 3

    @ManyToOne
    @JoinColumn(name = "parentId")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> children;

    @OneToMany(mappedBy = "category")
    private List<CourseCategory> courseCategories;
}
