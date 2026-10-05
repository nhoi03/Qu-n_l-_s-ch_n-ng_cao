package com.example.library.service;

import com.example.library.dto.report.MemberReportDto;
import com.example.library.dto.report.TopBookReportDto;
import com.example.library.entity.Borrowing;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ReportService {
    Page<Borrowing> getOverdueBorrowings(int page, int size);
    List<TopBookReportDto> getTop5BorrowedBooks();
    Page<MemberReportDto> getMemberBorrowingStats(int page, int size);
    Map<String, Object> getDashboardStatistics();
}
