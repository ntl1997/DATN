package com.poly.viettutor.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.poly.viettutor.model.Category;
import com.poly.viettutor.repository.CategoryRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Optional<Category> findById(int id) {
        return categoryRepository.findById(id);
    }

    public Category save(Category category) {
        this.setParentAndLevel(category);
        return categoryRepository.save(category);
    }

    public void deleteById(int id) {
        categoryRepository.deleteById(id);
    }

    // Láy danh sách danh mục theo cấp (cấp từ 1 đến 3)
    public List<Category> findAllCategoriesUpTo(int maxLevel) {
        return categoryRepository.findByLevelLessThanEqual(maxLevel);
    }

    // Gán danh mục cha và cấp cho danh mục
    public void setParentAndLevel(Category category) {
        int parentId = category.getParent().getCategoryId();
        if (parentId == 0) {
            category.setParent(null);
            category.setLevel(1); // Cấp 1 nếu không có parent
        } else {
            Category parent = categoryRepository.findById(parentId).get();
            int level = parent.getLevel() + 1;
            if (level > 3) {
                throw new IllegalArgumentException("Cấp độ danh mục không được vượt quá 3");
            }
            category.setParent(parent);
            category.setLevel(level);
        }
    }

}
