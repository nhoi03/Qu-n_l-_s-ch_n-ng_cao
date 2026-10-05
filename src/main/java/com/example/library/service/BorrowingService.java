package com.example.library.service;

import com.example.library.dto.form.BorrowingCreateForm;
import com.example.library.dto.form.ReturnForm;
import com.example.library.entity.Borrowing;
import com.example.library.entity.enums.BorrowingStatus;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface BorrowingService {
    Page<Borrowing> searchBorrowings(String keyword, BorrowingStatus status, int page, int size);
    Borrowing getBorrowingById(Long id);
    Borrowing createBorrowing(BorrowingCreateForm form);
    Borrowing returnBooks(Long borrowingId, ReturnForm form);
    Borrowing returnAllBooks(Long borrowingId, LocalDate returnDate);
    ReturnForm prepareReturnForm(Long borrowingId);
}
