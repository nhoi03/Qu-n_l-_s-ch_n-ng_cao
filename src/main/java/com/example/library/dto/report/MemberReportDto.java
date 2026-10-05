package com.example.library.dto.report;

public class MemberReportDto {
    private Long memberId;
    private String fullName;
    private String email;
    private Long totalBorrowings;
    private Long totalBooksBorrowed;

    public MemberReportDto() {
    }

    public MemberReportDto(Long memberId, String fullName, String email, Long totalBorrowings, Long totalBooksBorrowed) {
        this.memberId = memberId;
        this.fullName = fullName;
        this.email = email;
        this.totalBorrowings = totalBorrowings;
        this.totalBooksBorrowed = totalBooksBorrowed;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getTotalBorrowings() {
        return totalBorrowings;
    }

    public void setTotalBorrowings(Long totalBorrowings) {
        this.totalBorrowings = totalBorrowings;
    }

    public Long getTotalBooksBorrowed() {
        return totalBooksBorrowed;
    }

    public void setTotalBooksBorrowed(Long totalBooksBorrowed) {
        this.totalBooksBorrowed = totalBooksBorrowed;
    }
}
