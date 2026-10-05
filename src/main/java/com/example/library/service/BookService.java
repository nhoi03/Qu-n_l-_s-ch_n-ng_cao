package com.example.library.service;

import com.example.library.dto.form.BookForm;
import com.example.library.entity.Book;
import com.example.library.entity.enums.BookStatus;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BookService {
    Page<Book> searchBooks(String keyword, Long categoryId, BookStatus status, int page, int size, String sortBy, String sortDir);
    List<Book> getAvailableBooksForBorrowing();
    Book getBookById(Long id);
    Book createBook(BookForm form);
    Book updateBook(Long id, BookForm form);
    void deleteBook(Long id);
}
