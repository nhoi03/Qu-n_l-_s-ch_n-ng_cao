package com.example.library.controller;

import com.example.library.dto.form.BorrowingCreateForm;
import com.example.library.dto.form.BorrowingItemForm;
import com.example.library.dto.form.ReturnForm;
import com.example.library.dto.form.ReturnItemForm;
import com.example.library.entity.Book;
import com.example.library.entity.Borrowing;
import com.example.library.entity.Category;
import com.example.library.entity.Member;
import com.example.library.entity.enums.BookStatus;
import com.example.library.entity.enums.BorrowingStatus;
import com.example.library.entity.enums.MemberStatus;
import com.example.library.exception.BusinessRuleException;
import com.example.library.service.BookService;
import com.example.library.service.BorrowingService;
import com.example.library.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BorrowingController.class)
class BorrowingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BorrowingService borrowingService;

    @MockBean
    private MemberService memberService;

    @MockBean
    private BookService bookService;

    @Test
    @DisplayName("GET /borrowings - Hiển thị danh sách phiếu mượn")
    void testListBorrowings() throws Exception {
        when(borrowingService.searchBorrowings(any(), any(), eq(0), eq(10)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/borrowings"))
                .andExpect(status().isOk())
                .andExpect(view().name("borrowings/list"))
                .andExpect(model().attributeExists("borrowings", "currentPage", "totalPages"));
    }

    @Test
    @DisplayName("GET /borrowings/new - Hiển thị form tạo phiếu mượn")
    void testShowCreateForm() throws Exception {
        when(memberService.getActiveMembers()).thenReturn(List.of());
        when(bookService.getAvailableBooksForBorrowing()).thenReturn(List.of());

        mockMvc.perform(get("/borrowings/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("borrowings/form"))
                .andExpect(model().attributeExists("borrowingForm", "members", "books"));
    }

    @Test
    @DisplayName("POST /borrowings - Tạo phiếu mượn thành công và redirect")
    void testCreateBorrowing_Success() throws Exception {
        Borrowing borrowing = new Borrowing();
        borrowing.setId(100L);

        when(borrowingService.createBorrowing(any(BorrowingCreateForm.class))).thenReturn(borrowing);

        mockMvc.perform(post("/borrowings")
                        .param("memberId", "1")
                        .param("borrowDate", "2026-10-01")
                        .param("items[0].bookId", "1")
                        .param("items[0].quantity", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/borrowings/100"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @DisplayName("POST /borrowings - Lỗi nghiệp vụ khi tạo phiếu mượn trả về form với thông báo lỗi")
    void testCreateBorrowing_BusinessRuleException() throws Exception {
        when(memberService.getActiveMembers()).thenReturn(List.of());
        when(bookService.getAvailableBooksForBorrowing()).thenReturn(List.of());
        when(borrowingService.createBorrowing(any(BorrowingCreateForm.class)))
                .thenThrow(new BusinessRuleException("Sách không đủ số lượng tồn kho"));

        mockMvc.perform(post("/borrowings")
                        .param("memberId", "1")
                        .param("borrowDate", "2026-10-01")
                        .param("items[0].bookId", "1")
                        .param("items[0].quantity", "20"))
                .andExpect(status().isOk())
                .andExpect(view().name("borrowings/form"))
                .andExpect(model().attribute("errorMessage", "Sách không đủ số lượng tồn kho"));
    }

    @Test
    @DisplayName("GET /borrowings/{id} - Hiển thị chi tiết phiếu mượn")
    void testViewDetail() throws Exception {
        Member member = new Member(1L, "Nguyễn Văn A", "vana@gmail.com", "0901234567", MemberStatus.ACTIVE);
        Borrowing borrowing = new Borrowing(1L, member, LocalDate.now(), LocalDate.now().plusDays(14), null, BorrowingStatus.BORROWED, BigDecimal.ZERO);

        when(borrowingService.getBorrowingById(1L)).thenReturn(borrowing);

        mockMvc.perform(get("/borrowings/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("borrowings/detail"))
                .andExpect(model().attributeExists("borrowing", "today"));
    }

    @Test
    @DisplayName("POST /borrowings/{id}/return-all - Trả toàn bộ sách")
    void testReturnAll() throws Exception {
        Borrowing borrowing = new Borrowing();
        borrowing.setId(1L);

        when(borrowingService.returnAllBooks(eq(1L), any(LocalDate.class))).thenReturn(borrowing);

        mockMvc.perform(post("/borrowings/1/return-all"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/borrowings/1"))
                .andExpect(flash().attributeExists("successMessage"));
    }
}
