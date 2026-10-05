package com.example.library.service;

import com.example.library.dto.form.BookForm;
import com.example.library.entity.Book;
import com.example.library.entity.Category;
import com.example.library.entity.enums.BookStatus;
import com.example.library.exception.BusinessRuleException;
import com.example.library.exception.DuplicateResourceException;
import com.example.library.repository.BookRepository;
import com.example.library.repository.CategoryRepository;
import com.example.library.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Category category;
    private Book book;

    @BeforeEach
    void setUp() {
        category = new Category(1L, "Công nghệ thông tin", "Sách IT", true);
        book = new Book(1L, "978-0132350884", "Clean Code", "Robert C. Martin", category, 10, 10, BookStatus.ACTIVE);
    }

    @Test
    @DisplayName("Tạo sách mới thành công và khởi tạo số lượng sẵn có bằng tổng số lượng")
    void testCreateBook_Success() {
        BookForm form = new BookForm(null, "978-0134685991", "Effective Java", "Joshua Bloch", 1L, 5, 5, BookStatus.ACTIVE);

        when(bookRepository.existsByIsbn("978-0134685991")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Book created = bookService.createBook(form);

        assertNotNull(created);
        assertEquals("Effective Java", created.getTitle());
        assertEquals(5, created.getTotalQuantity());
        assertEquals(5, created.getAvailableQuantity());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    @DisplayName("Tạo sách thất bại nếu ISBN bị trùng")
    void testCreateBook_DuplicateIsbn() {
        BookForm form = new BookForm(null, "978-0132350884", "Clean Code", "Robert C. Martin", 1L, 10, 10, BookStatus.ACTIVE);
        when(bookRepository.existsByIsbn("978-0132350884")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> bookService.createBook(form));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Không cho phép xóa sách khi sách đang có lượt mượn chưa trả")
    void testDeleteBook_ActiveBorrowingExists() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.hasActiveBorrowing(1L)).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> bookService.deleteBook(1L));
        assertEquals(BookStatus.ACTIVE, book.getStatus());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Xóa sách thành công khi không có lượt mượn chưa trả")
    void testDeleteBook_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.hasActiveBorrowing(1L)).thenReturn(false);

        bookService.deleteBook(1L);

        assertEquals(BookStatus.DELETED, book.getStatus());
        verify(bookRepository).save(book);
    }
}
