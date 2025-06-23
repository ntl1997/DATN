package com.poly.viettutor.controller.admin;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.poly.viettutor.model.Category;
import com.poly.viettutor.service.CategoryService;
import com.poly.viettutor.utils.FileUtils;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PutMapping;

@Controller
public class AdminCategoryController {

    private final CategoryService categoryService;

    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }

    @GetMapping("/admin/categories")
    public String listCategory(Model model) {
        model.addAttribute("title", "Quản lý danh mục");
        model.addAttribute("content", "admin/category/list-category");
        model.addAttribute("scripts", "admin/category/list-category");
        model.addAttribute("categories", categoryService.findAll());
        return "admin/layout/index";
    }

    @GetMapping("/admin/categories/new")
    public String showCreateCategory(@ModelAttribute("category") Category category, Model model) {
        model.addAttribute("title", "Tạo mới danh mục");
        model.addAttribute("content", "admin/category/create-category");
        model.addAttribute("scripts", "admin/category/create-category");
        model.addAttribute("parents", categoryService.findAllCategoriesUpTo(2));
        return "admin/layout/index";
    }

    @GetMapping("/admin/categories/edit/{id}")
    public String showEditCategory(@PathVariable("id") int id, Model model) {
        Optional<Category> categoryOPOptional = categoryService.findById(id);
        if (categoryOPOptional.isPresent()) {
            Category category = categoryOPOptional.get();
            model.addAttribute("title", "Chỉnh sửa danh mục");
            model.addAttribute("content", "admin/category/edit-category");
            model.addAttribute("scripts", "admin/category/edit-category");
            model.addAttribute("parents", categoryService.findAllCategoriesUpTo(2));
            model.addAttribute("category", category);
            return "admin/layout/index";
        } else {
            return "redirect:/admin/categories?notFound=true";
        }
    }

    @PostMapping("/admin/categories/create")
    public String createCategory(@Valid @ModelAttribute("category") Category category,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes, Model model) {
        if (category.getName().isEmpty()) {
            return "redirect:/admin/categories/new?validError=true";
        }

        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                FileUtils.deleteImageIfExists(category.getImageUrl(), "uploads/category/");
                String fileName = FileUtils.saveImage(imageFile, "uploads/category/");
                category.setImageUrl(fileName);
            }
            categoryService.save(category);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("createError", e.getMessage());
            return "redirect:/admin/categories";
        }

        return "redirect:/admin/categories?createSuccess=true";
    }

    @PutMapping("/admin/categories/update/{id}")
    public String updateCategory(@Valid @ModelAttribute("category") Category category,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @PathVariable("id") int id, RedirectAttributes redirectAttributes, Model model) {
        if (category.getName().isEmpty()) {
            return "redirect:/admin/categories/edit/" + category.getCategoryId() + "?validError=true";
        }

        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                FileUtils.deleteImageIfExists(category.getImageUrl(), "uploads/category/");
                String fileName = FileUtils.saveImage(imageFile, "uploads/category/");
                category.setImageUrl(fileName);
            }
            category.setCategoryId(id);
            categoryService.save(category);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("updateError", e.getMessage());
            return "redirect:/admin/categories";
        }

        return "redirect:/admin/categories?updateSuccess=true";
    }

    @DeleteMapping("/admin/categories/delete/{id}")
    public String deleteCategory(@PathVariable("id") int id,
            RedirectAttributes redirectAttributes, Model model) {
        try {
            String fileName = categoryService.findById(id).get().getImageUrl();
            FileUtils.deleteImageIfExists(fileName, "uploads/category/");
            categoryService.deleteById(id);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("deleteError", e.getMessage());
            return "redirect:/admin/categories";
        }
        return "redirect:/admin/categories?deleteSuccess=true";
    }

}
