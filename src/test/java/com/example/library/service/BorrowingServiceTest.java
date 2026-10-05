package com.example.library.service;

import com.example.library.dto.form.BorrowingCreateForm;
import com.example.library.dto.form.BorrowingItemForm;
import com.example.library.dto.form.ReturnForm;
import com.example.library.dto.form.ReturnItemForm;
import com.example.library.entity.Book;
import com.example.library.entity.Borrowing;
import com.example.library.entity.BorrowingDetail;
import com.example.library.entity.Category;
import com.example.library.entity.Member;
import com.example.library.entity.enums.BookStatus;
import com.example.library.entity.enums.BorrowingStatus;
import com.example.library.entity.enums.MemberStatus;
import com.example.library.exception.BusinessRuleException;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowingDetailRepository;
import com.example.library.repository.BorrowingRepository;
import com.example.library.repository.MemberRepository;
import com.example.library.service.impl.BorrowingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowingServiceTest {

    @Mock
    private BorrowingRepository borrowingRepository;

    @Mock
    private BorrowingDetailRepository borrowingDetailRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BorrowingServiceImpl borrowingService;

    private Member activeMember;
    private Member blockedMember;
    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        Category category = new Category(1L, "CNTT", "IT", true);

        activeMember = new Member(1L, "Nguyễn Văn A", "vana@gmail.com", "0901234567", MemberStatus.ACTIVE);
        blockedMember = new Member(2L, "Trần Văn B", "vanb@gmail.com", "0912345678", MemberStatus.BLOCKED);

        book1 = new Book(1L, "ISBN-1", "Clean Code", "Robert Martin", category, 10, 5, BookStatus.ACTIVE);
        book2 = new Book(2L, "ISBN-2", "Refactoring", "Martin Fowler", category, 5, 2, BookStatus.ACTIVE);
    }

    @Test
    @DisplayName("Tạo phiếu mượn thành công: trừ số lượng tồn kho, hạn trả là 14 ngày")
    void testCreateBorrowing_Success() {
        LocalDate borrowDate = LocalDate.of(2026, 10, 1);
        List<BorrowingItemForm> items = List.of(
                new BorrowingItemForm(1L, 2),
                new BorrowingItemForm(2L, 1)
        );
        BorrowingCreateForm form = new BorrowingCreateForm(1L, borrowDate, items);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(activeMember));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book2));
        when(borrowingRepository.save(any(Borrowing.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Borrowing created = borrowingService.createBorrowing(form);

        assertNotNull(created);
        assertEquals(BorrowingStatus.BORROWED, created.getStatus());
        assertEquals(LocalDate.of(2026, 10, 15), created.getDueDate());
        assertEquals(2, created.getDetails().size());

        // Kiểm tra tồn kho bị trừ
        assertEquals(3, book1.getAvailableQuantity()); // 5 - 2 = 3
        assertEquals(1, book2.getAvailableQuantity()); // 2 - 1 = 1

        verify(bookRepository, times(2)).save(any(Book.class));
        verify(borrowingRepository).save(any(Borrowing.class));
    }

    @Test
    @DisplayName("Tạo phiếu mượn thất bại khi thành viên bị khóa (BLOCKED)")
    void testCreateBorrowing_MemberBlocked() {
        BorrowingCreateForm form = new BorrowingCreateForm(2L, LocalDate.now(), List.of(new BorrowingItemForm(1L, 1)));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(blockedMember));

        assertThrows(BusinessRuleException.class, () -> borrowingService.createBorrowing(form));
        verify(borrowingRepository, never()).save(any(Borrowing.class));
    }

    @Test
    @DisplayName("Tạo phiếu mượn thất bại khi số lượng sách sẵn có không đủ")
    void testCreateBorrowing_InsufficientAvailableQuantity() {
        // Yêu cầu mượn 10 cuốn trong khi book1 chỉ còn 5 cuốn
        BorrowingCreateForm form = new BorrowingCreateForm(1L, LocalDate.now(), List.of(new BorrowingItemForm(1L, 10)));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(activeMember));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));

        assertThrows(BusinessRuleException.class, () -> borrowingService.createBorrowing(form));
        verify(borrowingRepository, never()).save(any(Borrowing.class));
    }

    @Test
    @DisplayName("Trả sách một phần đúng hạn: cộng tồn kho, chưa chuyển sang RETURNED, không phạt")
    void testReturnBooks_PartialOnTime() {
        Borrowing borrowing = new Borrowing(1L, activeMember, LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 15), null, BorrowingStatus.BORROWED, BigDecimal.ZERO);
        BorrowingDetail detail1 = new BorrowingDetail(101L, borrowing, book1, 3, 0, BigDecimal.ZERO);
        borrowing.addDetail(detail1);

        when(borrowingRepository.findById(1L)).thenReturn(Optional.of(borrowing));
        when(borrowingRepository.save(any(Borrowing.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Trả 1 cuốn trước hạn (ngày 10/10)
        ReturnItemForm itemForm = new ReturnItemForm(101L, 1L, "Clean Code", "ISBN-1", 3, 0, 3, 1);
        ReturnForm form = new ReturnForm(1L, "Nguyễn Văn A", "vana@gmail.com", LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 15), LocalDate.of(2026, 10, 10), List.of(itemForm));

        Borrowing result = borrowingService.returnBooks(1L, form);

        assertEquals(BorrowingStatus.BORROWED, result.getStatus());
        assertEquals(1, detail1.getReturnedQuantity());
        assertEquals(2, detail1.getRemainingQuantity());
        assertEquals(BigDecimal.ZERO, detail1.getFineAmount());
        assertEquals(BigDecimal.ZERO, result.getTotalFine());
        assertEquals(6, book1.getAvailableQuantity()); // 5 + 1 = 6
    }

    @Test
    @DisplayName("Trả sách quá hạn: tính phạt 5.000 đ/ngày cho mỗi cuốn và cập nhật trạng thái RETURNED khi trả hết")
    void testReturnBooks_OverdueWithFineAndComplete() {
        // Hạn trả: 2026-09-10. Trả ngày: 2026-09-13 (Quá hạn 3 ngày)
        // Mượn 2 cuốn, trả cả 2 cuốn. Tiền phạt: 3 ngày * 2 cuốn * 5.000 = 30.000 đ
        Borrowing borrowing = new Borrowing(1L, activeMember, LocalDate.of(2026, 8, 27), LocalDate.of(2026, 9, 10), null, BorrowingStatus.BORROWED, BigDecimal.ZERO);
        BorrowingDetail detail = new BorrowingDetail(101L, borrowing, book1, 2, 0, BigDecimal.ZERO);
        borrowing.addDetail(detail);

        when(borrowingRepository.findById(1L)).thenReturn(Optional.of(borrowing));
        when(borrowingRepository.save(any(Borrowing.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReturnItemForm itemForm = new ReturnItemForm(101L, 1L, "Clean Code", "ISBN-1", 2, 0, 2, 2);
        ReturnForm form = new ReturnForm(1L, "Nguyễn Văn A", "vana@gmail.com", LocalDate.of(2026, 8, 27), LocalDate.of(2026, 9, 10), LocalDate.of(2026, 9, 13), List.of(itemForm));

        Borrowing result = borrowingService.returnBooks(1L, form);

        assertEquals(BorrowingStatus.RETURNED, result.getStatus());
        assertEquals(LocalDate.of(2026, 9, 13), result.getReturnedDate());
        assertEquals(2, detail.getReturnedQuantity());
        assertEquals(0, detail.getRemainingQuantity());
        assertEquals(BigDecimal.valueOf(30000L), detail.getFineAmount());
        assertEquals(BigDecimal.valueOf(30000L), result.getTotalFine());
        assertEquals(7, book1.getAvailableQuantity()); // 5 + 2 = 7
    }
}
