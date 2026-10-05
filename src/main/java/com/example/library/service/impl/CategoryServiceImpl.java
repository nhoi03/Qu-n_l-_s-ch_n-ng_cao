package com.example.library.service.impl;

import com.example.library.dto.form.CategoryForm;
import com.example.library.entity.Category;
import com.example.library.exception.DuplicateResourceException;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.repository.CategoryRepository;
import com.example.library.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Category> getCategories(String keyword, Boolean active, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        String cleanKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        return categoryRepository.searchCategories(cleanKeyword, active, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getActiveCategories() {
        return categoryRepository.findByActiveTrueOrderByNameAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thể loại với ID: " + id));
    }

    @Override
    public Category createCategory(CategoryForm form) {
        String trimmedName = form.getName().trim();
        if (categoryRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new DuplicateResourceException("Tên thể loại '" + trimmedName + "' đã tồn tại");
        }

        Category category = new Category();
        category.setName(trimmedName);
        category.setDescription(form.getDescription());
        category.setActive(form.isActive());

        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Long id, CategoryForm form) {
        Category category = getCategoryById(id);
        String trimmedName = form.getName().trim();

        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(trimmedName, id)) {
            throw new DuplicateResourceException("Tên thể loại '" + trimmedName + "' đã được sử dụng");
        }

        category.setName(trimmedName);
        category.setDescription(form.getDescription());
        category.setActive(form.isActive());

        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        category.setActive(false);
        categoryRepository.save(category);
    }

    @Override
    public void restoreCategory(Long id) {
        Category category = getCategoryById(id);
        category.setActive(true);
        categoryRepository.save(category);
    }
}
