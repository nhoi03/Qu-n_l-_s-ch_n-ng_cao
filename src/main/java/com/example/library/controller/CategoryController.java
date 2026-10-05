package com.example.library.controller;

import com.example.library.dto.form.CategoryForm;
import com.example.library.entity.Category;
import com.example.library.exception.DuplicateResourceException;
import com.example.library.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listCategories(@RequestParam(required = false) String keyword,
                                 @RequestParam(required = false) Boolean active,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 Model model) {
        Page<Category> categoryPage = categoryService.getCategories(keyword, active, page, size);
        model.addAttribute("categories", categoryPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", categoryPage.getTotalPages());
        model.addAttribute("totalElements", categoryPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("active", active);
        return "categories/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("categoryForm", new CategoryForm());
        model.addAttribute("isEdit", false);
        return "categories/form";
    }

    @PostMapping
    public String createCategory(@Valid @ModelAttribute("categoryForm") CategoryForm form,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "categories/form";
        }

        try {
            categoryService.createCategory(form);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm thể loại thành công!");
            return "redirect:/categories";
        } catch (DuplicateResourceException ex) {
            bindingResult.rejectValue("name", "duplicate", ex.getMessage());
            model.addAttribute("isEdit", false);
            return "categories/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Category category = categoryService.getCategoryById(id);
        CategoryForm form = new CategoryForm(category.getId(), category.getName(), category.getDescription(), category.isActive());
        model.addAttribute("categoryForm", form);
        model.addAttribute("isEdit", true);
        model.addAttribute("categoryId", id);
        return "categories/form";
    }

    @PostMapping("/{id}")
    public String updateCategory(@PathVariable Long id,
                                 @Valid @ModelAttribute("categoryForm") CategoryForm form,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("categoryId", id);
            return "categories/form";
        }

        try {
            categoryService.updateCategory(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thể loại thành công!");
            return "redirect:/categories";
        } catch (DuplicateResourceException ex) {
            bindingResult.rejectValue("name", "duplicate", ex.getMessage());
            model.addAttribute("isEdit", true);
            model.addAttribute("categoryId", id);
            return "categories/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        categoryService.deleteCategory(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã vô hiệu hóa thể loại thành công!");
        return "redirect:/categories";
    }

    @PostMapping("/{id}/restore")
    public String restoreCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        categoryService.restoreCategory(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã kích hoạt lại thể loại thành công!");
        return "redirect:/categories";
    }
}
