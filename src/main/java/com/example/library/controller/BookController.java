package com.example.library.controller;

import com.example.library.dto.form.BookForm;
import com.example.library.entity.Book;
import com.example.library.entity.enums.BookStatus;
import com.example.library.exception.BusinessRuleException;
import com.example.library.exception.DuplicateResourceException;
import com.example.library.service.BookService;
import com.example.library.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final CategoryService categoryService;

    public BookController(BookService bookService, CategoryService categoryService) {
        this.bookService = bookService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listBooks(@RequestParam(required = false) String keyword,
                            @RequestParam(required = false) Long categoryId,
                            @RequestParam(required = false) BookStatus status,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            @RequestParam(defaultValue = "id") String sortBy,
                            @RequestParam(defaultValue = "desc") String sortDir,
                            Model model) {
        Page<Book> bookPage = bookService.searchBooks(keyword, categoryId, status, page, size, sortBy, sortDir);

        model.addAttribute("books", bookPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookPage.getTotalPages());
        model.addAttribute("totalElements", bookPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("status", status);
        model.addAttribute("categories", categoryService.getActiveCategories());
        model.addAttribute("statuses", BookStatus.values());

        return "books/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("bookForm", new BookForm());
        model.addAttribute("categories", categoryService.getActiveCategories());
        model.addAttribute("statuses", BookStatus.values());
        model.addAttribute("isEdit", false);
        return "books/form";
    }

    @PostMapping
    public String createBook(@Valid @ModelAttribute("bookForm") BookForm form,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getActiveCategories());
            model.addAttribute("statuses", BookStatus.values());
            model.addAttribute("isEdit", false);
            return "books/form";
        }

        try {
            bookService.createBook(form);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm sách mới thành công!");
            return "redirect:/books";
        } catch (DuplicateResourceException ex) {
            bindingResult.rejectValue("isbn", "duplicate", ex.getMessage());
            model.addAttribute("categories", categoryService.getActiveCategories());
            model.addAttribute("statuses", BookStatus.values());
            model.addAttribute("isEdit", false);
            return "books/form";
        } catch (BusinessRuleException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("categories", categoryService.getActiveCategories());
            model.addAttribute("statuses", BookStatus.values());
            model.addAttribute("isEdit", false);
            return "books/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id);
        BookForm form = new BookForm(
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getCategory().getId(),
                book.getTotalQuantity(),
                book.getAvailableQuantity(),
                book.getStatus()
        );

        model.addAttribute("bookForm", form);
        model.addAttribute("categories", categoryService.getActiveCategories());
        model.addAttribute("statuses", BookStatus.values());
        model.addAttribute("isEdit", true);
        model.addAttribute("bookId", id);
        return "books/form";
    }

    @PostMapping("/{id}")
    public String updateBook(@PathVariable Long id,
                             @Valid @ModelAttribute("bookForm") BookForm form,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getActiveCategories());
            model.addAttribute("statuses", BookStatus.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("bookId", id);
            return "books/form";
        }

        try {
            bookService.updateBook(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật sách thành công!");
            return "redirect:/books";
        } catch (DuplicateResourceException ex) {
            bindingResult.rejectValue("isbn", "duplicate", ex.getMessage());
            model.addAttribute("categories", categoryService.getActiveCategories());
            model.addAttribute("statuses", BookStatus.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("bookId", id);
            return "books/form";
        } catch (BusinessRuleException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("categories", categoryService.getActiveCategories());
            model.addAttribute("statuses", BookStatus.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("bookId", id);
            return "books/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookService.deleteBook(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa sách thành công!");
        } catch (BusinessRuleException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/books";
    }
}
