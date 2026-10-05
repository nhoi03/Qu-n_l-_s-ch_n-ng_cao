package com.example.library.repository;

import com.example.library.dto.report.MemberReportDto;
import com.example.library.dto.report.TopBookReportDto;
import com.example.library.entity.Borrowing;
import com.example.library.entity.enums.BorrowingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {

    @Query("SELECT b FROM Borrowing b WHERE " +
           "(:status IS NULL OR b.status = :status) AND " +
           "(:keyword IS NULL OR LOWER(b.member.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(b.member.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(b.member.phone) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Borrowing> searchBorrowings(@Param("keyword") String keyword,
                                     @Param("status") BorrowingStatus status,
                                     Pageable pageable);

    @Query("SELECT b FROM Borrowing b WHERE b.status = com.example.library.entity.enums.BorrowingStatus.BORROWED " +
           "AND b.dueDate < :today ORDER BY b.dueDate ASC")
    Page<Borrowing> findOverdueBorrowings(@Param("today") LocalDate today, Pageable pageable);

    @Query("SELECT new com.example.library.dto.report.TopBookReportDto(bd.book.id, bd.book.title, bd.book.author, bd.book.isbn, SUM(CAST(bd.quantity AS long))) " +
           "FROM BorrowingDetail bd GROUP BY bd.book.id, bd.book.title, bd.book.author, bd.book.isbn ORDER BY SUM(CAST(bd.quantity AS long)) DESC")
    List<TopBookReportDto> findTopBorrowedBooks(Pageable pageable);

    @Query(value = "SELECT new com.example.library.dto.report.MemberReportDto(m.id, m.fullName, m.email, COUNT(DISTINCT b.id), COALESCE(SUM(CAST(bd.quantity AS long)), 0L)) " +
           "FROM Member m LEFT JOIN Borrowing b ON b.member.id = m.id LEFT JOIN b.details bd GROUP BY m.id, m.fullName, m.email ORDER BY COUNT(DISTINCT b.id) DESC",
           countQuery = "SELECT COUNT(m) FROM Member m")
    Page<MemberReportDto> findMemberBorrowingStats(Pageable pageable);
}
