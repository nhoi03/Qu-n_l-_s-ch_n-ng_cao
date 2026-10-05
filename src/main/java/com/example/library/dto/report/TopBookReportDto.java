package com.example.library.dto.report;

public class TopBookReportDto {
    private Long bookId;
    private String title;
    private String author;
    private String isbn;
    private Long totalBorrowed;

    public TopBookReportDto() {
    }

    public TopBookReportDto(Long bookId, String title, String author, String isbn, Long totalBorrowed) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.totalBorrowed = totalBorrowed;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Long getTotalBorrowed() {
        return totalBorrowed;
    }

    public void setTotalBorrowed(Long totalBorrowed) {
        this.totalBorrowed = totalBorrowed;
    }
}
