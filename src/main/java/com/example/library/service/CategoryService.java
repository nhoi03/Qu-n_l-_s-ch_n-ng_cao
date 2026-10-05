package com.example.library.service;

import com.example.library.dto.form.CategoryForm;
import com.example.library.entity.Category;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CategoryService {
    Page<Category> getCategories(String keyword, Boolean active, int page, int size);
    List<Category> getActiveCategories();
    Category getCategoryById(Long id);
    Category createCategory(CategoryForm form);
    Category updateCategory(Long id, CategoryForm form);
    void deleteCategory(Long id);
    void restoreCategory(Long id);
}
