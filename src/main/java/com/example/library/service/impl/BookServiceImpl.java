package com.example.library.service.impl;

import com.example.library.dto.form.BookForm;
import com.example.library.entity.Book;
import com.example.library.entity.Category;
import com.example.library.entity.enums.BookStatus;
import com.example.library.exception.BusinessRuleException;
import com.example.library.exception.DuplicateResourceException;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.repository.BookRepository;
import com.example.library.repository.CategoryRepository;
import com.example.library.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    public BookServiceImpl(BookRepository bookRepository, CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Book> searchBooks(String keyword, Long categoryId, BookStatus status, int page, int size, String sortBy, String sortDir) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortProperty = (sortBy != null && !sortBy.trim().isEmpty()) ? sortBy.trim() : "createdAt";
        if (!List.of("id", "title", "author", "isbn", "createdAt", "totalQuantity", "availableQuantity").contains(sortProperty)) {
            sortProperty = "id";
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortProperty));
        String cleanKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;

        return bookRepository.searchBooks(cleanKeyword, categoryId, status, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> getAvailableBooksForBorrowing() {
        return bookRepository.findByStatusAndAvailableQuantityGreaterThan(BookStatus.ACTIVE, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách với ID: " + id));
    }

    @Override
    public Book createBook(BookForm form) {
        String isbn = form.getIsbn().trim();
        if (bookRepository.existsByIsbn(isbn)) {
            throw new DuplicateResourceException("Mã ISBN '" + isbn + "' đã tồn tại");
        }

        Category category = categoryRepository.findById(form.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thể loại với ID: " + form.getCategoryId()));

        Book book = new Book();
        book.setIsbn(isbn);
        book.setTitle(form.getTitle().trim());
        book.setAuthor(form.getAuthor().trim());
        book.setCategory(category);
        book.setTotalQuantity(form.getTotalQuantity());
        book.setAvailableQuantity(form.getTotalQuantity());
        book.setStatus(form.getStatus() != null ? form.getStatus() : BookStatus.ACTIVE);

        return bookRepository.save(book);
    }

    @Override
    public Book updateBook(Long id, BookForm form) {
        Book book = getBookById(id);
        String isbn = form.getIsbn().trim();

        if (bookRepository.existsByIsbnAndIdNot(isbn, id)) {
            throw new DuplicateResourceException("Mã ISBN '" + isbn + "' đã được sử dụng bởi cuốn sách khác");
        }

        Category category = categoryRepository.findById(form.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thể loại với ID: " + form.getCategoryId()));

        int currentlyBorrowed = book.getTotalQuantity() - book.getAvailableQuantity();
        if (form.getTotalQuantity() < currentlyBorrowed) {
            throw new BusinessRuleException("Tổng số lượng không thể nhỏ hơn số lượng sách đang được mượn (" + currentlyBorrowed + " cuốn)");
        }

        int newAvailable = form.getTotalQuantity() - currentlyBorrowed;

        book.setIsbn(isbn);
        book.setTitle(form.getTitle().trim());
        book.setAuthor(form.getAuthor().trim());
        book.setCategory(category);
        book.setTotalQuantity(form.getTotalQuantity());
        book.setAvailableQuantity(newAvailable);
        if (form.getStatus() != null) {
            book.setStatus(form.getStatus());
        }

        return bookRepository.save(book);
    }

    @Override
    public void deleteBook(Long id) {
        Book book = getBookById(id);
        if (bookRepository.hasActiveBorrowing(id)) {
            throw new BusinessRuleException("Không thể xóa sách '" + book.getTitle() + "' vì sách đang có lượt mượn chưa trả");
        }

        book.setStatus(BookStatus.DELETED);
        bookRepository.save(book);
    }
}
