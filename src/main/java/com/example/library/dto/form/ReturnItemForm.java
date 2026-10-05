package com.example.library.dto.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ReturnItemForm {

    private Long detailId;
    private Long bookId;
    private String bookTitle;
    private String bookIsbn;
    private Integer quantity;
    private Integer returnedQuantity;
    private Integer remainingQuantity;

    @NotNull(message = "Số lượng trả không được để trống")
    @Min(value = 0, message = "Số lượng trả không được âm")
    private Integer returnQuantity = 0;

    public ReturnItemForm() {
    }

    public ReturnItemForm(Long detailId, Long bookId, String bookTitle, String bookIsbn, Integer quantity, Integer returnedQuantity, Integer remainingQuantity, Integer returnQuantity) {
        this.detailId = detailId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.bookIsbn = bookIsbn;
        this.quantity = quantity;
        this.returnedQuantity = returnedQuantity;
        this.remainingQuantity = remainingQuantity;
        this.returnQuantity = returnQuantity != null ? returnQuantity : 0;
    }

    public Long getDetailId() {
        return detailId;
    }

    public void setDetailId(Long detailId) {
        this.detailId = detailId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public void setBookIsbn(String bookIsbn) {
        this.bookIsbn = bookIsbn;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getReturnedQuantity() {
        return returnedQuantity;
    }

    public void setReturnedQuantity(Integer returnedQuantity) {
        this.returnedQuantity = returnedQuantity;
    }

    public Integer getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(Integer remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    public Integer getReturnQuantity() {
        return returnQuantity;
    }

    public void setReturnQuantity(Integer returnQuantity) {
        this.returnQuantity = returnQuantity;
    }
}
