package com.example.library.service;

import com.example.library.dto.report.MemberReportDto;
import com.example.library.dto.report.TopBookReportDto;
import com.example.library.entity.Borrowing;
import com.example.library.entity.enums.BorrowingStatus;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowingRepository;
import com.example.library.repository.CategoryRepository;
import com.example.library.repository.MemberRepository;
import com.example.library.service.impl.ReportServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private BorrowingRepository borrowingRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    @Test
    @DisplayName("Lấy danh sách phiếu mượn quá hạn")
    void testGetOverdueBorrowings() {
        Borrowing borrowing = new Borrowing();
        borrowing.setId(10L);
        borrowing.setStatus(BorrowingStatus.BORROWED);

        Page<Borrowing> page = new PageImpl<>(List.of(borrowing));
        when(borrowingRepository.findOverdueBorrowings(eq(LocalDate.now()), any(Pageable.class))).thenReturn(page);

        Page<Borrowing> result = reportService.getOverdueBorrowings(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(10L, result.getContent().get(0).getId());
    }

    @Test
    @DisplayName("Lấy top 5 sách được mượn nhiều nhất")
    void testGetTop5BorrowedBooks() {
        TopBookReportDto dto = new TopBookReportDto(1L, "Clean Code", "Robert Martin", "ISBN-1", 15L);
        when(borrowingRepository.findTopBorrowedBooks(any(Pageable.class))).thenReturn(List.of(dto));

        List<TopBookReportDto> list = reportService.getTop5BorrowedBooks();

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(15L, list.get(0).getTotalBorrowed());
    }

    @Test
    @DisplayName("Lấy thống kê số lượt mượn theo thành viên")
    void testGetMemberBorrowingStats() {
        MemberReportDto dto = new MemberReportDto(1L, "Nguyễn Văn A", "vana@gmail.com", 3L, 5L);
        Page<MemberReportDto> page = new PageImpl<>(List.of(dto));
        when(borrowingRepository.findMemberBorrowingStats(any(Pageable.class))).thenReturn(page);

        Page<MemberReportDto> result = reportService.getMemberBorrowingStats(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(3L, result.getContent().get(0).getTotalBorrowings());
        assertEquals(5L, result.getContent().get(0).getTotalBooksBorrowed());
    }

    @Test
    @DisplayName("Lấy số liệu thống kê tổng quan cho dashboard")
    void testGetDashboardStatistics() {
        when(categoryRepository.count()).thenReturn(6L);
        when(bookRepository.count()).thenReturn(25L);
        when(memberRepository.count()).thenReturn(12L);
        when(memberRepository.findByStatusOrderByFullNameAsc(any())).thenReturn(List.of());
        when(borrowingRepository.findOverdueBorrowings(any(), any())).thenReturn(new PageImpl<>(List.of()));
        when(borrowingRepository.searchBorrowings(any(), eq(BorrowingStatus.BORROWED), any())).thenReturn(new PageImpl<>(List.of()));

        Map<String, Object> stats = reportService.getDashboardStatistics();

        assertNotNull(stats);
        assertEquals(6L, stats.get("totalCategories"));
        assertEquals(25L, stats.get("totalBooks"));
        assertEquals(12L, stats.get("totalMembers"));
    }
}
