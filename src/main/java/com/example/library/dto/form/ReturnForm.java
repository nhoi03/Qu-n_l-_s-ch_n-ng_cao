package com.example.library.dto.form;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReturnForm {

    @NotNull(message = "ID phiếu mượn không hợp lệ")
    private Long borrowingId;

    private String memberFullName;
    private String memberEmail;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate borrowDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate returnDate = LocalDate.now();

    @Valid
    private List<ReturnItemForm> items = new ArrayList<>();

    public ReturnForm() {
    }

    public ReturnForm(Long borrowingId, String memberFullName, String memberEmail, LocalDate borrowDate, LocalDate dueDate, LocalDate returnDate, List<ReturnItemForm> items) {
        this.borrowingId = borrowingId;
        this.memberFullName = memberFullName;
        this.memberEmail = memberEmail;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate != null ? returnDate : LocalDate.now();
        this.items = items != null ? items : new ArrayList<>();
    }

    public Long getBorrowingId() {
        return borrowingId;
    }

    public void setBorrowingId(Long borrowingId) {
        this.borrowingId = borrowingId;
    }

    public String getMemberFullName() {
        return memberFullName;
    }

    public void setMemberFullName(String memberFullName) {
        this.memberFullName = memberFullName;
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public List<ReturnItemForm> getItems() {
        return items;
    }

    public void setItems(List<ReturnItemForm> items) {
        this.items = items;
    }
}
