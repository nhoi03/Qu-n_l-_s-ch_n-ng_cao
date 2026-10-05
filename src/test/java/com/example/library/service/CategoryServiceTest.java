package com.example.library.service;

import com.example.library.dto.form.CategoryForm;
import com.example.library.entity.Category;
import com.example.library.exception.DuplicateResourceException;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.repository.CategoryRepository;
import com.example.library.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category(1L, "Công nghệ thông tin", "Sách lập trình", true);
    }

    @Test
    @DisplayName("Lấy danh sách thể loại có phân trang và tìm kiếm")
    void testGetCategories() {
        Page<Category> page = new PageImpl<>(List.of(category));
        when(categoryRepository.searchCategories(eq("Công nghệ"), eq(true), any(Pageable.class))).thenReturn(page);

        Page<Category> result = categoryService.getCategories("Công nghệ", true, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Công nghệ thông tin", result.getContent().get(0).getName());
    }

    @Test
    @DisplayName("Thêm mới thể loại thành công")
    void testCreateCategory_Success() {
        CategoryForm form = new CategoryForm(null, "Kinh tế", "Sách kinh tế", true);
        when(categoryRepository.existsByNameIgnoreCase("Kinh tế")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category c = invocation.getArgument(0);
            c.setId(2L);
            return c;
        });

        Category created = categoryService.createCategory(form);

        assertNotNull(created);
        assertEquals("Kinh tế", created.getName());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("Thêm thể loại thất bại khi tên đã tồn tại")
    void testCreateCategory_DuplicateName() {
        CategoryForm form = new CategoryForm(null, "Công nghệ thông tin", "Sách IT", true);
        when(categoryRepository.existsByNameIgnoreCase("Công nghệ thông tin")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> categoryService.createCategory(form));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Xóa mềm thể loại (chuyển active = false)")
    void testDeleteCategory_SoftDelete() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(1L);

        assertFalse(category.isActive());
        verify(categoryRepository).save(category);
    }
}
