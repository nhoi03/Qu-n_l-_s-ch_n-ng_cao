package com.example.library.service.impl;

import com.example.library.dto.report.MemberReportDto;
import com.example.library.dto.report.TopBookReportDto;
import com.example.library.entity.Borrowing;
import com.example.library.entity.enums.BookStatus;
import com.example.library.entity.enums.BorrowingStatus;
import com.example.library.entity.enums.MemberStatus;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowingRepository;
import com.example.library.repository.CategoryRepository;
import com.example.library.repository.MemberRepository;
import com.example.library.service.ReportService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final BorrowingRepository borrowingRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;

    public ReportServiceImpl(BorrowingRepository borrowingRepository,
                             BookRepository bookRepository,
                             MemberRepository memberRepository,
                             CategoryRepository categoryRepository) {
        this.borrowingRepository = borrowingRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Page<Borrowing> getOverdueBorrowings(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return borrowingRepository.findOverdueBorrowings(LocalDate.now(), pageable);
    }

    @Override
    public List<TopBookReportDto> getTop5BorrowedBooks() {
        Pageable top5 = PageRequest.of(0, 5);
        return borrowingRepository.findTopBorrowedBooks(top5);
    }

    @Override
    public Page<MemberReportDto> getMemberBorrowingStats(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return borrowingRepository.findMemberBorrowingStats(pageable);
    }

    @Override
    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> stats = new HashMap<>();

        long totalCategories = categoryRepository.count();
        long totalBooks = bookRepository.count();
        long totalMembers = memberRepository.count();
        long activeMembers = memberRepository.findByStatusOrderByFullNameAsc(MemberStatus.ACTIVE).size();

        Page<Borrowing> overduePage = borrowingRepository.findOverdueBorrowings(LocalDate.now(), PageRequest.of(0, 1));
        long overdueCount = overduePage.getTotalElements();

        Page<Borrowing> activeBorrowingsPage = borrowingRepository.searchBorrowings(null, BorrowingStatus.BORROWED, PageRequest.of(0, 1));
        long activeBorrowingsCount = activeBorrowingsPage.getTotalElements();

        stats.put("totalCategories", totalCategories);
        stats.put("totalBooks", totalBooks);
        stats.put("totalMembers", totalMembers);
        stats.put("activeMembers", activeMembers);
        stats.put("overdueBorrowingsCount", overdueCount);
        stats.put("activeBorrowingsCount", activeBorrowingsCount);

        return stats;
    }
}
