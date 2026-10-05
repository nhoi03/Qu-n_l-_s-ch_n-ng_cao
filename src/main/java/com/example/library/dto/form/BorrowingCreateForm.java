package com.example.library.dto.form;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BorrowingCreateForm {

    @NotNull(message = "Vui lòng chọn thành viên")
    private Long memberId;

    @NotNull(message = "Ngày mượn không được để trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate borrowDate = LocalDate.now();

    @NotEmpty(message = "Phiếu mượn phải có ít nhất một cuốn sách")
    @Valid
    private List<BorrowingItemForm> items = new ArrayList<>();

    public BorrowingCreateForm() {
        this.items.add(new BorrowingItemForm());
    }

    public BorrowingCreateForm(Long memberId, LocalDate borrowDate, List<BorrowingItemForm> items) {
        this.memberId = memberId;
        this.borrowDate = borrowDate != null ? borrowDate : LocalDate.now();
        this.items = items != null ? items : new ArrayList<>();
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public List<BorrowingItemForm> getItems() {
        return items;
    }

    public void setItems(List<BorrowingItemForm> items) {
        this.items = items;
    }
}
