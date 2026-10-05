package com.example.library.dto.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class BorrowingItemForm {

    @NotNull(message = "Vui lòng chọn sách")
    private Long bookId;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng mượn tối thiểu là 1")
    private Integer quantity = 1;

    public BorrowingItemForm() {
    }

    public BorrowingItemForm(Long bookId, Integer quantity) {
        this.bookId = bookId;
        this.quantity = quantity;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
