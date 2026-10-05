package com.example.library.repository;

import com.example.library.entity.Book;
import com.example.library.entity.enums.BookStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);

    boolean existsByIsbnAndIdNot(String isbn, Long id);

    long countByCategoryId(Long categoryId);

    List<Book> findByStatusAndAvailableQuantityGreaterThan(BookStatus status, Integer quantity);

    @Query("SELECT b FROM Book b WHERE " +
           "(:keyword IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:categoryId IS NULL OR b.category.id = :categoryId) " +
           "AND (:status IS NULL OR b.status = :status)")
    Page<Book> searchBooks(@Param("keyword") String keyword,
                           @Param("categoryId") Long categoryId,
                           @Param("status") BookStatus status,
                           Pageable pageable);

    @Query("SELECT COUNT(bd) > 0 FROM BorrowingDetail bd WHERE bd.book.id = :bookId AND bd.quantity > bd.returnedQuantity")
    boolean hasActiveBorrowing(@Param("bookId") Long bookId);
}
