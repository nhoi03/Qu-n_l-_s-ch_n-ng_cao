package com.example.library.controller;

import com.example.library.dto.report.MemberReportDto;
import com.example.library.dto.report.TopBookReportDto;
import com.example.library.entity.Borrowing;
import com.example.library.service.ReportService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public String dashboard(Model model) {
        Map<String, Object> stats = reportService.getDashboardStatistics();
        List<TopBookReportDto> topBooks = reportService.getTop5BorrowedBooks();
        Page<Borrowing> overduePage = reportService.getOverdueBorrowings(0, 5);

        model.addAttribute("stats", stats);
        model.addAttribute("topBooks", topBooks);
        model.addAttribute("overdueBorrowings", overduePage.getContent());
        model.addAttribute("today", LocalDate.now());

        return "reports/index";
    }

    @GetMapping("/overdue")
    public String overdueReport(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                Model model) {
        Page<Borrowing> overduePage = reportService.getOverdueBorrowings(page, size);

        model.addAttribute("overdueBorrowings", overduePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", overduePage.getTotalPages());
        model.addAttribute("totalElements", overduePage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("today", LocalDate.now());

        return "reports/overdue";
    }

    @GetMapping("/top-books")
    public String topBooksReport(Model model) {
        List<TopBookReportDto> topBooks = reportService.getTop5BorrowedBooks();
        model.addAttribute("topBooks", topBooks);
        return "reports/top-books";
    }

    @GetMapping("/member-stats")
    public String memberStatsReport(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    Model model) {
        Page<MemberReportDto> memberStatsPage = reportService.getMemberBorrowingStats(page, size);

        model.addAttribute("memberStats", memberStatsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", memberStatsPage.getTotalPages());
        model.addAttribute("totalElements", memberStatsPage.getTotalElements());
        model.addAttribute("size", size);

        return "reports/member-stats";
    }
}
