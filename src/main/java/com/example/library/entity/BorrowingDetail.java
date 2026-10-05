package com.example.library.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "borrowing_details")
public class BorrowingDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrowing_id", nullable = false)
    private Borrowing borrowing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "returned_quantity", nullable = false)
    private Integer returnedQuantity = 0;

    @Column(name = "fine_amount", nullable = false)
    private BigDecimal fineAmount = BigDecimal.ZERO;

    public BorrowingDetail() {
    }

    public BorrowingDetail(Long id, Borrowing borrowing, Book book, Integer quantity, Integer returnedQuantity, BigDecimal fineAmount) {
        this.id = id;
        this.borrowing = borrowing;
        this.book = book;
        this.quantity = quantity;
        this.returnedQuantity = returnedQuantity != null ? returnedQuantity : 0;
        this.fineAmount = fineAmount != null ? fineAmount : BigDecimal.ZERO;
    }

    public Integer getRemainingQuantity() {
        if (quantity == null) return 0;
        int returned = returnedQuantity != null ? returnedQuantity : 0;
        return Math.max(0, quantity - returned);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Borrowing getBorrowing() {
        return borrowing;
    }

    public void setBorrowing(Borrowing borrowing) {
        this.borrowing = borrowing;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
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

    public BigDecimal getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(BigDecimal fineAmount) {
        this.fineAmount = fineAmount;
    }
}
